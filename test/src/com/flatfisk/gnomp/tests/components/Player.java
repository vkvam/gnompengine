package com.flatfisk.gnomp.tests.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;


public class Player implements ISerializable<Player>, Component, Pool.Poolable {
    public int touchedPlatformTimes = 0;
    public boolean wasKilled = true;

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
