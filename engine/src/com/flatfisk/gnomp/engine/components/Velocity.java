package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by Vemund Kvam on 05/12/15.
 */
public class Velocity implements ISerializable<Velocity>, Component, Pool.Poolable {
    public Transform velocity = new Transform();
    @Override
    public void reset() {
        velocity.setZero();
    }

    @Override
    public Velocity addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }
}
