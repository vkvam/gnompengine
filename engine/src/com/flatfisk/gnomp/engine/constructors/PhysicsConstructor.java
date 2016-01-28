package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.engine.shape.Shape;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class PhysicsConstructor extends Constructor<PhysicsBody,PhysicsBody.Node,Array<FixtureDef>> {
    private final World box2DWorld;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ComponentMapper<Geometry.Node> structureMapper;
    private ComponentMapper<Velocity> velocityMapper;
    private ComponentMapper<PhysicalProperties> physicalPropertiesMapper;

    public PhysicsConstructor(GnompEngine engine,World box2DWorld) {
        super(engine,PhysicsBody.class, PhysicsBody.Node.class);
        structureMapper = ComponentMapper.getFor(Geometry.Node.class);
        velocityMapper = ComponentMapper.getFor(Velocity.class);
        physicalPropertiesMapper = ComponentMapper.getFor(PhysicalProperties.class);
        this.box2DWorld = box2DWorld;
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
        Geometry.Node geometry = structureMapper.get(entity);
        if(!geometry.intermediate && relationshipMapper.get(entity).intermediate) {
            FixtureDef[] fixtures = getFixtures(geometry, transform, physicalPropertiesMapper.get(entity));
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

    public FixtureDef[] getFixtures(Geometry.Node structure,Transform transform, PhysicalProperties physicalProperties) {
        if (structure.shape != null) {
            FixtureDef[] structureFixtureDefs  = getFixtureDefinitions(structure.shape, transform,physicalProperties);
            return structureFixtureDefs;
        }
        return null;
    }

    private FixtureDef[] getFixtureDefinitions(Shape shape, Transform transform, PhysicalProperties physicalProperties) {

        shape.setRotation(transform.rotation);
        FixtureDef[] fixtureDefinitions = shape.getFixtureDefinitions(transform.vector);

        for(FixtureDef fixtureDef:fixtureDefinitions){

            fixtureDef.density = physicalProperties.density;
            fixtureDef.isSensor = physicalProperties.isSensor;
            fixtureDef.friction = physicalProperties.friction;
            fixtureDef.filter.categoryBits = physicalProperties.categoryBits;
            fixtureDef.filter.maskBits = physicalProperties.maskBits;
        }

        shape.setRotation(0);
        return fixtureDefinitions;
    }
}
