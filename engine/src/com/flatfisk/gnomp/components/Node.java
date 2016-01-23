package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;

public class Node implements Component, Pool.Poolable{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private static int initialSize = 0;

    // The parent
    public EntityWrapper owner;
    public EntityWrapper parent = null;
    public Array<EntityWrapper> children;
    public Class<? extends Node> childType;

    public boolean hasChildren(){
        return children.size>0;
    }

    protected Node(Class<? extends Node> childType) {
        children = new Array<EntityWrapper>(initialSize);
        this.childType = childType;
    }

    public void setOwner(Entity entity) {
        owner = new EntityWrapper((GnompEngine.GnompEntity) entity);
    }

    public void setParent(Entity entity) {
        parent = new EntityWrapper((GnompEngine.GnompEntity) entity);
    }

    /**
     * @param entity, the child entity to be added.
     * @return true if child was added
     */
    public boolean addChild(Entity entity, GnompEngine engine) {

        if (!hasChild(entity,engine) && !entity.equals(this.parent)) {
            add(entity);
            Node childNoe = entity.getComponent(this.childType);
            childNoe.parent = owner;
            return true;
        }
        return false;
    }


    /**
     * Remove all children of node
     */
    public void removeAllChildren() {
        children.clear();
    }
    private void add(Entity entity){
        children.add(new EntityWrapper((GnompEngine.GnompEntity) entity));
    }
    public void removeChild(Entity entity,GnompEngine engine){
        LOG.info("Removing child!");
        EntityWrapper remove = null;
        for(EntityWrapper wrapper:children){
            if(wrapper.getEntity(engine).equals(entity)){
                remove = wrapper;
            }
        }

        children.removeValue(remove, false);
    }

    private boolean hasChild(Entity entity, GnompEngine engine){

        for(EntityWrapper entityWrapper:children){
            return entityWrapper.getEntity(engine).equals(entity);
        }

        return false;
    }

    @Override
    public void reset(){
        children.clear();
        parent = null;
        owner = null;
    }

    public Node addCopy(GnompEngine gnompEngine, Entity entity) {
        Node node = gnompEngine.addComponent(this.getClass(),entity);
        node.children.addAll(children);
        node.owner = owner;
        node.parent = parent;

        return node;
    }

    public class EntityWrapper{
        private GnompEngine.GnompEntity entity;
        private long id;

        public EntityWrapper(GnompEngine.GnompEntity entity) {
            this.entity = entity;
            this.id = entity.id;
        }

        public GnompEngine.GnompEntity getEntity(GnompEngine gnompEngine) {
            if(entity!=null && entity.id == id) {
                return entity;
            }else{
                entity = gnompEngine.getEntity(id);
                return entity;
            }
        }
    }
}
