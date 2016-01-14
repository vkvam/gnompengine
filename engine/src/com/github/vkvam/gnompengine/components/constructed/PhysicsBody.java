package com.github.vkvam.gnompengine.components.constructed;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.github.vkvam.gnompengine.math.Translation;

public class PhysicsBody implements  Component, Pool.Poolable {
    public Body body;

    public PhysicsBody() {
    }

    public Translation getTranslation(){
        return new Translation(getPosition(),getAngle());
    }
    public Translation getVelocity(){
        return new Translation(getLinearVelocity(),getAngularVelocity());
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
