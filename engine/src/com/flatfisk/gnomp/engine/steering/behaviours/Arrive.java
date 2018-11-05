package com.flatfisk.gnomp.engine.steering.behaviours;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.flatfisk.gnomp.engine.GnompMappers;
import com.flatfisk.gnomp.engine.components.PhysicsSteerable;
import com.flatfisk.gnomp.engine.steering.Steering;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

public class Arrive implements Steering {
    private Transform temp = new Transform();
    public Arrive() {
    }

    @Override
    public Transform steer(Entity entity, Body body, PhysicsSteerable.Container physicsSteering, ShapeRenderer debugRender) {
        Vector2 velocity = GnompMappers.physicsBodyStateMap.get(entity).velocity.vector;
        Vector2 position = temp.set(GnompMappers.spatialNodeMap.get(entity).world).toBox2D().vector;

        Vector2 targetPosition = physicsSteering.target;

        Vector2 desiredVelocity = Pools.obtainVector2FromCopy(targetPosition).sub(position);
        float distance = desiredVelocity.len();


        float timeToStop = velocity.len() / physicsSteering.maxLinearAcceleration;
        float distanceToStop = timeToStop * timeToStop * physicsSteering.maxLinearAcceleration;

        //Vector2 desiredVelocity = positionDiff.nor().scl(maxVelocity);

        // Start hardest brake possible 2 units from target
        if (distance < distanceToStop) {
            desiredVelocity.scl(0.01f);
        }else {
            desiredVelocity.nor().scl(physicsSteering.maxLinearVelocity);
        }

        /*
        if(debugRender!=null){
            debugRender.setColor(Color.ORANGE);
            debugRender.line(body.getPosition(), body.getPosition().cpy().add(desiredVelocity.cpy().scl(weight)));
        }
        */

        if(debugRender!=null){
            debugRender.setColor(Color.WHITE);
            debugRender.line(body.getPosition(), physicsSteering.target);
        }

        return temp.set(desiredVelocity, 0);
    }



}
