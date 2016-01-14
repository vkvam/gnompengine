package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.OrientationRelative;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphNode;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphRoot;
import com.flatfisk.gnomp.math.Translation;

public class PhysicsTrackerSystem extends IteratingSystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<PhysicsBody> physicsBodyMapper;
    private ComponentMapper<ScenegraphNode> scenegraphNodeComponentMapper;
    private ComponentMapper<OrientationRelative> orientationMapper;

    public PhysicsTrackerSystem(int priority) {
        super(Family.all(PhysicsBody.class, ScenegraphNode.class).exclude(ScenegraphRoot.class).get(),priority);

        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        orientationMapper = ComponentMapper.getFor(OrientationRelative.class);
        scenegraphNodeComponentMapper = ComponentMapper.getFor(ScenegraphNode.class);

    }

    @Override
    public void addedToEngine(Engine engine) {
        LOG.info("System added to engine");
        super.addedToEngine(engine);
    }

    @Override
    public void update(final float f) {
        super.update(f);
    }

    @Override
    public void processEntity(Entity entity, float f) {
        PhysicsBody body = physicsBodyMapper.get(entity);

        if(body.body!=null) {
            OrientationRelative orientation = orientationMapper.get(entity);
            ScenegraphNode node = scenegraphNodeComponentMapper.get(entity);
            Entity parent = node.parent;
            if(parent!=null) {
                PhysicsBody parentBody = physicsBodyMapper.get(parent);
                Body b = body.body;

                Translation t = orientation.worldTranslation.getCopy().toBox2D();
                Transform t2 = b.getTransform();

                float xDiff=(t.position.x-(t2.getPosition().x))/f;
                float yDiff=(t.position.y-(t2.getPosition().y))/f;
                float aDiff=(t.angle-t2.getRotation())/f;

                b.setLinearVelocity(parentBody.body.getLinearVelocity().cpy().add(xDiff,yDiff));
                b.setAngularVelocity(parentBody.body.getAngularVelocity()+aDiff);
            }
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

    }


}
