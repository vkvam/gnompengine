package com.flatfisk.gnomp.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Node;

public abstract class NodeSystem<T1 extends NodeSystem.IterateDTO, T3 extends Node> extends FamilySystem implements EntityListener {
    public ComponentMapper<T3> nodeMapper;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private Class<T3> childClass;

    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     * @param filter to match against entities
     */
    public NodeSystem(Family filter, Class<T3> childClass, int priority) {
        super(filter,priority);
        this.childClass = childClass;
    }

    @Override
    public void addedToEngine(Engine engine) {
        LOG.info("System added to engine");
        super.addedToEngine(engine);
        nodeMapper = ComponentMapper.getFor(childClass);
    }


    @Override
    public void entityAdded(Entity e) {
        LOG.info("Entity added to engine");
        if (getFamily().matches(e)) {
            LOG.info("Inserted root with id:" + e.hashCode());
            T1 dto = insertedRoot(e);
            insertedChildren(nodeMapper.get(e), dto);
            finalInsertedRoot(e, dto);
        }
    }

    private void insertedChildren(Node node, T1 dto) {
        if (node == null) {
            return;
        }
        for (Entity entity : node.children) {
            if (entity != null && !getFamily().matches(entity)) {
                LOG.info("Inserted child with id:" + entity.hashCode());
                T1 dto2 = insertedChild(entity, dto);
                insertedChildren(nodeMapper.get(entity), dto2);
            }
        }
    }

    /**
     * Called when a root entity is inserted.
     *
     * @param e the root entity
     * @return a dto that can be used by the children to inherit or collect data.
     */
    protected abstract T1 insertedRoot(Entity e);

    /**
     * Called when a child is inserted.
     *
     * @param e   the child entity
     * @param dto that is passed from the parent.
     * @return dto to pass to children.
     */
    protected abstract T1 insertedChild(Entity e, T1 dto);

    /**
     * Called when a hierarchy has finished inserting all nodes.
     *
     * @param e   the parent entity of the hierarchy.
     * @param dto the resulting dto from the processing of the whole hierarchy.
     */
    protected abstract void finalInsertedRoot(Entity e, T1 dto);

    @Override
    public void entityRemoved(Entity e) {
        if (getFamily().matches(e)) {
            removedParent(e);
            removedChildren(nodeMapper.get(e));
        } else if (nodeMapper.has(e)) {
            removedChild(e);
            removedChildren(nodeMapper.get(e));
        }
    }

    private void removedChildren(Node node) {
        if (node == null) {
            return;
        }
        for (Entity entity : node.children) {
            if (!getFamily().matches(entity)) {
                removedChild(entity);
                removedChildren(nodeMapper.get(entity));
            }
        }
    }

    protected void removedParent(Entity e) {

    }

    protected void removedChild(Entity e) {

    }

    public interface IterateDTO {

    }
}
