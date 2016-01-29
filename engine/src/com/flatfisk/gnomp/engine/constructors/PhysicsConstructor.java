package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.shape.AbstractShape;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class PhysicsConstructor extends Constructor<PhysicsBody,PhysicsBody.Node,Array<FixtureDef>> {
    private final World box2DWorld;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ComponentMapper<Shape> structureMapper;
    private ComponentMapper<Velocity> velocityMapper;
    private ComponentMapper<PhysicalProperties> physicalPropertiesMapper;
    private GnompEngine engine;

    public PhysicsConstructor(GnompEngine engine,World box2DWorld) {
        super(PhysicsBody.class, PhysicsBody.Node.class);
        this.engine = engine;
        this.box2DWorld = box2DWorld;
        structureMapper = ComponentMapper.getFor(Shape.class);
        velocityMapper = ComponentMapper.getFor(Velocity.class);
        physicalPropertiesMapper = ComponentMapper.getFor(PhysicalProperties.class);
    }

    @Override
    public void parentAddedFinal(Entity entity, Spatial.Node constructorOrientation, Array<FixtureDef> physicsBodyDef) {

        Transform worldTransform = constructorOrientation.world.getCopy().toBox2D();

        BodyDef bodyDef = constructorMapper.get(entity).bodyDef;
        bodyDef.position.set(worldTransform.vector);
        bodyDef.angle = worldTransform.rotation;

        PhysicsBody.Container bodyContainer = engine.addComponent(PhysicsBody.Container.class,entity);
        bodyContainer.body = createBody(bodyDef,physicsBodyDef);
        bodyContainer.body.setUserData(entity);

        Velocity velocity = velocityMapper.get(entity);
        if(velocity!=null && velocity.velocity != null){
            Transform velocityTransform = velocity.velocity.getCopy().toBox2D();
            bodyContainer.body.setLinearVelocity(velocityTransform.vector);
            bodyContainer.body.setAngularVelocity(velocityTransform.rotation);
        }

    }

    @Override
    public Array<FixtureDef> parentAdded(Entity entity, Spatial.Node constructor) {
        Array<FixtureDef> fixtureDefs = new Array<FixtureDef>();
        FixtureDef[] fixtures = getFixtures(structureMapper.get(entity),Pools.obtainSpatial(), physicalPropertiesMapper.get(entity));
        if(fixtures!=null){
            fixtureDefs.addAll(fixtures);
        }
        return fixtureDefs;
    }

    @Override
    public Array<FixtureDef> insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, Array<FixtureDef> bodyDefContainer) {
        Transform transform = childOrientation.world.subtractedCopy(constructorOrientation.world);
        Shape shape = structureMapper.get(entity);
        if(relationshipMapper.get(entity).intermediate) {
            FixtureDef[] fixtures = getFixtures(shape, transform, physicalPropertiesMapper.get(entity));
            if(fixtures!=null){
                bodyDefContainer.addAll(fixtures);
            }
        }
        return bodyDefContainer;
    }

    @Override
    public void parentRemoved(Entity entity) {
        entity.remove(PhysicsBody.Container.class);
    }

    @Override
    public void childRemoved(Entity entity) {

    }

    private Body createBody(BodyDef bodyDef, Array<FixtureDef> fixtureDefs) {
        LOG.info("Create physics body of type:"+bodyDef.type);
        Body body = box2DWorld.createBody(bodyDef);
        LOG.info("Adding "+fixtureDefs.size+" fixtures");
        for (FixtureDef fixture : fixtureDefs) {
            body.createFixture(fixture);
            fixture.shape.dispose();
        }
        LOG.info("Total mass:"+body.getMass());
        return body;
    }

    public FixtureDef[] getFixtures(Shape structure,Transform transform, PhysicalProperties physicalProperties) {
        if (structure.geometry != null) {
            FixtureDef[] structureFixtureDefs  = getFixtureDefinitions(structure.geometry, transform,physicalProperties);
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
}
