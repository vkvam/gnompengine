package com.flatfisk.gnomp.entitymanagers;

/**
 * Created by Vemund Kvam on 06/12/15.
 */

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.SortedIntList;
import com.flatfisk.gnomp.components.Node;
import com.flatfisk.gnomp.components.relatives.OrientationRelative;
import com.flatfisk.gnomp.components.roots.OrientationDef;
import com.flatfisk.gnomp.constructors.Constructor;

public class ConstructorManager implements EntityListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public ComponentMapper<OrientationRelative> orientationMapper;
    public ComponentMapper<Node> nodeMapper;

    public SortedIntList<Constructor> constructors;
    public Family rootFamily;

    public PooledEngine engine;
    public ConstructorManager(){
        rootFamily = Family.all(OrientationDef.class,OrientationRelative.class).get();

        nodeMapper = ComponentMapper.getFor(Node.class);
        orientationMapper = ComponentMapper.getFor(OrientationRelative.class);
        constructors = new SortedIntList<Constructor>();
    }

    public void addConstructor(Constructor constructor, int priority){
        constructors.insert(priority,constructor);
    }

    @Override
    public void entityAdded(Entity entity) {

        OrientationRelative rootOrientation = orientationMapper.get(entity);

        for(SortedIntList.Node<Constructor> constructorNode:constructors){
            LOG.info("Adding entity to constructor "+constructorNode.value.getClass());
            Constructor constructor = constructorNode.value;
            if(constructor.isParent(entity)){
                LOG.info("Entity is a parent according to constructor:"+constructor.getClass());
                parentAdded(constructor, entity, rootOrientation);
            }
        }
    }

    private void parentAdded(Constructor constructor, Entity entity, OrientationRelative rootOrientation){
        LOG.info("Parent added for constructor:"+constructor.getClass());
        OrientationRelative constructorOrientation = orientationMapper.get(entity);
        Entity[] children = constructorOrientation.children;

        Object iterateDTO = constructor.parentAdded(entity, rootOrientation, constructorOrientation);
        entityChildrenAdded(children,constructor,rootOrientation,constructorOrientation,constructorOrientation,iterateDTO);
        constructor.parentAddedFinal(entity, iterateDTO);
    }

    private void entityChildrenAdded(Entity[] children, Constructor constructor, OrientationRelative rootOrientation, OrientationRelative constructorOrientation, OrientationRelative parentOrientation, Object iterateDTO){
        for(Entity child : children) {
            if(constructor.isChild(child)){
                OrientationRelative orientation = orientationMapper.get(child);
                LOG.info("Child added for constructor:"+constructor.getClass());
                iterateDTO = constructor.insertedChild(child,rootOrientation,constructorOrientation,parentOrientation,orientation,iterateDTO);
                entityChildrenAdded(orientation.children, constructor, rootOrientation, constructorOrientation, orientation, iterateDTO);
            }else if(constructor.isParent(child)){
                LOG.info("Parent added for constructor through child:"+constructor.getClass());
                parentAdded(constructor,child,rootOrientation);
            }
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }

}
