package com.flatfisk.gnomp.engine;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.abstracts.AbstractNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;


/**
 * Created by Vemund Kvam on 16/01/16.
 */
public class GnompEngine extends PooledEngine {

    private Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);
    private ConstructorManager constructorManager;

    protected Array<Entity> entitiesToConstruct = new Array<Entity>(2);
    protected Array<Entity> entitiesConstructed = new Array<Entity>(2);
    protected Array<EntityReconstructOperation> entitiesToReconstruct = new Array<EntityReconstructOperation>(2);

    public GnompEngine() {
        this(10, 1000, 10, 1000);
    }

    public GnompEngine(int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize) {
        super(entityPoolInitialSize, entityPoolMaxSize, componentPoolInitialSize, componentPoolMaxSize);
        constructorManager = new ConstructorManager(this);
    }

    public ConstructorManager getConstructorManager() {
        return constructorManager;
    }

    /**
     * Creates and adds a new entity
     *
     * @return created entity added to engine
     */
    public Entity addEntity() {
        Entity entity = super.createEntity();
        addEntity(entity);
        return entity;
    }

    /**
     * Removes all children of this entity, then removes the entity from the engine
     *
     * @param entity to be removed
     */
    public void removeEntity(Entity entity) {
        removeFromParents(entity);
        super.removeEntity(entity);
    }

    /**
     * Constructs an entity or the nearest descendant that has one or more constructors
     *
     * @param entity to be constructed
     */
    public void constructEntity(Entity entity) {
        entitiesToConstruct.add(entity);
    }

    /**
     * Reconstructs an entity
     * <p>
     * Constructors, specified by class, will be rebuilt
     * Containers without any matching constructors, will be removed
     *
     * @param entity             to reconstruct
     * @param constructorClasses component classes to use for reconstruction
     */
    public void reConstructEntity(Entity entity, Class<? extends Component>... constructorClasses) {
        EntityReconstructOperation def = new EntityReconstructOperation();
        def.entity = entity;
        def.toConstruct = constructorClasses;
        entitiesToReconstruct.add(def);
    }

    /**
     * Deprecated (use createComponent and addComponent instead, this ensures entityListeners work as expected)
     * Creates and adds component to entity.
     *
     * @param componentType to be created
     * @param entity        to add the created component to
     * @return the created component added to the entity
     */
    @Deprecated
    public <T extends Component> T addComponent(Class<T> componentType, Entity entity) {
        T component = createComponent(componentType, entity);
        entity.add(component);
        return component;
    }

    @Deprecated
    public <T extends Component> T createComponent(Class<T> componentType) {
        throw new UnsupportedOperationException(
                "\nThe createComponent method in PooledEngine will not call setOwner\n" +
                "on AbstractNodes, use createComponent(Class<T> componentType, Entity entity)\n"
        );
    }

    /**
     * Creates a component and sets the correct owner for Nodes
     *
     * @param componentType to be created
     * @param entity        to add the created component to
     * @return the created component added to the entity
     */
    public <T extends Component> T createComponent(Class<T> componentType, Entity entity) {
        T component = super.createComponent(componentType);
        if (component instanceof AbstractNode) {
            ((AbstractNode) component).setOwner(entity);
        }
        return component;
    }

    /**
     * Updates all the systems in this Engine, then constructs and rebuilds entities.
     *
     * @param deltaTime The time passed since the last frame.
     */
    public void update(float deltaTime) {
        super.update(deltaTime);
        constructEntities();
        reconstructsEntitiesForComponents();
    }

    private void removeFromParents(Entity entity) {
        for (Component c : entity.getComponents()) {
            if (c instanceof AbstractNode) {
                AbstractNode node = (AbstractNode) c;
                removeChildren(node.parent, entity, node.getClass());
            }
        }
    }

    private void removeChildren(Entity parent, Entity child, Class<? extends AbstractNode> nodeType) {

        if (parent != null) {
            AbstractNode node = parent.getComponent(nodeType);
            node.removeChild(child);
        }

        super.removeEntity(child);

        AbstractNode childNode = child.getComponent(nodeType);
        if (childNode != null && childNode.children != null) {
            Entity[] children = childNode.children.toArray(Entity.class);

            for (Entity entity : children) {
                removeChildren(child, entity, nodeType);
            }
        }
    }

    private void constructEntities() {
        if (entitiesToConstruct.size > 0) {
            Iterator<Entity> entitiesAddedIterator = entitiesToConstruct.iterator();
            while (entitiesAddedIterator.hasNext()) {
                Entity entity = entitiesAddedIterator.next();
                Entity constructor = constructorManager.getConstructor(entity, true);

                if (constructor != null && !entitiesConstructed.contains(constructor, false)) {
                    constructorManager.dismantleEntity(constructor);
                    constructorManager.constructEntity(constructor);
                    entitiesConstructed.add(constructor);
                }

                entitiesAddedIterator.remove();
            }
            entitiesConstructed.clear();
        }
    }

    private void reconstructsEntitiesForComponents() {
        if (entitiesToReconstruct.size > 0) {
            Iterator<EntityReconstructOperation> entitiesAddedIterator = entitiesToReconstruct.iterator();

            while (entitiesAddedIterator.hasNext()) {
                EntityReconstructOperation entity = entitiesAddedIterator.next();
                Entity constructor = constructorManager.getConstructor(entity.entity, true);
                // TODO: Why is the != null required: Because some entities doesn't have a constructor...
                if (constructor != null && !entitiesConstructed.contains(constructor, false)) {
                    constructorManager.reConstructEntity(constructor, entity.toConstruct);
                    entitiesConstructed.add(constructor);
                }

                entitiesAddedIterator.remove();
            }
            entitiesConstructed.clear();
        }
    }

    private static class EntityReconstructOperation {
        Entity entity;
        Class<? extends Component>[] toConstruct;

    }


}
