package com.flatfisk.gnomp.components.constructed;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.math.Spatial;

public class PhysicsBody implements  Component, Pool.Poolable {
    public Body body;
    public boolean positionChanged = false;

    public PhysicsBody() {
    }

    public Spatial getTranslation(){
        return new Spatial(getPosition(),getAngle());
    }
    public Spatial getVelocity(){
        return new Spatial(getLinearVelocity(),getAngularVelocity());
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
    }

}
