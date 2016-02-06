package com.flatfisk.gnomp.engine.components.abstracts;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;

public abstract class AbstractNode implements Component, Pool.Poolable{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private static int initialSize = 1;
    public Entity owner;
    public Entity parent = null;
    public Array<Entity> children;

    protected AbstractNode() {
        children = new Array<Entity>(initialSize);
    }

    public void setOwner(Entity entity) {
        owner = entity;
    }

    /**
     * @return true if child was added
     */
    public boolean addChild(Entity entity) {

        if (!hasChild(entity) && !entity.equals(this.parent)) {
            children.add(entity);
            AbstractNode childNode = entity.getComponent(getClass());
            childNode.parent = owner;
            return true;
        }
        return false;
    }

    public void removeChild(Entity entity){
        children.removeValue(entity, false);
    }

    private boolean hasChild(Entity entity){
        children.contains(entity, false);
        return false;
    }

    @Override
    public void reset(){
        children.clear();
        parent = null;
        owner = null;
    }

    public AbstractNode addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }

}
