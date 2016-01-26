package com.flatfisk.gnomp.components.roots;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.ConstructorComponent;

public class PhysicsBodyDef implements ConstructorComponent<PhysicsBodyDef>, Pool.Poolable {
    public BodyDef bodyDef;

    public PhysicsBodyDef() {
        bodyDef = new BodyDef();
    }

    @Override
    public void reset() {
        bodyDef = null;
    }

    @Override
    public PhysicsBodyDef addCopy(GnompEngine gnompEngine, Entity entity) {
        PhysicsBodyDef physicsBodyDef = gnompEngine.addComponent(getClass(),entity);
        physicsBodyDef.bodyDef = bodyDef;
        return physicsBodyDef;
    }
}
