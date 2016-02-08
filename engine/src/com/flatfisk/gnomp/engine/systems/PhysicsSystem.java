package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
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
    private float fixedStepInterval = 1/60f;

    public PhysicsSystem(World box2DWorld,int priority) {
        super(Family.all(PhysicsBody.Container.class).get(),priority);
        this.box2DWorld = box2DWorld;
    }

    public World getBox2DWorld() {
        return box2DWorld;
    }

    public void setFixedStep(boolean fixedStep) {
        this.fixedStep = fixedStep;
    }

    public void setFixedStepInterval(float fixedStepInterval) {
        this.fixedStepInterval = fixedStepInterval;
    }

    @Override
    public void addedToEngine(Engine engine) {
        LOG.info("System added to engine");
        super.addedToEngine(engine);
        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.Container.class);
        orientationMapper = ComponentMapper.getFor(Spatial.Node.class);
        velocityMapper = ComponentMapper.getFor(Velocity.class);
    }

    @Override
    public void update(final float f) {
        box2DWorld.step(fixedStep?fixedStepInterval:f,3,3);
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
