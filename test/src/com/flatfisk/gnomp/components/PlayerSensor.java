package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class PlayerSensor implements ConstructorComponent<PlayerSensor>,Component, Pool.Poolable {
    public int touchedPlatformTimes = 0;

    @Override
    public void reset() {
        touchedPlatformTimes = 0;
    }

    @Override
    public PlayerSensor addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }
}
