package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 05/12/15.
 */
public class Velocity implements ConstructorComponent<Velocity>, Component, Pool.Poolable {
    public Spatial velocity = Pools.obtainSpatial();
    @Override
    public void reset() {
        velocity.vector.setZero();
        velocity.rotation = 0;
    }

    @Override
    public Velocity addCopy(GnompEngine gnompEngine, Entity entity) {
        Velocity vel = gnompEngine.addComponent(Velocity.class,entity);
        vel.velocity = Pools.obtainSpatialFromCopy(velocity);
        return vel;
    }
}
