package com.flatfisk.gnomp.engine.steering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.flatfisk.gnomp.engine.components.PhysicsSteerable;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

public class SteeringSet implements Steering{
    public Array<SteeringWithWeight> steerings = new Array<SteeringWithWeight>();
    Transform t = new Transform();
    //private ObjectMap<Class<? extends Steering>, Steering> steerings = new ObjectMap<Class<? extends Steering>, Steering>();

    @Override
    public Transform steer(Entity entity, Body body, PhysicsSteerable.Container physicsSteering, ShapeRenderer debugRender) {
        t.setZero();
        for(SteeringWithWeight s: steerings) {
            // The weight is applied to the desired velocity vector, a steering can thus get drastically reduced effect
            // by reducing the weight.

            // When two steerings have opposing vectors and same weight, the desired velocity vector will have 0 length.
            // Approach and collision detection might be in conflict.
            t.add(s.steering.steer(entity, body, physicsSteering, debugRender).scl(s.weight));
        }
        return t;
    }

    public void setSteering(Steering steering, float weight){
        steerings.add(new SteeringWithWeight(steering, weight));
    }

    public static class SteeringWithWeight{
        Steering steering;
        float weight;

        public SteeringWithWeight(Steering steering, float weight) {
            this.steering = steering;
            this.weight = weight;
        }
    }

}
