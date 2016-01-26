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

    public ComponentMapper<StructureRelative> structureMapper;
    private ComponentMapper<Velocity> velocityMapper;
    private ComponentMapper<PhysicsBody> bodyMapper;

    public PhysicsConstructor(GnompEngine engine,World box2DWorld) {
        super(engine,PhysicsBodyDef.class, PhysicsBodyRelative.class);
        structureMapper = ComponentMapper.getFor(StructureRelative.class);
        velocityMapper = ComponentMapper.getFor(Velocity.class);
        bodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        this.box2DWorld = box2DWorld;
    }

    @Override
    public void parentAddedFinal(Entity entity, SpatialRelative constructorOrientation, Array<FixtureDef> physicsBodyDef) {
        LOG.info("Finalizing physicsbody construction");
        LOG.info("ADDED BODY");
        engine.addComponent(PhysicsBody.class,entity);


        Velocity velocity = velocityMapper.get(entity);

        PhysicsBodyDef bodyDefContainer = constructorMapper.get(entity);
        BodyDef bodyDef = bodyDefContainer.bodyDef;

        Spatial t = constructorOrientation.world.getCopy().toBox2D();
        bodyDef.position.set(t.vector);
        bodyDef.angle = t.rotation;

        PhysicsBody bodyContainer = engine.addComponent(PhysicsBody.class,entity);//physicsBodyMapper.get(entity);
        bodyContainer.body = createBody(bodyDef,physicsBodyDef);
        bodyContainer.body.setUserData(entity);


        if(velocity!=null && velocity.velocity != null){
            LOG.info("Velocity:"+velocity.velocity.vector.x+","+velocity.velocity.vector.y);
            Spatial nodeVelocityBox2D = velocity.velocity.getCopy().toBox2D();
            bodyContainer.body.setLinearVelocity(nodeVelocityBox2D.vector);
            bodyContainer.body.setAngularVelocity(nodeVelocityBox2D.rotation);
        }

    }

    @Override
    public Array<FixtureDef> parentAdded(Entity entity, SpatialRelative constructor) {
        Array<FixtureDef> fixtureDefs = new Array<FixtureDef>();

        //PhysicsBodyDef bodyContainer = constructorMapper.get(entity);

        Spatial t = constructor.world.getCopy().toBox2D();

        LOG.info("Inserting parent at vector:"+t.vector);
        //bodyContainer.bodyDef.position.set(t.vector);
        //bodyContainer.bodyDef.angle = t.rotation;

        // If the constructor has fixtures, they should be drawn at origin.
        // TODO: This should always be empty, before this stage !!!!
        //bodyContainer.fixtureDefs.clear();
        //bodyContainer.addFixtures(structureMapper.get(entity),Pools.obtainSpatial());
        FixtureDef[] fixtures = getFixtures(structureMapper.get(entity),Pools.obtainSpatial());
        if(fixtures!=null){
            fixtureDefs.addAll(fixtures);
        }
        return fixtureDefs;
    }

    @Override
    public Array<FixtureDef> insertedChild(Entity entity, SpatialRelative constructorOrientation, SpatialRelative parentOrientation, SpatialRelative childOrientation, Array<FixtureDef> bodyDefContainer) {

        // Use vector relativeType to constructor.
        Spatial spatial = childOrientation.world.subtractedCopy(constructorOrientation.world);
        LOG.info("Inserting child at vector:"+ spatial.vector);

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
        PhysicsBody physicsBody = bodyMapper.get(entity);
        if(physicsBody!=null) {
            Body b = physicsBody.body;
            if (b != null) {
                b.setUserData(null);
                box2DWorld.destroyBody(bodyMapper.get(entity).body);
            }
        }

    }

    @Override
    public void childRemoved(Entity entity) {

    }

    private Body createBody(BodyDef bodyDef, Array<FixtureDef> fixtureDefs) {
        LOG.info("CREATING BODY");
        LOG.info("Type:"+bodyDef.type);
        Body body = box2DWorld.createBody(bodyDef);
        LOG.info("Adding "+fixtureDefs.size+" fixtures");
        for (FixtureDef def : fixtureDefs) {
            body.createFixture(def);
            def.shape.dispose();
        }
        LOG.info("Mass:"+body.getMass());
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
