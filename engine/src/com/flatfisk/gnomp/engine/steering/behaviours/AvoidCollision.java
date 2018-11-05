package com.flatfisk.gnomp.engine.steering.behaviours;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompContactListener;
import com.flatfisk.gnomp.engine.GnompMappers;
import com.flatfisk.gnomp.engine.components.PhysicsSteerable;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.engine.steering.Steering;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

// TODO: Enormous amount of Vector2 copys

/*
TODO: SOMETHING IN HERE makes entites lock together if their distance is < a threshold.
* */

public class AvoidCollision implements Steering, GnompContactListener {
    private float minRadius;
    private int updateRadiusInterval = 5;
    private int updateRadiusCounter = updateRadiusInterval;


    private int updateCollisionInterval = 0;
    private int updateCollisionCounter = updateCollisionInterval;


    private World world;

    private Fixture collisionCircle;
    private FixtureDef fixtureDef;
    private Vector2 desiredVelocity = Pools.obtainVector();

    private Queue<Float> velicities = new Queue<Float>(10);

    Transform t = new Transform();

    Pool<Collision> collisionPool = new Pool<Collision>() {
        @Override
        protected Collision newObject() {
            return new Collision();
        }
    };
    private ObjectMap<Fixture, Collision> lastContacts = new ObjectMap<Fixture, Collision>();

    @Override
    public void beginContact(Fixture a, Fixture b, Contact contact) {

    }

    @Override
    public void endContact(Fixture a, Fixture b, Contact contact) {

        // This needs to be updated each cycle since we are completely dependant on removing lastContacts
        Fixture circle = null;
        Fixture other = null;
        if (a.equals(collisionCircle)) {
            circle = a;
            other = b;

        } else if (b.equals(collisionCircle)) {
            other = a;
            circle = b;
        }

        if (circle != null) {
            freeContactPoints(other);
            lastContacts.remove(other);
        }
    }

    @Override
    public void preSolve(Fixture a, Fixture b, Contact contact, Manifold oldManifold) {
        // Update every 5 frames
        if(updateCollisionCounter--<0) {
            Fixture circle = null;
            Fixture other = null;
            boolean circleIsA = true;
            if (a.equals(collisionCircle)) {
                circle = a;
                other = b;

            } else if (b.equals(collisionCircle)) {
                other = a;
                circle = b;
                circleIsA = false;
            }

            if (circle != null) {
                WorldManifold man = contact.getWorldManifold();
                oldManifold.getPoints();
                addContactPoints(other, man.getPoints(), man.getNormal(), circleIsA);
            }
            updateCollisionCounter = updateCollisionInterval;
        }
    }

    @Override
    public void postSolve(Fixture a, Fixture b, Contact contact, ContactImpulse impulse) {

    }

    static class Collision {
        Collision() {
        }

        public Vector2 position = new Vector2();
        public Vector2 normal = new Vector2();
    }

    public AvoidCollision(FixtureDef def, float minRadius) {
        //collisionCircle = b.createFixture(def);
        this.fixtureDef = def;
        this.minRadius = minRadius;
    }

    public void setWorld(World world){
        this.world = world;
    }

    public void deActivate(){
        if(false && collisionCircle!=null) {
            collisionCircle.getBody().getFixtureList().removeValue(collisionCircle, true);
            collisionCircle = null;
        }
    }

    private Transform position = new Transform();
    private Transform velocity = new Transform();

