package com.flatfisk.gnomp.engine;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.SortedIntList;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.constructors.SpatialConstructor;

import static com.flatfisk.gnomp.engine.GnompMappers.spatialNodeMap;

public class ConstructorManager {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public SortedIntList<Constructor> constructors;
    private Family rootFamily;

    public GnompEngine engine;
    ConstructorManager(GnompEngine engine){
        rootFamily = Family.all(Spatial.class, Spatial.Node.class).get();
        constructors = new SortedIntList<Constructor>();
        this.engine = engine;
    }

    public void addConstructor(Constructor constructor, int priority){
        constructors.insert(priority,constructor);
    }



    public void constructEntity(Entity entity) {
        dismantleEntity(entity);
        Spatial.Node constructorOrientation = spatialNodeMap.get(entity);
        for (SortedIntList.Node<Constructor> constructorNode : constructors) {
            Constructor constructor = constructorNode.value;
            constructEntity(constructor, entity, constructorOrientation);
        }
    }

    void reConstructEntity(Entity entity, Class<? extends Component>[] constructorClasses) {
        Spatial.Node constructorOrientation = spatialNodeMap.get(entity);

        for (SortedIntList.Node<Constructor> constructorNode : constructors) {
            Constructor constructor = constructorNode.value;
            if(classMatchesConstructor(constructor,constructorClasses)) {
                parentRemoved(constructor, entity);
                constructEntity(constructor, entity, constructorOrientation);
            }else if(!entityHasConstructor(entity, constructor)){
                entity.remove(constructor.constructed);
            }
        }
    }

    private boolean entityHasConstructor(Entity entity, Constructor constructor){
        return constructor.hasConstructor(entity);
    }

    private boolean classMatchesConstructor(Constructor constructor, Class<? extends Component>[] constructorClasses){
        for(Class<? extends Component> c : constructorClasses) {
            if(constructor.constructor.equals(c))
                return true;
        }
        return false;
    }

    private void constructEntity(Constructor constructor, Entity entity, Spatial.Node rootOrientation){
        Spatial.Node constructorOrientation = spatialNodeMap.get(entity);
        Array<Entity> children = constructorOrientation.children;

        boolean isParent = constructor.isParent(entity);

        if(isParent){
            LOG.info("Constructing entity for constructor:"+constructor.getClass());
        }

        Object iterateDTO = isParent?constructor.parentAdded(entity, constructorOrientation):null;
        childrenAdded(children, constructor, rootOrientation, constructorOrientation, constructorOrientation, iterateDTO);

        if(isParent) {
            constructor.parentAddedFinal(entity, constructorOrientation, iterateDTO);
        }
    }

    private void childrenAdded(Array<Entity> children, Constructor constructor, Spatial.Node rootOrientation, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Object iterateDTO){
        for(Entity childWrapper : children) {
            LOG.info("Child for constructor:"+childWrapper);
            if(childWrapper!=null && childWrapper!=null) {
            Entity child = childWrapper;
                if (constructor.isChild(child)) {
                    Spatial.Node orientation = spatialNodeMap.get(child);
                    LOG.info("Child added for constructor:" + constructor.getClass());

                    iterateDTO = constructor.insertedChild(child, constructorOrientation, parentOrientation, orientation, iterateDTO);
                    childrenAdded(orientation.children, constructor, rootOrientation, constructorOrientation, orientation, iterateDTO);

                    /* TODO: Evaluate if this is valid when reconstructing an entity. It probably won't break an entity,
                    but it may cause some strange and undesired end states. */
                } else if (constructor.isParent(child)) {
                    LOG.info("RECONSTRUCT: "+rootOrientation);
                    LOG.info("Parent added for constructor through child:" + constructor.getClass());

                    constructEntity(constructor, child, rootOrientation);

                }
            }
        }
    }


    Entity getConstructor(Entity entity) {

        boolean hasChildren = false;
        boolean hasConstructor = false;

        Constructor constructor;

        for (SortedIntList.Node<Constructor> constructorNode : constructors) {
            constructor = constructorNode.value;
            // TODO: Is this really correct?
            if(!(constructor instanceof SpatialConstructor)) {
                hasChildren = hasChildren|| constructor.relationshipMapper.has(entity);
                hasConstructor = hasConstructor|| constructor.constructorMapper.has(entity);
            }
        }

        Spatial.Node rel = entity.getComponent(Spatial.Node.class);

        if(hasConstructor){
            return entity;
        }else if(hasChildren && rel!=null &&rel.parent!=null){
            return getConstructor(rel.parent);
        }
        return null;
    }

    Entity dismantleEntity(Entity constructorEntity){

        for (SortedIntList.Node<Constructor> constructorNode : constructors) {
            Constructor constructor = constructorNode.value;
            if(constructorEntity!=null) {
                parentRemoved(constructor, constructorEntity);
            }
        }
        return constructorEntity;
    }

    private void parentRemoved(Constructor constructor, Entity entity) {
        LOG.info("Parent removed for constructor:"+constructor.getClass());
        Spatial.Node constructorOrientation = spatialNodeMap.get(entity);
        if(constructorOrientation == null){
            throw new NullPointerException("Constructable entities needs to have a Spatial.Node");
        }
        Array<Entity> children = constructorOrientation.children;

        constructor.parentRemoved(entity);
        childrenRemoved(children, constructor);
    }


    private void childrenRemoved(Array<Entity> children, Constructor constructor){
        for(Entity childWrapper : children) {
            if(childWrapper!=null) {
                if (constructor.isChild(childWrapper)) {
                    Spatial.Node orientation = spatialNodeMap.get(childWrapper);
                    constructor.childRemoved(childWrapper);
                    childrenRemoved(orientation.children, constructor);

                } else if (constructor.isParent(childWrapper)) {
                    parentRemoved(constructor, childWrapper);
                }
            }
        }
    }


}
