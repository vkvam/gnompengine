package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Constructable;
import com.flatfisk.gnomp.components.PhysicsBody;
import com.flatfisk.gnomp.components.Scenegraph;
import com.flatfisk.gnomp.math.Spatial;

public class PositionPhysicsScenegraphSystem extends IteratingSystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<PhysicsBody.Container> physicsBodyMapper;
    private ComponentMapper<Scenegraph.Node> scenegraphNodeComponentMapper;
    private ComponentMapper<Constructable.Node> orientationMapper;

    public PositionPhysicsScenegraphSystem(int priority) {
        super(Family.all(PhysicsBody.Container.class, Scenegraph.Node.class).exclude(Scenegraph.class).get(),priority);

        physicsBodyMapper = ComponentMapper.getFor(PhysicsBody.Container.class);
        orientationMapper = ComponentMapper.getFor(Constructable.Node.class);
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

        PhysicsBody.Container body = physicsBodyMapper.get(entity);

        if(body.body!=null) {
            Constructable.Node orientation = orientationMapper.get(entity);

            verifyPositionTransfer(orientation);

            Scenegraph.Node node = scenegraphNodeComponentMapper.get(entity);
            Entity parent = node.parent;
            if(parent!=null) {
                PhysicsBody.Container parentBody = physicsBodyMapper.get(parent);
                Body b = body.body;
                if(b!=null && parentBody!=null) {
                    Spatial t = orientation.world.getCopy().toBox2D();
                    Transform t2 = b.getTransform();

                    float xDiff = (t.vector.x - (t2.getPosition().x)) / f;
                    float yDiff = (t.vector.y - (t2.getPosition().y)) / f;

                    b.setLinearVelocity(parentBody.body.getLinearVelocity().cpy().add(xDiff, yDiff));
                }
            }
        }
    }

    private void verifyPositionTransfer(Constructable.Node orientation){

        if(orientation.inheritFromParentType == Constructable.Node.SpatialInheritType.POSITION_ANGLE) {
            String msg = "inheritFromParentType should be "+ Constructable.Node.SpatialInheritType.POSITION+
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