    @Override
    public Transform steer(Entity entity, Body body, PhysicsSteerable.Container physicsSteering, ShapeRenderer debugRender) {
        desiredVelocity.setZero();

        if(collisionCircle==null){
            collisionCircle = body.createFixture(fixtureDef);
            return t.set(desiredVelocity, 0);
        }

        //
        //float collisionCircleCadius = body.getLinearVelocity().len();
        float collisionCircleCadius;
        collisionCircleCadius = velocity.set(GnompMappers.physicsBodyStateMap.get(entity).velocity).vector.len();

        collisionCircleCadius = MathUtils.clamp(collisionCircleCadius, minRadius,4);

        velicities.addLast(collisionCircleCadius);
        collisionCircleCadius = 0;
        for(float v: velicities){
            collisionCircleCadius+=v/(float) velicities.size;
        }
        // ~1 Second average
        if(velicities.size>60){
            velicities.removeFirst();
        }

        if(updateRadiusCounter--<0) {
            collisionCircle.getShape().setRadius(collisionCircleCadius);
            updateRadiusCounter=updateRadiusInterval;
        }

        position.vector.set(GnompMappers.spatialNodeMap.get(entity).world.vector);
        position.toBox2D();


        if (lastContacts.size>0) {
            for (Collision c : lastContacts.values()) {
                //TODO: Use c.normal value for something useful?
                desiredVelocity.add(Pools.obtainVector2FromCopy(position.vector).sub(c.position));
            }
        }

        if (!desiredVelocity.isZero()) {
            // TODO: Calculate this for Body.Container
            TextureCoordinates.BoundingRectangle r = entity.getComponent(Renderable.class).boundingRectangle;
            float radius = Math.max(r.height,r.width)* PhysicsConstants.METERS_PER_PIXEL;
            // Set radius for coll circle
            minRadius = Math.max(radius, minRadius);

            desiredVelocity.scl(1f/(float) lastContacts.size);

            float distanceFromCollision = Math.max(0,desiredVelocity.len()-radius);
            float maxDistanceFromCollision = collisionCircleCadius-radius;

            distanceFromCollision=Math.min(maxDistanceFromCollision-0.001f, distanceFromCollision);
            float inverted = (maxDistanceFromCollision-distanceFromCollision)/maxDistanceFromCollision;

            desiredVelocity.nor();
            desiredVelocity.scl(inverted);
            desiredVelocity.scl(physicsSteering.maxLinearVelocity);

            if (debugRender != null && lastContacts != null) {
                    debugRender.setColor(Color.YELLOW);
                    debugRender.line(position.vector, position.vector.cpy().add(desiredVelocity.cpy()));
            }
        }


        // TODO: Do not apply force if no force given.
        return t.set(desiredVelocity, 0);
    }


    /*
    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {

        // This needs to be updated each cycle since we are completely dependant on removing lastContacts
        Fixture circle = null;
        Fixture other = null;
        if (contact.getFixtureA().equals(collisionCircle)) {
            circle = contact.getFixtureA();
            other = contact.getFixtureB();

        } else if (contact.getFixtureB().equals(collisionCircle)) {
            other = contact.getFixtureA();
            circle = contact.getFixtureB();
        }

        if (circle != null) {
            freeContactPoints(other);
            lastContacts.remove(other);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

        // Update every 5 frames
        if(updateCollisionCounter--<0) {
            Fixture circle = null;
            Fixture other = null;
            boolean circleIsA = true;
            if (contact.getFixtureA().equals(collisionCircle)) {
                circle = contact.getFixtureA();
                other = contact.getFixtureB();

            } else if (contact.getFixtureB().equals(collisionCircle)) {
                other = contact.getFixtureA();
                circle = contact.getFixtureB();
                circleIsA = false;
            }

            if (circle != null) {
                addContactPoints(other, contact.getWorldManifold().getPoints(), contact.getWorldManifold().getNormal(), circleIsA);
            }
            updateCollisionCounter = updateCollisionInterval;
        }

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
    */

    private void freeContactPoints(Fixture ident){
        if(lastContacts.containsKey(ident)) {
            collisionPool.free(lastContacts.remove(ident));
        }
    }

    private void addContactPoints(Fixture ident, Vector2[] points, Vector2 normal, boolean circleIsA){
        freeContactPoints(ident);
        Collision collision = collisionPool.obtain();
        collision.normal.set(normal);
        if(!circleIsA) {
            collision.normal.scl(-1);
        }
        Vector2 position = collision.position;
        for(Vector2 p: points){
            position.add(p);
        }
        position.scl(1f/(float) points.length);
        lastContacts.put(ident, collision);
    }
}
