package com.flatfisk.amalthea.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class Player implements ISerializable<Player>, Component, Pool.Poolable {
    public int touchedPlatformTimes = 0;
    public boolean wasKilled = true;
    public Vector2 lookAt = Vector2.Zero;

    @Override
    public void reset() {
        touchedPlatformTimes = 0;
    }

    @Override
    public Player addCopy(GnompEngine gnompEngine, Entity entity) {
        Player p = gnompEngine.addComponent(this.getClass(),entity);
        p.touchedPlatformTimes = touchedPlatformTimes;
        return p;
    }
}
