package com.flatfisk.gnomp.tests.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;


public class PlayerSensor implements ISerializable<PlayerSensor>,Component, Pool.Poolable {
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
