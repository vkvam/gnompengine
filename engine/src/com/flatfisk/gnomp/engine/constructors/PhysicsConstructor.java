package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Entity;
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

public class PhysicsConstructor extends Constructor<PhysicsBody, PhysicsBody.Node, PhysicsBody.Container, Array<PhysicsConstructor.FixtureDefWrapper>> {
    private final World box2DWorld;
    private Logger LOG = new Logger(this.getClass().getName(), Logger.DEBUG);
    private GnompEngine engine;

    public PhysicsConstructor(GnompEngine engine, World box2DWorld) {
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

        Velocity velocity = velocityMap.get(entity);
        if (velocity != null && velocity.velocity != null) {
            LOG.info("VECTOR:" + velocity.velocity.vector);
            Transform velocityTransform = Pools.obtain(Transform.class).set(velocity.velocity).toBox2D();
            bodyContainer.body.setLinearVelocity(velocityTransform.vector);
            bodyContainer.body.setAngularVelocity(velocityTransform.rotation);
            Pools.free(velocityTransform);
        }
    }

    @Override
    public Array<FixtureDefWrapper> parentAdded(Entity entity, Spatial.Node constructor) {
        Array<FixtureDefWrapper> fixtureDefs = new Array<FixtureDefWrapper>();
        Transform t = Pools.obtain(Transform.class);

        FixtureDef[] fixtures = getFixtures(shapeMap.get(entity), t, physicalPropertiesMap.get(entity));
        if (fixtures != null) {
            for (FixtureDef f : fixtures) {
                fixtureDefs.add(new FixtureDefWrapper(entity, f, t));
            }
        }

        Pools.free(t);
        return fixtureDefs;

    }

    @Override
    public Array<FixtureDefWrapper> insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, Array<FixtureDefWrapper> fixtureDefDTO) {
        Transform t = Pools.obtain(Transform.class).set(childOrientation.world).subtract(constructorOrientation.world);
        t.vector.rotate(-constructorOrientation.world.rotation);
        Shape shape = shapeMap.get(entity);
        if (relationshipMapper.get(entity).intermediate) {
            FixtureDef[] childFixtureDefs = getFixtures(shape, t, physicalPropertiesMap.get(entity));
            if (childFixtureDefs != null) {
                for (FixtureDef childFixture : childFixtureDefs) {
                    fixtureDefDTO.add(new FixtureDefWrapper(entity, childFixture, t));
                }
            }
        }
        Pools.free(t);
        return fixtureDefDTO;
    }

    @Override
    public void parentRemoved(Entity entity) {
        entity.remove(PhysicsBody.Container.class);
    }

    @Override
    public void childRemoved(Entity entity) {

    }

    private Body createBody(BodyDef bodyDef, Array<FixtureDefWrapper> fixtureDefs) {
        LOG.info("Create physicsConstructorMap body of type:" + bodyDef.type);
        Body body = box2DWorld.createBody(bodyDef);
        LOG.info("Adding " + fixtureDefs.size + " fixtures");
        for (FixtureDefWrapper fixtureDefWrapper : fixtureDefs) {
            Fixture fixture = body.createFixture(fixtureDefWrapper.fixtureDef);
            fixtureDefWrapper.fixtureDef.shape.dispose();
            // Use the entity used for construction and it's relative position to the parent body as userdata.
            fixture.setUserData(new FixtureUserData(fixtureDefWrapper.owner, fixtureDefWrapper.transformRelativeToBody));
        }
        LOG.info("Total mass:" + body.getMass());
        return body;
    }

    private FixtureDef[] getFixtures(Shape structure, Transform transform, PhysicalProperties physicalProperties) {
        if (structure.getGeometry() != null) {
            FixtureDef[] structureFixtureDefs = getFixtureDefinitions(structure.getGeometry(), transform, physicalProperties);
            return structureFixtureDefs;
        }
        return null;
    }

    private FixtureDef[] getFixtureDefinitions(AbstractShape abstractShape, Transform transform, PhysicalProperties physicalProperties) {

        abstractShape.setRotation(transform.rotation);
        FixtureDef[] fixtureDefinitions = abstractShape.getFixtureDefinitions(transform.vector);

        for (FixtureDef fixtureDef : fixtureDefinitions) {

            fixtureDef.density = physicalProperties.density;
            fixtureDef.isSensor = physicalProperties.isSensor;
            fixtureDef.friction = physicalProperties.friction;
            fixtureDef.filter.categoryBits = physicalProperties.categoryBits;
            fixtureDef.filter.maskBits = physicalProperties.maskBits;
        }

        abstractShape.setRotation(0);
        return fixtureDefinitions;
    }

    protected static class FixtureUserData {
        final Transform transformRelativeToBody;
        final Entity owner;

        FixtureUserData(Entity owner, Transform transformRelativeToBody) {
            this.owner = owner;
            this.transformRelativeToBody = transformRelativeToBody;
        }
    }

    static class FixtureDefWrapper {
        Entity owner;
        FixtureDef fixtureDef;
        final Transform transformRelativeToBody;

        FixtureDefWrapper(Entity owner, FixtureDef fixtureDef, Transform transformRelativeToBody) {
            this.owner = owner;
            this.fixtureDef = fixtureDef;
            this.transformRelativeToBody = transformRelativeToBody;
        }
    }
}
