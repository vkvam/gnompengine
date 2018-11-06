package com.flatfisk.gnomp.engine.message;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.flatfisk.gnomp.engine.GnompEngine;

/**
 * Sends messages to other systems in the form of entities.
 *
 */
public class Messenger<E extends Message>{
    private final GnompEngine engine;
    private final Family messageFamily;
    private final Array<Entity> messagesToAdd;
    private final Class<E> messageType;

    public Messenger(Class<E> messageType, GnompEngine engine) {
        this.messageFamily = Family.all(messageType).get();
        this.messagesToAdd =  new Array<Entity>(2);
        this.messageType = messageType;
        this.engine = engine;
    }

    public void update() {
        removeMessages();
        addMessages();
    }

    public E createMessage(){
        Entity entity = engine.createEntity();
        messagesToAdd.add(entity);
        return engine.addComponent(messageType, entity);
    }

    private void removeMessages(){
        for(Entity e : engine.getEntitiesFor(messageFamily)){
            engine.removeEntity(e);
        }
    }

    private void addMessages(){
        for(Entity e : messagesToAdd){
            engine.addEntity(e);
        }
        messagesToAdd.clear();
    }

}
