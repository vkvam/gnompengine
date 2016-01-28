package com.flatfisk.gnomp.tests.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.abstracts.ISerializable;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class Player implements ISerializable<Player>, Component, Pool.Poolable {
    public int touchedPlatformTimes = 0;

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
