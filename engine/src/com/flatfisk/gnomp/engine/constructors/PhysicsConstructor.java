package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.shape.AbstractShape;
import com.flatfisk.gnomp.math.Transform;
import static com.flatfisk.gnomp.engine.GnompMappers.*;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class PhysicsConstructor extends Constructor<PhysicsBody,PhysicsBody.Node,PhysicsBody.Container, Array<PhysicsConstructor.FixtureDefWrapper>> {
    private final World box2DWorld;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);
    private GnompEngine engine;

    public PhysicsConstructor(GnompEngine engine,World box2DWorld) {
        super(PhysicsBody.class, PhysicsBody.Node.class, PhysicsBody.Container.class);
        this.engine = engine;
        this.box2DWorld = box2DWorld;
    }

    @Override
    public void parentAddedFinal(Entity entity, Spatial.Node constructorOrientation, Array<FixtureDefWrapper> fixtureDefs) {

        Transform worldTransform = Pools.obtain(Transform.class).set(constructorOrientation.world).toBox2D();

        BodyDef bodyDef = constructorMapper.get(entity).bodyDef;
        bodyDef.position.set(worldTransform.vector);
        bodyDef.angle = worldTransform.rotation;

        Pools.free(worldTransform);

        PhysicsBody.Container bodyContainer = engine.addComponent(PhysicsBody.Container.class, entity);
        bodyContainer.body = createBody(bodyDef, fixtureDefs);
        bodyContainer.body.setUserData(entity);

        PhysicsBodyState physicsBodyState = physicsBodyStateMap.get(entity);
        if (physicsBodyState != null && physicsBodyState.velocity != null) {
            Gdx.app.debug(getClass().getName(),"VECTOR:"+ physicsBodyState.velocity.vector);
            //Transform velocityTransform = Pools.obtain(Transform.class).set(physicsBodyState.velocity).toBox2D();
            Transform velocityTransform = Pools.obtain(Transform.class).set(physicsBodyState.velocity);
            bodyContainer.body.setLinearVelocity(velocityTransform.vector);
            bodyContainer.body.setAngularVelocity(velocityTransform.rotation);
            Pools.free(velocityTransform);
        }
        Gdx.app.log(getClass().getName(),"Constructed parent");
    }

    @Override
    public Array<FixtureDefWrapper> parentAdded(Entity entity, Spatial.Node constructor) {
        Array<FixtureDefWrapper> fixtureDefs = new Array<FixtureDefWrapper>();
        Transform t = Pools.obtain(Transform.class);

        FixtureDef[] fixtures = getFixtures(shapeMap.get(entity), t, physicalPropertiesMap.get(entity));
        if (fixtures != null) {
            for(FixtureDef f:fixtures){
                fixtureDefs.add(new FixtureDefWrapper(entity,f, t));
            }
        }

        Pools.free(t);
        return fixtureDefs;

    }

    @Override
    public Array<FixtureDefWrapper> insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, Array<FixtureDefWrapper> fixtureDefs) {
        Transform t = Pools.obtain(Transform.class).set(childOrientation.world).subtract(constructorOrientation.world);
        t.vector.rotate(-constructorOrientation.world.rotation);
        Shape shape = shapeMap.get(entity);
        if(!relationshipMapper.get(entity).intermediate) {
            FixtureDef[] fixtures = getFixtures(shape, t, physicalPropertiesMap.get(entity));
            if(fixtures!=null){
                for(FixtureDef f:fixtures){
                    fixtureDefs.add(new FixtureDefWrapper(entity, f, t));
                }
            }
        }
        Pools.free(t);
        Gdx.app.log(getClass().getName(),"Added child");
        return fixtureDefs;
    }

    @Override
    public void parentRemoved(Entity entity) {
        entity.remove(PhysicsBody.Container.class);
    }

    @Override
    public void childRemoved(Entity entity) {

    }

    private Body createBody(BodyDef bodyDef, Array<FixtureDefWrapper> fixtureDefs) {
        Gdx.app.debug(getClass().getName(),"Create physicsConstructorMap body of type:"+bodyDef.type);
        Body body = box2DWorld.createBody(bodyDef);
        Gdx.app.debug(getClass().getName(),"Adding "+fixtureDefs.size+" fixtures");
        for (FixtureDefWrapper fixture : fixtureDefs) {
            Fixture f = body.createFixture(fixture.fixtureDef);
            fixture.fixtureDef.shape.dispose();
            // Use the entity used for construction and it's relative position to the parent body as userdata.
            f.setUserData(new FixtureUserData(fixture.owner, fixture.transformRelativeToBody));
        }
        Gdx.app.debug(getClass().getName(),"Total mass:"+body.getMass());
        return body;
    }

    public FixtureDef[] getFixtures(Shape structure,Transform transform, PhysicalProperties physicalProperties) {
        if (structure.geometry != null) {
            FixtureDef[] structureFixtureDefs  = getFixtureDefinitions(structure.geometry, transform, physicalProperties);
            return structureFixtureDefs;
        }
        return null;
    }

    private FixtureDef[] getFixtureDefinitions(AbstractShape abstractShape, Transform transform, PhysicalProperties physicalProperties) {

        abstractShape.setRotation(transform.rotation);
        FixtureDef[] fixtureDefinitions = abstractShape.getFixtureDefinitions(transform.vector);

        for(FixtureDef fixtureDef:fixtureDefinitions){

            fixtureDef.density = physicalProperties.density;
            fixtureDef.isSensor = physicalProperties.isSensor;
            fixtureDef.friction = physicalProperties.friction;
            fixtureDef.filter.categoryBits = physicalProperties.categoryBits;
            fixtureDef.filter.maskBits = physicalProperties.maskBits;
        }

        abstractShape.setRotation(0);
        return fixtureDefinitions;
    }

    protected static class FixtureUserData{
        public final Transform transformRelativeToBody;
        public final Entity owner;

        public FixtureUserData(Entity owner, Transform transformRelativeToBody) {
            this.owner = owner;
            this.transformRelativeToBody = transformRelativeToBody;
        }
    }

    protected static class FixtureDefWrapper{
        public Entity owner;
        public FixtureDef fixtureDef;
        public final Transform transformRelativeToBody;

        public FixtureDefWrapper(Entity owner, FixtureDef fixtureDef, Transform transformRelativeToBody) {
            this.owner = owner;
            this.fixtureDef = fixtureDef;
            this.transformRelativeToBody = transformRelativeToBody;
        }
    }
}
