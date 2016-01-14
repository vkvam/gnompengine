package com.github.vkvam.gnompengine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

public abstract class Node implements Component, Pool.Poolable{
    public Entity[] children;
    public Entity owner;
    private static int initialSize = 0;

    // The id of the parent
    public Entity parent = null;
    public Class<? extends Node> childType;

    protected Node(Class<? extends Node> childType) {
        children = new Entity[initialSize];
        this.childType = childType;
    }

    /**
     * @param entity, the child entity to be added.
     * @return true if child was not present.
     */
    public boolean addChild(Entity entity) {

        if (!hasChild(entity) && !entity.equals(this.parent)) {
            add(entity);
            Node a = entity.getComponent(this.childType);
            a.parent = owner;
            return true;
        }
        return false;
    }

    private void add(Entity id){
        int size= children.length;
        Entity[] newItems = new Entity[size+1];
        System.arraycopy(children, 0, newItems, 0, children.length);
        newItems[size]=id;
        children =newItems;
    }

    private boolean hasChild(Entity entity){
        boolean result = false;
        for(Entity child : children){
            if(child.equals(entity)){
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void reset(){
        children = new Entity[initialSize];
        parent = null;
    }

}
