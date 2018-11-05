package com.flatfisk.gnomp.engine.steering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.flatfisk.gnomp.engine.components.PhysicsSteerable;
import com.flatfisk.gnomp.math.Transform;


public interface Steering {
    Transform steer(Entity entity, Body body, PhysicsSteerable.Container physicsSteering, ShapeRenderer debugRender);

}
