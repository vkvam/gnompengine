package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Velocity;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.roots.PhysicsBodyDef;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphNode;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

public class PhysicsSystem extends IteratingSystem implements EntityListener, ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<PhysicsBodyDef> physicsBodyDefMapper;
    private ComponentMapper<PhysicsBody> physicsBodyMapper;
    private ComponentMapper<SpatialRelative> orientationMapper;
    private ComponentMapper<Velocity> velocityMapper;
    private ComponentMapper<ScenegraphNode> scenegraphNodeComponentMapper;

    private World box2DWorld;
    private boolean fixedStep=false;
    private float fixedStepInterval = 1/60f;

    public PhysicsSystem(World box2DWorld,int priority) {
        super(Family.all(PhysicsBody.class).get(),priority);
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
        physicsBodyDefMapper = ComponentMapper.getFor(PhysicsBodyDef.class);
        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        orientationMapper = ComponentMapper.getFor(SpatialRelative.class);
        velocityMapper = ComponentMapper.getFor(Velocity.class);
        scenegraphNodeComponentMapper = ComponentMapper.getFor(ScenegraphNode.class);
    }

    @Override
    public void update(final float f) {
        box2DWorld.step(fixedStep?fixedStepInterval:f,3,3);
        super.update(f);
    }

    @Override
    public void processEntity(Entity entity, float f) {
        PhysicsBody body = physicsBodyMapper.get(entity);
        if(body.body!=null) {
            if(body.positionChanged){
                body.positionChanged = false;
                SpatialRelative relative = orientationMapper.get(entity);
                Spatial spatial = relative.world.toBox2D();
                body.body.setTransform(spatial.vector,spatial.rotation);
            }else {
                Velocity velocity = velocityMapper.get(entity);

                if (velocity != null) {
                    velocity.velocity = Pools.obtainSpatialFromCopy(body.getVelocity().toWorld());
                }

                SpatialRelative orientation = orientationMapper.get(entity);
                orientation.world = body.getTranslation().getCopy().toWorld();
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {

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
        box2DWorld.dispose();
    }

}
