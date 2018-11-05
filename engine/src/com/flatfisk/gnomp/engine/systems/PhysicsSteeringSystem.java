package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.engine.GnompMappers;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.PhysicsBodyState;
import com.flatfisk.gnomp.engine.components.PhysicsSteerable;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

import static com.flatfisk.gnomp.engine.GnompMappers.*;

public class PhysicsSteeringSystem extends IteratingSystem {
    private final World box2DWorld;
    protected ShapeRenderer renderer;

    Vector2 desiredVelocity = new Vector2();
    Transform temp = new Transform();
    public PhysicsSteeringSystem(int priority, World box2dWorld, boolean debug) {
        super(Family.all(PhysicsSteerable.Container.class, PhysicsBody.Container.class).get(), priority);
        this.box2DWorld = box2dWorld;

        if(debug){
            renderer = new ShapeRenderer();
        }
    }



    @Override
    protected void processEntity(Entity entity, float dt) {

        if(renderer != null){
            renderer.setProjectionMatrix(getEngine().getSystem(CameraSystem.class).getPhysicsMatrix());
            renderer.begin(ShapeRenderer.ShapeType.Line);

        }

        Spatial.Node spat = GnompMappers.spatialNodeMap.get(entity);

        PhysicsBody.Container physicsBody = physicsBodyMap.get(entity);
        Body body = physicsBody.body;
        PhysicsSteerable.Container physicsSteering = physicsSteerableMapper.get(entity);

        PhysicsBodyState pbs = GnompMappers.physicsBodyStateMap.get(entity);

        Vector2 linearVelocity = temp.set(pbs.velocity).vector;
        desiredVelocity.setZero();

        Transform steering = physicsSteering.activeSteering.steer(entity, body, physicsSteering, renderer);
        desiredVelocity.add(steering.vector);

        float maxLinAcc = physicsSteering.maxLinearAcceleration;
        float maxLinVel = physicsSteering.maxLinearVelocity;

        // Calculate max force from provided max acc.
        float maxForce = pbs.mass*maxLinAcc;

        // PhysicsBodyState difference gives the direction of the force
        Vector2 velDiff = desiredVelocity.sub(linearVelocity);

        if(renderer!=null) {
            // Draws the
            //renderer.setColor(0,1,0,1);
            //renderer.line(body.getPosition(), body.getPosition().cpy().add(desiredVelocity));
            renderer.end();
        }

        // Normalize force vector and scale with max force
        velDiff.nor().scl(maxForce);
        //velDiff.limit(maxForce);

        body.applyForceToCenter(velDiff,true);

        // Caps to top speed
        // TODO: Use the square
        if(linearVelocity.len()>maxLinVel){
            body.setLinearVelocity(linearVelocity.nor().scl(maxLinVel));
        }

        float rot = spat.world.rotation*MathUtils.degreesToRadians+(float) Math.PI/2f;
        float rotDiff = wrapAngleAroundZero(rot-steering.rotation);
        if(rotDiff>0+0.001){
            body.applyTorque(-3f*pbs.intertia*maxForce, true);
        }else if(rotDiff<0-0.001){
            body.applyTorque(3f*pbs.intertia*maxForce, true);
        }


    }

    /** Wraps the given angle to the range [-PI, PI]
     * @param a the angle in radians
     * @return the given angle wrapped to the range [-PI, PI] */
    public static float wrapAngleAroundZero (float a) {
        if (a >= 0) {
            float rotation = a % MathUtils.PI2;
            if (rotation > MathUtils.PI) rotation -= MathUtils.PI2;
            return rotation;
        } else {
            float rotation = -a % MathUtils.PI2;
            if (rotation > MathUtils.PI) rotation -= MathUtils.PI2;
            return -rotation;
        }
    }
}
