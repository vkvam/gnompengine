package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Light;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.Scenegraph;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;
/**
Acts on entities with physics-components and a Scenegraph parent,
PhysicsBody.Container and Light.Container
 */
public class PhysicsScenegraphSystem extends IteratingSystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ComponentMapper<PhysicsBody.Container> physicsBodyMapper;
    private ComponentMapper<Light.Container> lightMapper;
    private ComponentMapper<Scenegraph.Node> scenegraphNodeComponentMapper;
    private ComponentMapper<Spatial.Node> orientationMapper;

    public PhysicsScenegraphSystem(int priority) {

        super(Family.all(PhysicsBody.Container.class,Scenegraph.Node.class).exclude(Scenegraph.class).get(),priority);

        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.Container.class);
        lightMapper = ComponentMapper.getFor(Light.Container.class);
        orientationMapper = ComponentMapper.getFor(Spatial.Node.class);
        scenegraphNodeComponentMapper = ComponentMapper.getFor(Scenegraph.Node.class);

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
    public void processEntity(Entity entity, float f) throws UnsupportedOperationException{

        Spatial.Node entitySpatial = orientationMapper.get(entity);

        verifyPositionTransfer(entitySpatial);

        Scenegraph.Node node = scenegraphNodeComponentMapper.get(entity);
        Entity parent = node.parent;


        PhysicsBody.Container body = physicsBodyMapper.get(entity);
        Transform entityOrientation = entitySpatial.world.getCopy().toBox2D();

        if(body.body!=null) {
            PhysicsBody.Container parentBody = physicsBodyMapper.get(parent);

            if (parentBody != null) {
                Body b = body.body;
                com.badlogic.gdx.physics.box2d.Transform t2 = b.getTransform();
                float xDiff = (entityOrientation.vector.x - (t2.getPosition().x)) / f;
                float yDiff = (entityOrientation.vector.y - (t2.getPosition().y)) / f;
                b.setLinearVelocity(parentBody.body.getLinearVelocity().cpy().add(xDiff, yDiff));
            }
        }
    }

    private void verifyPositionTransfer(Spatial.Node orientation){

        if(orientation.inheritFromParentType == Spatial.Node.SpatialInheritType.POSITION_ANGLE) {
            String msg = "inheritFromParentType should be "+ Spatial.Node.SpatialInheritType.POSITION+
                    " when used with "+this.getClass();
            throw new UnsupportedOperationException(msg);
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
