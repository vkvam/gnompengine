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
    public Entity owner;
    public Entity parent = null;
    public Array<Entity> children;
    public Class<? extends Node> childType;

    public boolean hasChildren(){
        return children.size>0;
    }

    protected Node(Class<? extends Node> childType) {
        children = new Array<Entity>(initialSize);
        this.childType = childType;
    }

    public void setOwner(Entity entity) {
        owner = entity;
    }

    public void setParent(Entity entity) {
        parent = entity;
    }

    /**
     * @return true if child was added
     */
    public boolean addChild(Entity entity) {

        if (!hasChild(entity) && !entity.equals(this.parent)) {
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
        children.add(entity);
    }
    public void removeChild(Entity entity){
        LOG.info("Removing child!");
        children.removeValue(entity, false);
    }

    private boolean hasChild(Entity entity){
        children.contains(entity,false);
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

}
