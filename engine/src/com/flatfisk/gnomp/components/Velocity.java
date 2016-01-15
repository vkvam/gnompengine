package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.math.Spatial;

/**
 * Created by Vemund Kvam on 05/12/15.
 */
public class Velocity implements Component, Pool.Poolable {
    public Spatial velocity;
    @Override
    public void reset() {
        velocity.vector.setZero();
        velocity.rotation = 0;
    }
}
