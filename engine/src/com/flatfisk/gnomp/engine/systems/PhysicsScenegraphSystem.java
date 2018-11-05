package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.Scenegraph;
import com.flatfisk.gnomp.engine.components.Spatial;
import static com.flatfisk.gnomp.engine.GnompMappers.*;
/**
Acts on entities with physicsConstructorMap-components and a Scenegraph parent,
PhysicsBody.Container and Light.Container
 */
public class PhysicsScenegraphSystem extends IteratingSystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);

    public PhysicsScenegraphSystem(int priority) {

        super(Family.all(PhysicsBody.Container.class,Scenegraph.Node.class).exclude(Scenegraph.class).get(),priority);

    }

    @Override
    public void update(final float f) {
        super.update(f);
    }

    @Override
    public void processEntity(Entity entity, float f) throws UnsupportedOperationException{

        Spatial.Node childSpatial = spatialNodeMap.get(entity);

        verifyPositionTransfer(childSpatial);

        Scenegraph.Node childNode = scenegraphNodeMap.get(entity);
        Entity parent = childNode.parent;
        PhysicsBody.Container childBodyContainer = physicsBodyMap.get(entity);
        Body childBody = childBodyContainer.body;

        if(childBody!=null) {
            PhysicsBody.Container parentBodyContainer = physicsBodyMap.get(parent);

            if (parentBodyContainer != null) {
                Vector2 childScenegraphPosition = childSpatial.world.vector;
                Vector2 childPhysicsPosition = childBody.getPosition();
                Vector2 childPhysicsLinearVelocity = childBody.getLinearVelocity();
                Vector2 parentVelocity = parentBodyContainer.body.getLinearVelocity();

                float xDiff = (childScenegraphPosition.x * PhysicsConstants.METERS_PER_PIXEL - (childPhysicsPosition.x)) / f;
                float yDiff = (childScenegraphPosition.y * PhysicsConstants.METERS_PER_PIXEL - (childPhysicsPosition.y)) / f;

                childPhysicsLinearVelocity.x = parentVelocity.x+xDiff;
                childPhysicsLinearVelocity.y = parentVelocity.y+yDiff;

                childBody.setLinearVelocity(childPhysicsLinearVelocity);
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
