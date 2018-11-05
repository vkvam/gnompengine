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

public class Face implements Steering {
    private Transform temp = new Transform();
    public Face() {
    }

    @Override
    public Transform steer(Entity entity, Body body, PhysicsSteerable.Container physicsSteering, ShapeRenderer debugRender) {
        Vector2 velocity = GnompMappers.physicsBodyStateMap.get(entity).velocity.vector;
        //Vector2 position = temp.set(GnompMappers.spatialNodeMap.get(entity).world).toBox2D().vector;
        temp.rotation = velocity.angleRad();
        return temp;
    }



}
