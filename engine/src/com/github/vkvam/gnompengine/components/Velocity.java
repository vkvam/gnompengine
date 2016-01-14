package com.github.vkvam.gnompengine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.github.vkvam.gnompengine.math.Translation;

/**
 * Created by Vemund Kvam on 05/12/15.
 */
public class Velocity implements Component, Pool.Poolable {
    public Translation velocity;
    @Override
    public void reset() {
        velocity.position.setZero();
        velocity.angle = 0;
    }
}
