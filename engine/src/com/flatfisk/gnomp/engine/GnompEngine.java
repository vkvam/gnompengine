package com.flatfisk.gnomp.engine;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.abstracts.AbstractNode;

import java.util.Iterator;


/**
 * Created by Vemund Kvam on 16/01/16.
 */
public class GnompEngine extends PooledEngine {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ConstructorManager constructorManager;
    protected Array<Entity> entitiesToConstruct = new Array<Entity>(2);
    protected Array<Entity> entitiesConstructed = new Array<Entity>(2);

    protected Array<EntityReconstructDef> entitiesToReconstruct = new Array<EntityReconstructDef>(2);

    public GnompEngine () {
        this(10, 1000, 10, 1000);
    }

    public GnompEngine (int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize) {
        super(entityPoolInitialSize,entityPoolMaxSize,componentPoolInitialSize,componentPoolMaxSize);
        constructorManager  = new ConstructorManager(this);
    }

    public ConstructorManager getConstructorManager() {
        return constructorManager;
    }

    public void addEntity(Entity entity){
        super.addEntity(entity);
    }

    /**
     * Constructs or reconstructs the entity or the nearest descendant entity with one or more constructors.
     * @param entity
     */
    public void constructEntity(Entity entity){
        entitiesToConstruct.add(entity);
    }

    /**
     * Reconstruct an entity for the specified constructor components.
     * If some constructors are removed from the the entity, the Container will be removed.
     * @param entity
     */
    public void reConstructEntity(Entity entity, Class<? extends Component> ... components){
        EntityReconstructDef def = new EntityReconstructDef();
        def.entity = entity;
        def.toConstruct = components;
        entitiesToReconstruct.add(def);
    }

    public Entity createEntity () {
        Entity entity = super.createEntity();
        addEntity(entity);
        return entity;
    }

    public void removeEntity(Entity entity){
        removeFromParents(entity);
        super.removeEntity(entity);
    }

    public <T extends Component> T addComponent (Class<T> componentType, Entity entity) {
        T component = createComponent(componentType);
        if( component instanceof AbstractNode){
            ((AbstractNode) component).setOwner(entity);
        }
        entity.add(component);
        return component;
    }

    public void update(float f){
        super.update(f);
        constructEntities();
        reconstructsEntitiesForComponents();
    }

    private void removeFromParents(Entity entity){
        for(Component c : entity.getComponents()){
            if(c instanceof AbstractNode){
                AbstractNode node = (AbstractNode) c;
                removeChildren(node.parent,entity,node.getClass());
            }
        }
    }

    private void removeChildren(Entity parent, Entity child, Class<? extends AbstractNode> nodeType){

        if(parent!=null) {
            AbstractNode node = parent.getComponent(nodeType);
            node.removeChild(child);
        }

        super.removeEntity(child);

        AbstractNode childNode = child.getComponent(nodeType);
        if(childNode!=null && childNode.children!=null) {
            Entity[] children = childNode.children.toArray(Entity.class);

            for (Entity entity : children) {
                removeChildren(child, entity,nodeType);
            }
        }
    }

    private void constructEntities(){
        if(entitiesToConstruct.size>0) {
            Iterator<Entity> entitiesAddedIterator = entitiesToConstruct.iterator();
            while (entitiesAddedIterator.hasNext()) {
                Entity entity = entitiesAddedIterator.next();
                Entity constructor = constructorManager.getConstructor(entity,true);

                if(!entitiesConstructed.contains(constructor,false)){
                    constructorManager.dismantleEntity(constructor);
                    constructorManager.constructEntity(constructor);
                    entitiesConstructed.add(constructor);
                }

                entitiesAddedIterator.remove();
            }
            entitiesConstructed.clear();
        }
    }

    private void reconstructsEntitiesForComponents(){
        if(entitiesToReconstruct.size>0) {
            Iterator<EntityReconstructDef> entitiesAddedIterator = entitiesToReconstruct.iterator();

            while (entitiesAddedIterator.hasNext()) {
                EntityReconstructDef entity = entitiesAddedIterator.next();
                Entity constructor = constructorManager.getConstructor(entity.entity,true);

                if(!entitiesConstructed.contains(constructor,false)){
                    constructorManager.reConstructEntity(constructor,entity.toConstruct);
                    entitiesConstructed.add(constructor);
                }

                entitiesAddedIterator.remove();
            }
            entitiesConstructed.clear();
        }
    }

    public static class EntityReconstructDef{
        Entity entity;
        Class<? extends Component>[] toConstruct;

    }


}
