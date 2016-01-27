package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.Velocity;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.PhysicsBodyRelative;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.components.roots.PhysicsBodyDef;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class PhysicsConstructor extends Constructor<PhysicsBodyDef,PhysicsBodyRelative,Array<FixtureDef>> {
    private final World box2DWorld;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ComponentMapper<StructureRelative> structureMapper;
    private ComponentMapper<Velocity> velocityMapper;

    public PhysicsConstructor(GnompEngine engine,World box2DWorld) {
        super(engine,PhysicsBodyDef.class, PhysicsBodyRelative.class);
        structureMapper = ComponentMapper.getFor(StructureRelative.class);
        velocityMapper = ComponentMapper.getFor(Velocity.class);
        this.box2DWorld = box2DWorld;
    }

    @Override
    public void parentAddedFinal(Entity entity, SpatialRelative constructorOrientation, Array<FixtureDef> physicsBodyDef) {

        Spatial worldSpatial = constructorOrientation.world.getCopy().toBox2D();

        BodyDef bodyDef = constructorMapper.get(entity).bodyDef;
        bodyDef.position.set(worldSpatial.vector);
        bodyDef.angle = worldSpatial.rotation;

        PhysicsBody bodyContainer = engine.addComponent(PhysicsBody.class,entity);
        bodyContainer.body = createBody(bodyDef,physicsBodyDef);
        bodyContainer.body.setUserData(entity);

        Velocity velocity = velocityMapper.get(entity);
        if(velocity!=null && velocity.velocity != null){
            Spatial velocitySpatial = velocity.velocity.getCopy().toBox2D();
            bodyContainer.body.setLinearVelocity(velocitySpatial.vector);
            bodyContainer.body.setAngularVelocity(velocitySpatial.rotation);
        }

    }

    @Override
    public Array<FixtureDef> parentAdded(Entity entity, SpatialRelative constructor) {
        Array<FixtureDef> fixtureDefs = new Array<FixtureDef>();
        FixtureDef[] fixtures = getFixtures(structureMapper.get(entity),Pools.obtainSpatial());
        if(fixtures!=null){
            fixtureDefs.addAll(fixtures);
        }
        return fixtureDefs;
    }

    @Override
    public Array<FixtureDef> insertedChild(Entity entity, SpatialRelative constructorOrientation, SpatialRelative parentOrientation, SpatialRelative childOrientation, Array<FixtureDef> bodyDefContainer) {
        Spatial spatial = childOrientation.world.subtractedCopy(constructorOrientation.world);
        if(relationshipMapper.get(entity).relativeType == Relative.CHILD) {
            FixtureDef[] fixtures = getFixtures(structureMapper.get(entity),spatial);
            if(fixtures!=null){
                bodyDefContainer.addAll(fixtures);
            }
        }
        return bodyDefContainer;
    }

    @Override
    public void parentRemoved(Entity entity) {
        entity.remove(PhysicsBody.class);
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

    public FixtureDef[] getFixtures(StructureRelative structure,Spatial spatial) {
        if (structure.shape != null) {
            FixtureDef[] structureFixtureDefs = structure.getFixtureDefinitions(spatial);
            return structureFixtureDefs;
        }
        return null;

    }
}
