package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.ContactManager;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.PhysicsBodyState;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;
import static com.flatfisk.gnomp.engine.GnompMappers.*;

public class PhysicsSystem extends IteratingSystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);

    private World box2DWorld;
    private boolean fixedStep=false;
    private float fixedStepInterval = 1f/60f;
    private StatsSystem statsSystem;

    public ContactManager manager = new ContactManager();

    public PhysicsSystem(World box2DWorld,int priority) {
        super(Family.all(PhysicsBody.Container.class).get(),priority);

        this.box2DWorld = box2DWorld;
        this.box2DWorld.setContactListener(manager);
        box2DWorld.setContinuousPhysics(true);

    }

    public World getBox2DWorld() {
        return box2DWorld;
    }
    public void setStatsSystem(StatsSystem statsSystem){
        this.statsSystem = statsSystem;
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
        box2DWorld.step(fixedStepInterval, 5, 8);
        super.update(f);
        addStats();
    }

    private void addStats(){
        if(statsSystem!=null){
            statsSystem.addStat("Physics");
            statsSystem.addStat("Bodies:"+box2DWorld.getBodyCount());
            statsSystem.addStat("Fixtures:"+box2DWorld.getFixtureCount());
            statsSystem.addStat("Joints:"+box2DWorld.getJointCount());
            statsSystem.addStat("Proxies:"+box2DWorld.getProxyCount());
            statsSystem.addLine();
        }
    }

    @Override
    public void processEntity(Entity entity, float f) {
        PhysicsBody.Container body = physicsBodyMap.get(entity);
        if(body.body!=null) {

            // Apply manually defined angular change
            if( body.angleModificationType!=PhysicsBody.Container.AngleModificationType.NONE ) {

                switch (body.angleModificationType) {
                    case IMPULSE:
                        body.body.applyAngularImpulse(body.angularModification, body.wake);
                        break;
                    case TORQUE:
                        body.body.applyTorque(body.angularModification, body.wake);
                        break;
                }

                body.angleModificationType = PhysicsBody.Container.AngleModificationType.NONE;
                body.wake = false;
            }

            // Apply manually defined position change
            //if(body.positionModificationType != PhysicsBody.Container.PositionModificationType.TRANSFORM ){

            switch(body.positionModificationType){
                case FORCE_AT_CENTER:
                    body.body.applyForceToCenter(body.modifyDirection, body.wake);
                    break;
                case FORCE_AT_POINT:
                    body.body.applyForce(body.modifyDirection, body.modifyPoint, body.wake);
                    break;
                case IMPULSE_AT_POINT:
                    body.body.applyLinearImpulse(body.modifyDirection, body.modifyPoint, body.wake);
                    break;
                case TRANSFORM:
                    body.body.setTransform(body.modifyDirection, body.angularModification);
                    break;
                case POSITION:
                    body.body.setTransform(body.modifyDirection, body.body.getAngle());
                    break;
            }

            body.positionModificationType = PhysicsBody.Container.PositionModificationType.NONE;
            body.wake = false;

            //}

            // Transfer physicsBodyState and position to correct components.
            PhysicsBodyState physicsBodyState = physicsBodyStateMap.get(entity);

            if (physicsBodyState != null) {
                physicsBodyState.velocity.vector.set(body.getLinearVelocity());
                physicsBodyState.velocity.rotation = body.getAngularVelocity();
                physicsBodyState.intertia = body.body.getInertia();
                physicsBodyState.mass = body.body.getMass();
                // NOTE: Removed since we almost always convert it back to box2d space
                //physicsBodyState.velocity.toWorld();
            }

            Spatial.Node orientation = spatialNodeMap.get(entity);
            Transform world = orientation.world;
            world.vector.set(body.getPosition());
            world.rotation = body.getAngle();
            world.toWorld();

        }
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        Gdx.app.debug(getClass().getName(),"Disposing physicsConstructorMap world");
        box2DWorld.dispose();
    }
}
