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
        return null;
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
            return null;
        }
    }

    public static class Container implements ISpatialController, Component, Pool.Poolable {
        private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
        public Body body;

        // Set to true to have the position updated to the value of Spatial.
        public boolean positionChanged = false;

        public boolean forceApplied = false;
        public boolean wake = false;

        public PositionModificationType positionModificationType = PositionModificationType.NONE;
        public AngleModificationType angleModificationType = AngleModificationType.IMPULSE;
        public float angularModification = 0;
        public Vector2 modifyDirection = new Vector2();
        public Vector2 modifyPoint = new Vector2();


        /** Set the position of the body's origin and rotation. This breaks any contacts and wakes the other bodies. Manipulating a
         * body's transform may cause non-physical behavior.
         * @param position the world position of the body's local origin.
         * @param angle the world rotation in radians. */
        public void setTransform (Vector2 position, float angle) {
            modifyDirection.set(position);
            angularModification = angle;
            this.positionModificationType = PositionModificationType.TRANSFORM;
        }

        /** Set the position of the body's origin and rotation. This breaks any contacts and wakes the other bodies. Manipulating a
         * body's transform may cause non-physical behavior.
         * @param x the world position on the x-axis
         * @param y the world position on the y-axis
         * @param angle the world rotation in radians. */
        public void setTransform (float x, float y, float angle) {
            modifyDirection.set(x,y);
            angularModification = angle;
            this.positionModificationType = PositionModificationType.TRANSFORM;
        }

        /** Set the position of the body's origin. This breaks any contacts and wakes the other bodies. Manipulating a
         * body's transform may cause non-physical behavior.
         * @param x the world position on the x-axis
         * @param y the world position on the y-axis
         */
        public void setPosition (float x, float y ) {
            modifyDirection.set(x,y);
            this.positionModificationType = PositionModificationType.POSITION;
        }


        /** Set the position of the body's origin. This breaks any contacts and wakes the other bodies. Manipulating a
         * body's transform may cause non-physical behavior.
         * @param position the world position
         */
        public void setPosition (Vector2 position) {
            modifyDirection.set(position);
            this.positionModificationType = PositionModificationType.POSITION;
        }


        /** Apply a force at a world point. If the force is not applied at the center of mass, it will generate a torque and affect the
         * angular velocity. This wakes up the body.
         * @param force the world force vector, usually in Newtons (N).
         * @param point the world position of the point of application.
         * @param wake up the body */
        public void applyForce (Vector2 force, Vector2 point, boolean wake){
            this.positionModificationType = PositionModificationType.FORCE_AT_POINT;
            modifyDirection.set(force);
            modifyPoint.set(point);
            this.wake = wake;
        }

        /** Apply a force at a world point. If the force is not applied at the center of mass, it will generate a torque and affect the
         * angular velocity. This wakes up the body.
         * @param forceX the world force vector on x, usually in Newtons (N).
         * @param forceY the world force vector on y, usually in Newtons (N).
         * @param pointX the world position of the point of application on x.
         * @param pointY the world position of the point of application on y.
         * @param wake up the body*/
        public void applyForce (float forceX, float forceY, float pointX, float pointY, boolean wake){
            this.positionModificationType = PositionModificationType.FORCE_AT_POINT;
            modifyDirection.set(forceX,forceY);
            modifyPoint.set(pointX,pointY);
            this.wake = wake;
        }

        /** Apply a force to the center of mass. This wakes up the body.
         * @param force the world force vector, usually in Newtons (N). */
        public void applyForceToCenter (Vector2 force, boolean wake){
            this.positionModificationType = PositionModificationType.FORCE_AT_CENTER;
            modifyDirection.set(force);
            this.wake = wake;
        }

        /** Apply a force to the center of mass. This wakes up the body.
         * @param forceX the world force vector, usually in Newtons (N).
         * @param forceY the world force vector, usually in Newtons (N). */
        public void applyForceToCenter (float forceX, float forceY, boolean wake){
            this.positionModificationType = PositionModificationType.FORCE_AT_CENTER;
            modifyDirection.set(forceX,forceY);
            this.wake = wake;
        }

        /** Apply a torque. This affects the angular velocity without affecting the linear velocity of the center of mass. This wakes up
         * the body.
         * @param torque about the z-axis (out of the screen), usually in N-m.
         * @param wake up the body */
        public void applyTorque (float torque, boolean wake){
            this.angleModificationType = AngleModificationType.TORQUE;
            this.angularModification = torque;
            this.wake = wake;
        }

        /** Apply an impulse at a point. This immediately modifies the velocity. It also modifies the angular velocity if the point of
         * application is not at the center of mass. This wakes up the body.
         * @param impulse the world impulse vector, usually in N-seconds or kg-m/s.
         * @param point the world position of the point of application.
         * @param wake up the body*/
        public void applyLinearImpulse (Vector2 impulse, Vector2 point, boolean wake){
            this.positionModificationType = PositionModificationType.IMPULSE_AT_POINT;
            this.modifyDirection.set(impulse);
            this.modifyPoint.set(point);
            this.wake = wake;
        }

        /** Apply an impulse at a point. This immediately modifies the velocity. It also modifies the angular velocity if the point of
         * application is not at the center of mass. This wakes up the body.
         * @param impulseX the world impulse vector on the x-axis, usually in N-seconds or kg-m/s.
         * @param impulseY the world impulse vector on the y-axis, usually in N-seconds or kg-m/s.
         * @param pointX the world position of the point of application on the x-axis.
         * @param pointY the world position of the point of application on the y-axis.
         * @param wake up the body*/
        public void applyLinearImpulse (float impulseX, float impulseY, float pointX, float pointY, boolean wake){
            this.positionModificationType = PositionModificationType.IMPULSE_AT_POINT;
            this.modifyDirection.set(impulseX,impulseY);
            this.modifyPoint.set(pointX,pointY);
            this.wake = wake;
        }
        /** Apply an angular impulse.
         * @param impulse the angular impulse in units of kg*m*m/s */
        public void applyAngularImpulse (float impulse, boolean wake){
            this.angleModificationType = AngleModificationType.IMPULSE;
            this.angularModification = impulse;
            this.wake = wake;
        }



        public Vector2 getLinearVelocity(){
            return body.getLinearVelocity();
        }
        public float getAngularVelocity(){
            return body.getAngularVelocity();
        }

        public Vector2 getPosition(){
            return body.getPosition();
        }
        public float getAngle(){
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

        public enum PositionModificationType {
            NONE,
            FORCE_AT_CENTER,
            FORCE_AT_POINT,
            IMPULSE_AT_POINT,
            TRANSFORM,
            POSITION
        }

        public enum AngleModificationType {
            NONE,
            IMPULSE,
            TORQUE
        }

    }
}
