package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
<<<<<<< HEAD
import com.badlogic.gdx.ApplicationListener;
=======
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
<<<<<<< HEAD
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Velocity;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.OrientationRelative;
import com.flatfisk.gnomp.components.roots.PhysicsBodyDef;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphNode;
import com.flatfisk.gnomp.math.Translation;

public class PhysicsSystem extends IteratingSystem implements EntityListener, ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<PhysicsBodyDef> physicsBodyDefMapper;
    private ComponentMapper<PhysicsBody> physicsBodyMapper;
    private ComponentMapper<OrientationRelative> orientationMapper;
    private ComponentMapper<Velocity> velocityMapper;
    private ComponentMapper<ScenegraphNode> scenegraphNodeComponentMapper;

    private World box2DWorld;

    public PhysicsSystem(World box2DWorld,int priority) {
        super(Family.all(PhysicsBody.class).get(),priority);
=======
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.PhysicsBody;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.gdx.GdxSystem;
import com.flatfisk.gnomp.math.Translation;

public class PhysicsSystem extends IteratingSystem implements EntityListener, GdxSystem {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<PhysicsBody> physicsBodyMapper;
    private ComponentMapper<ScenegraphNode> scenegraphMapper;
    private World box2DWorld;

    public PhysicsSystem(World box2DWorld,int priority) {
        super(Family.all(Root.class,PhysicsBody.class).get(),priority);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
        this.box2DWorld = box2DWorld;
    }

    public World getBox2DWorld() {
        return box2DWorld;
    }

    @Override
    public void addedToEngine(Engine engine) {
        LOG.info("System added to engine");
        super.addedToEngine(engine);
<<<<<<< HEAD
        physicsBodyDefMapper = ComponentMapper.getFor(PhysicsBodyDef.class);
        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        orientationMapper = ComponentMapper.getFor(OrientationRelative.class);
        velocityMapper = ComponentMapper.getFor(Velocity.class);
        scenegraphNodeComponentMapper = ComponentMapper.getFor(ScenegraphNode.class);
    }

    @Override
    public void update(final float f) {
=======
        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        scenegraphMapper = ComponentMapper.getFor(ScenegraphNode.class);
    }

    @Override
    public void update(float f) {
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
        box2DWorld.step(f, 3, 3);
        super.update(f);
    }

    @Override
    public void processEntity(Entity entity, float f) {
        PhysicsBody body = physicsBodyMapper.get(entity);
<<<<<<< HEAD

        if(body.body!=null) {
            Velocity velocity = velocityMapper.get(entity);
            if (velocity != null) {
                velocity.velocity = body.getVelocity().toWorld().getCopy();
            }

            OrientationRelative orientation = orientationMapper.get(entity);
            orientation.worldTranslation = body.getTranslation().getCopy().toWorld();
        }
=======
        ScenegraphNode node = scenegraphMapper.get(entity);
        node.localTranslation = body.getTranslation().toWorld();
        node.velocity = body.getVelocity().toWorld();
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

    @Override
    public void entityAdded(Entity entity) {
<<<<<<< HEAD

            LOG.info("Entity added");
            Velocity velocity = velocityMapper.get(entity);

            PhysicsBodyDef bodyDefContainer = physicsBodyDefMapper.get(entity);
            Array<FixtureDef> fixtureDefs = bodyDefContainer.fixtureDefs;
            BodyDef bodyDef = bodyDefContainer.bodyDef;

            PhysicsBody bodyContainer = physicsBodyMapper.get(entity);
            bodyContainer.body = createBody(bodyDef,fixtureDefs);
            bodyContainer.body.setUserData(entity);

            if(velocity!=null && velocity.velocity != null){
                Translation nodeVelocityBox2D = velocity.velocity.getCopy().toBox2D();
                bodyContainer.body.setLinearVelocity(nodeVelocityBox2D.position);
                bodyContainer.body.setAngularVelocity(nodeVelocityBox2D.angle);
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
=======
        if(getFamily().matches(entity)) {
            ScenegraphNode node = scenegraphMapper.get(entity);
            PhysicsBody bodyContainer = physicsBodyMapper.get(entity);

            Translation nodePositionBox2D = node.localTranslation.getCopy().toBox2D();
            Translation nodeVelocityBox2D = node.velocity == null ? new Translation(0, 0, 0) : node.velocity.getCopy().toBox2D();

            BodyDef bodyDef = bodyContainer.bodyDef;
            bodyDef.position.set(nodePositionBox2D.position);
            bodyDef.angle = nodePositionBox2D.angle;
            bodyContainer.body = createBody(bodyContainer, nodeVelocityBox2D);
        }
    }

    private Body createBody(PhysicsBody bodyContainer,Translation velocities) {
        Body body = box2DWorld.createBody(bodyContainer.bodyDef);
        for (FixtureDef def : bodyContainer.fixtureDefs) {
            body.createFixture(def);
            def.shape.dispose();
        }
        body.setLinearVelocity(velocities.position);
        body.setAngularVelocity(velocities.angle);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
        return body;
    }

    @Override
    public void entityRemoved(Entity entity) {
<<<<<<< HEAD

=======
        PhysicsBody bodyContainer = physicsBodyMapper.get(entity);
        box2DWorld.destroyBody(bodyContainer.body);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
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
