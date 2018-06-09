package com.flatfisk.gnomp.tests.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.engine.message.Message;

/**
* Created by Vemund Kvam on 20/02/16.
*/
public class RemoveEntitySystem extends IteratingSystem {
    private float c = 0;
    public RemoveEntitySystem() {
        super(Family.all().get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {

        c+=v;
        if(c>5){
            this.getEngine().removeEntity(entity);
        }
    }
}
