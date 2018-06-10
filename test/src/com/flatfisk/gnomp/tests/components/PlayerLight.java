package com.flatfisk.gnomp.tests.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;


public class PlayerLight implements ISerializable<PlayerLight>,Component, Pool.Poolable {
    public int touchedPlatformTimes = 0;

    @Override
    public void reset() {
        touchedPlatformTimes = 0;
    }

    @Override
    public PlayerLight addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }
}
