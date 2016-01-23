package com.flatfisk.gnomp.components.constructed;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

public class PhysicsBody implements SpatialRelative.Controller, Component, Pool.Poolable {
    public Body body;
    public boolean positionChanged = false;

    public PhysicsBody() {
    }

    public Spatial getTranslation(){
        return new Spatial(getPosition(),getAngle());
    }
    public Spatial getVelocity(){
        Spatial spat = Pools.obtainSpatial();
        spat.set(getLinearVelocity(),getAngularVelocity());
        return spat;
    }

    private Vector2 getLinearVelocity(){
        return body.getLinearVelocity();
    }
    private float getAngularVelocity(){
        return body.getAngularVelocity();
    }

    private Vector2 getPosition(){
        return body.getPosition();
    }
    private float getAngle(){
        return body.getAngle();
    }

    @Override
    public void reset() {
        body = null;
        positionChanged = false;
    }

}
