package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.flatfisk.gnomp.components.Node;

public abstract class IteratingNodeSystem<T1 extends NodeSystem.IterateDTO, T2 extends Component, T3 extends Node> extends NodeSystem<T1, T3> {

    public ComponentMapper<T2> parentNodeMapper;
    private Class<T2> parentClass;
    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     * @param filter to match against entities
     */
    public IteratingNodeSystem(Family filter, Class<T2> parentClass, Class<T3> childClass, int priority) {
        super(filter, childClass,priority);
        this.parentClass = parentClass;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        parentNodeMapper = ComponentMapper.getFor(parentClass);
    }



    @Override
    public void update (float deltaTime) {
        ImmutableArray<Entity> entities = getEntities();
        for (int i = 0; i < entities.size(); ++i) {
            Entity e = entities.get(i);
            T1 dto = processRoot(e);
            processChildren(nodeMapper.get(e),dto);
        }
    }

    private void processChildren(Node node, T1 dto) {
        if (node == null) {
            return;
        }
        for (Entity entity : node.children) {
            // If child entity is a parent, stop digging.
            if (!parentNodeMapper.has(entity)) {
                T1 dto2 = processChild(entity, dto);
                processChildren(nodeMapper.get(entity), dto2);
            }
        }
    }

    protected T1 processRoot(Entity e) {
        return null;
    }

    protected T1 processChild(Entity e, T1 dto) {
        return null;
    }

    @Override
    protected T1 insertedRoot(Entity e) {
        return null;
    }

    @Override
    protected void removedParent(Entity e) {
    }

}
