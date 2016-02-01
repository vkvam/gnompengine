package com.flatfisk.gnomp.engine.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.components.abstracts.ISpatialController;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Root node for constructing a PhysicsBody
 */
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

    /**
     * PhysicsBody node for constructing a Fixture.
     */
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
            Transform spat = Pools.obtainTransform();
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
            LOG.info("Removing userdata for body");
            body.setUserData(null);

            LOG.info("Removing userdata for fixtures");
            for(Fixture f : body.getFixtureList()){
                f.setUserData(null);
            }
            LOG.info("Destroying body");
            body.getWorld().destroyBody(body);
            body = null;
            positionChanged = false;
        }

    }
}
