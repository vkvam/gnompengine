package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.relatives.PhysicsBodyRelative;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.components.roots.PhysicsBodyDef;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class PhysicsConstructor extends Constructor<PhysicsBodyDef,PhysicsBodyRelative,PhysicsBodyDef> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public PooledEngine engine;
    public ComponentMapper<StructureRelative> structureMapper;

    public PhysicsConstructor(PooledEngine engine) {
        super(engine,PhysicsBodyDef.class, PhysicsBodyRelative.class);
        this.engine = engine;
        structureMapper = ComponentMapper.getFor(StructureRelative.class);
    }

    @Override
    public void parentAddedFinal(Entity entity, PhysicsBodyDef physicsBodyDef) {
        LOG.info("Finalizing physicsbody construction");
        PhysicsBody physicsBody = engine.createComponent(PhysicsBody.class);
        entity.add(physicsBody);
    }

    @Override
    public PhysicsBodyDef parentAdded(Entity entity, SpatialRelative rootOrientation, SpatialRelative constructor) {
        PhysicsBodyDef bodyContainer = constructorMapper.get(entity);

        Spatial t = constructor.worldSpatial.getCopy().toBox2D();

        LOG.info("Inserting parent at vector:"+t.vector);

        bodyContainer.bodyDef.position.set(t.vector);
        bodyContainer.bodyDef.angle = t.rotation;

        // If the constructor has fixtures, they should be drawn at origin.
        Spatial fixtureOffset = Pools.obtainSpatial();
        bodyContainer.addFixtures(structureMapper.get(entity),fixtureOffset);

        return bodyContainer;
    }

    @Override
    public PhysicsBodyDef insertedChild(Entity entity, SpatialRelative rootOrientation, SpatialRelative constructorOrientation, SpatialRelative parentOrientation, SpatialRelative childOrientation, PhysicsBodyDef bodyDefContainer) {

        // Use vector relativeType to constructor.
        Spatial spatial = childOrientation.worldSpatial.subtractedCopy(constructorOrientation.worldSpatial);
        LOG.info("Inserting child at vector:"+ spatial.vector);

        bodyDefContainer.addFixtures(structureMapper.get(entity), spatial);

        return bodyDefContainer;
    }


}
