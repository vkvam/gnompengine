package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Velocity;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.roots.PhysicsBodyDef;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphNode;
import com.flatfisk.gnomp.math.Spatial;

public class PhysicsSystem extends IteratingSystem implements EntityListener, ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<PhysicsBodyDef> physicsBodyDefMapper;
    private ComponentMapper<PhysicsBody> physicsBodyMapper;
    private ComponentMapper<SpatialRelative> orientationMapper;
    private ComponentMapper<Velocity> velocityMapper;
    private ComponentMapper<ScenegraphNode> scenegraphNodeComponentMapper;

    private World box2DWorld;

    public PhysicsSystem(World box2DWorld,int priority) {
        super(Family.all(PhysicsBody.class).get(),priority);
        this.box2DWorld = box2DWorld;
    }

    public World getBox2DWorld() {
        return box2DWorld;
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
        box2DWorld.step(f, 3, 3);
        super.update(f);
    }

    @Override
    public void processEntity(Entity entity, float f) {
        PhysicsBody body = physicsBodyMapper.get(entity);

        if(body.body!=null) {
            if(body.positionChanged){

                Entity entity1 = new Entity();
                for(Component component:entity.getComponents()) {
                    entity1.add(component);
                }
                entity.removeAll();
                getEngine().removeEntity(entity);
                getEngine().addEntity(entity1);
                /*
                box2DWorld.destroyBody(body.body);
                body.body.setUserData(null);
                body.body = null;

                body.positionChanged = false;
                getEngine().removeEntity(entity);
                Entity entity1 = new Entity();
                for(Component component:entity.getComponents()) {
                    entity1.add(component);
                    getEngine().addEntity(entity1);
                }
                */
            }else {
                Velocity velocity = velocityMapper.get(entity);

                if (velocity != null) {
                    velocity.velocity = body.getVelocity().toWorld().getCopy();
                }

                SpatialRelative orientation = orientationMapper.get(entity);
                orientation.worldSpatial = body.getTranslation().getCopy().toWorld();
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {

            LOG.info("Entity added");
            Velocity velocity = velocityMapper.get(entity);

            PhysicsBodyDef bodyDefContainer = physicsBodyDefMapper.get(entity);
            Array<FixtureDef> fixtureDefs = bodyDefContainer.fixtureDefs;
            BodyDef bodyDef = bodyDefContainer.bodyDef;

            PhysicsBody bodyContainer = physicsBodyMapper.get(entity);
            bodyContainer.body = createBody(bodyDef,fixtureDefs);
            bodyContainer.body.setUserData(entity);

            if(velocity!=null && velocity.velocity != null){
                Spatial nodeVelocityBox2D = velocity.velocity.getCopy().toBox2D();
                bodyContainer.body.setLinearVelocity(nodeVelocityBox2D.vector);
                bodyContainer.body.setAngularVelocity(nodeVelocityBox2D.rotation);
            }
    }

    private Body createBody(BodyDef bodyDef, Array<FixtureDef> fixtureDefs) {

        LOG.info("Type:"+bodyDef.type);
        Body body = box2DWorld.createBody(bodyDef);
        LOG.info("Adding "+fixtureDefs.size+" fixtures");
        for (FixtureDef def : fixtureDefs) {
            body.createFixture(def);
            def.shape.dispose();
        }
        LOG.info("Mass:"+body.getMass());
        return body;
    }

    @Override
    public void entityRemoved(Entity entity) {
        LOG.info("Entity removed");
        PhysicsBody body = physicsBodyMapper.get(entity);
        box2DWorld.destroyBody(body.body);
        body.body.setUserData(null);
        body.body = null;
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
