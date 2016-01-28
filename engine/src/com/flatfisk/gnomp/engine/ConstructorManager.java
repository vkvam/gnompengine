package com.flatfisk.gnomp.engine;

/**
 * Created by Vemund Kvam on 06/12/15.
 */

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.SortedIntList;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.constructors.SpatialConstructor;

public class ConstructorManager {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public ComponentMapper<Spatial.Node> spatialRelativeComponentMapper;
    public ComponentMapper<Spatial> spatialDefComponentMapper;

    public SortedIntList<com.flatfisk.gnomp.engine.constructors.Constructor> constructors;
    public Family rootFamily;

    public GnompEngine engine;
    public ConstructorManager(GnompEngine engine){
        rootFamily = Family.all(Spatial.class, Spatial.Node.class).get();

        spatialRelativeComponentMapper = ComponentMapper.getFor(Spatial.Node.class);
        spatialDefComponentMapper = ComponentMapper.getFor(Spatial.class);
        constructors = new SortedIntList<com.flatfisk.gnomp.engine.constructors.Constructor>();
        this.engine = engine;
    }

    public void addConstructor(com.flatfisk.gnomp.engine.constructors.Constructor constructor, int priority){
        constructors.insert(priority,constructor);
    }

    public void constructEntity(Entity entity) {

            Spatial.Node constructorOrientation = spatialRelativeComponentMapper.get(entity);
            for (SortedIntList.Node<com.flatfisk.gnomp.engine.constructors.Constructor> constructorNode : constructors) {
                com.flatfisk.gnomp.engine.constructors.Constructor constructor = constructorNode.value;
                parentAdded(constructor, entity, constructorOrientation);
            }
    }

    private void parentAdded(com.flatfisk.gnomp.engine.constructors.Constructor constructor, Entity entity, Spatial.Node rootOrientation){
        LOG.info("Parent added for constructor:"+constructor.getClass());
        Spatial.Node constructorOrientation = spatialRelativeComponentMapper.get(entity);
        Array<Entity> children = constructorOrientation.children;

        Object iterateDTO = constructor.parentAdded(entity, constructorOrientation);
        childrenAdded(children, constructor, rootOrientation, constructorOrientation, constructorOrientation, iterateDTO);
        constructor.parentAddedFinal(entity, constructorOrientation, iterateDTO);
    }

    private void childrenAdded(Array<Entity> children, com.flatfisk.gnomp.engine.constructors.Constructor constructor, Spatial.Node rootOrientation, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Object iterateDTO){
        for(Entity childWrapper : children) {
            LOG.info("Child for constructor:"+childWrapper);
            if(childWrapper!=null && childWrapper!=null) {
            Entity child = childWrapper;
                if (constructor.isChild(child)) {
                    Spatial.Node orientation = spatialRelativeComponentMapper.get(child);
                    LOG.info("Child added for constructor:" + constructor.getClass());

                    iterateDTO = constructor.insertedChild(child, constructorOrientation, parentOrientation, orientation, iterateDTO);
                    childrenAdded(orientation.children, constructor, rootOrientation, constructorOrientation, orientation, iterateDTO);

                } else if (constructor.isParent(child)) {
                    LOG.info("RECONSTRUCT: "+rootOrientation/*.reconstruct*/);
                    LOG.info("Parent added for constructor through child:" + constructor.getClass());

                    parentAdded(constructor, child, rootOrientation);

                }
            }
        }
    }


    public Entity getConstructor(Entity entity, boolean isParent) {

        boolean hasChildren = false;
        boolean hasConstructor = false;

        com.flatfisk.gnomp.engine.constructors.Constructor constructor;

        for (SortedIntList.Node<com.flatfisk.gnomp.engine.constructors.Constructor> constructorNode : constructors) {
            constructor = constructorNode.value;
            if(!(constructor instanceof SpatialConstructor)) {
                hasChildren = constructor.relationshipMapper.has(entity);
                hasConstructor = constructor.constructorMapper.has(entity);
            }
        }

        Spatial.Node rel = entity.getComponent(Spatial.Node.class);

        if(isParent&&hasConstructor){
            return entity;
        }else if(hasChildren && rel!=null &&rel.parent!=null){
            return getConstructor(rel.parent, true);
        }
        return null;
    }

    public Entity dismantleEntity(Entity constructorEntity){

        for (SortedIntList.Node<com.flatfisk.gnomp.engine.constructors.Constructor> constructorNode : constructors) {
            com.flatfisk.gnomp.engine.constructors.Constructor constructor = constructorNode.value;
            if(constructorEntity!=null) {
                parentRemoved(constructor, constructorEntity);
            }
        }
        return constructorEntity;
    }

    private void parentRemoved(com.flatfisk.gnomp.engine.constructors.Constructor constructor, Entity entity){
        LOG.info("Parent removed for constructor:"+constructor.getClass());
        Spatial.Node constructorOrientation = spatialRelativeComponentMapper.get(entity);
        Array<Entity> children = constructorOrientation.children;

        constructor.parentRemoved(entity);
        childrenRemoved(children, constructor);
    }


    private void childrenRemoved(Array<Entity> children, com.flatfisk.gnomp.engine.constructors.Constructor constructor){
        for(Entity childWrapper : children) {
            LOG.info("Child for constructor:"+childWrapper);
            if(childWrapper!=null && childWrapper!=null) {
                Entity child = childWrapper;
                if (constructor.isChild(child)) {
                    Spatial.Node orientation = spatialRelativeComponentMapper.get(child);
                    LOG.info("Child added for constructor:" + constructor.getClass());

                    constructor.childRemoved(child);
                    childrenRemoved(orientation.children, constructor);

                } else if (constructor.isParent(child)) {
                    parentRemoved(constructor, child);
                }
            }
        }
    }


}