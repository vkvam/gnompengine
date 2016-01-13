package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
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
        this.box2DWorld = box2DWorld;
    }

    public World getBox2DWorld() {
        return box2DWorld;
    }

    @Override
    public void addedToEngine(Engine engine) {
        LOG.info("System added to engine");
        super.addedToEngine(engine);
        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        scenegraphMapper = ComponentMapper.getFor(ScenegraphNode.class);
    }

    @Override
    public void update(float f) {
        box2DWorld.step(f, 3, 3);
        super.update(f);
    }

    @Override
    public void processEntity(Entity entity, float f) {
        PhysicsBody body = physicsBodyMapper.get(entity);
        ScenegraphNode node = scenegraphMapper.get(entity);
        node.localTranslation = body.getTranslation().toWorld();
        node.velocity = body.getVelocity().toWorld();
    }

    @Override
    public void entityAdded(Entity entity) {
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
        return body;
    }

    @Override
    public void entityRemoved(Entity entity) {
        PhysicsBody bodyContainer = physicsBodyMapper.get(entity);
        box2DWorld.destroyBody(bodyContainer.body);
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
