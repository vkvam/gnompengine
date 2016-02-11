package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.components.Velocity;
import com.flatfisk.gnomp.math.Transform;

public class PhysicsSystem extends IteratingSystem {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<PhysicsBody.Container> physicsBodyMapper;
    private ComponentMapper<Spatial.Node> orientationMapper;
    private ComponentMapper<Velocity> velocityMapper;

    private World box2DWorld;
    private boolean fixedStep=false;
    private float fixedStepInterval = 1f/60f;

    public PhysicsSystem(World box2DWorld,int priority) {
        super(Family.all(PhysicsBody.Container.class).get(),priority);
        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.Container.class);
        orientationMapper = ComponentMapper.getFor(Spatial.Node.class);
        velocityMapper = ComponentMapper.getFor(Velocity.class);
        this.box2DWorld = box2DWorld;
    }

    public World getBox2DWorld() {
        return box2DWorld;
    }

    /**
     *
     * @param fixedStep, 0 will turn fixed stepping off.
     */
    public void setFixedStep(float fixedStep) {
        this.fixedStep = !MathUtils.isZero(fixedStep);
        this.fixedStepInterval = fixedStep;
    }


    @Override
    public void update(float f) {
        f = fixedStep ? fixedStepInterval : Math.min(f,fixedStepInterval*2);
        box2DWorld.step(fixedStepInterval, 3, 3);
        super.update(f);
    }

    @Override
    public void processEntity(Entity entity, float f) {
        PhysicsBody.Container body = physicsBodyMapper.get(entity);
        if(body.body!=null) {
            if(body.positionChanged){
                body.positionChanged = false;
                Spatial.Node relative = orientationMapper.get(entity);
                Transform transform = relative.world.toBox2D();
                body.body.setTransform(transform.vector, transform.rotation);
            }else {
                Velocity velocity = velocityMapper.get(entity);

                if (velocity != null) {
                    velocity.velocity.vector.set(body.getLinearVelocity());
                    velocity.velocity.rotation = body.getAngularVelocity();
                    velocity.velocity.toWorld();
                }

                Spatial.Node orientation = orientationMapper.get(entity);
                Transform world = orientation.world;
                world.vector.set(body.getPosition());
                world.rotation = body.getAngle();
                world.toWorld();
            }
        }
    }

}
