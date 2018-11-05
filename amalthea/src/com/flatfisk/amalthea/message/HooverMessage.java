package com.flatfisk.amalthea.message;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.engine.message.Message;

/**
* Created by Vemund Kvam on 20/02/16.
*/
public class HooverMessage implements Message {
    public Entity entityHoovered;
    public Vector2 at = new Vector2();
    public boolean isPressed;

    public HooverMessage() {

    }

    @Override
    public void reset() {
        entityHoovered = null;
    }
}
