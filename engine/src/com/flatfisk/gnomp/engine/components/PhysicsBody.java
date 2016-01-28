package com.flatfisk.gnomp.engine.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.components.abstracts.ISpatialController;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

public class PhysicsBody implements ISerializable<PhysicsBody>, Pool.Poolable {
    public BodyDef bodyDef;
    public PhysicsBody() {
        bodyDef = new BodyDef();
    }

    @Override
    public void reset() {
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.setZero();
        bodyDef.angle = 0;
        bodyDef.linearVelocity.setZero();
        bodyDef.angularVelocity = 0;
        bodyDef.linearDamping = 0;
        bodyDef.angularDamping = 0;
        bodyDef.allowSleep = true;
        bodyDef.awake = true;
        bodyDef.fixedRotation = false;
        bodyDef.bullet = false;
        bodyDef.active = true;
        bodyDef.gravityScale = 1;
    }

    @Override
    public PhysicsBody addCopy(GnompEngine gnompEngine, Entity entity) {
        PhysicsBody physicsBodyDef = gnompEngine.addComponent(getClass(),entity);
        physicsBodyDef.bodyDef = bodyDef;
        return physicsBodyDef;
    }


    public static class Node implements ISerializable<Node> {
        public boolean intermediate = false;

        @Override
        public void reset() {

        }

        public Node addCopy(GnompEngine gnompEngine,Entity entity){
            Node relative = gnompEngine.addComponent(getClass(),entity);
            return relative;
        }
    }

    public static class Container implements ISpatialController, Component, Pool.Poolable {
        private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
        public Body body;
        public boolean positionChanged = false;

        public Container() {
        }

        public Transform getTranslation(){
            return new Transform(getPosition(),getAngle());
        }
        public Transform getVelocity(){
            Transform spat = Pools.obtainSpatial();
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
            LOG.info("Removing body, " + body);
            body.setUserData(null);
            body.getWorld().destroyBody(body);

            body = null;
            positionChanged = false;
        }

    }
}
