package com.flatfisk.gnomp.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Pool;

public class PhysicsConstruction implements Component, Pool.Poolable {
    public BodyDef.BodyType bodyType;

    @Override
    public void reset() {
        bodyType = null;
    }
}
