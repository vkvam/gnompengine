package com.github.vkvam.gnompengine.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Logger;
import com.github.vkvam.gnompengine.components.constructed.PhysicsBody;
import com.github.vkvam.gnompengine.components.relatives.OrientationRelative;
import com.github.vkvam.gnompengine.components.relatives.PhysicsBodyRelative;
import com.github.vkvam.gnompengine.components.relatives.StructureRelative;
import com.github.vkvam.gnompengine.components.roots.PhysicsBodyDef;
import com.github.vkvam.gnompengine.math.Translation;

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
    public PhysicsBodyDef parentAdded(Entity entity, OrientationRelative rootOrientation, OrientationRelative constructor) {
        PhysicsBodyDef bodyContainer = constructorMapper.get(entity);

        Translation t = constructor.worldTranslation.getCopy().toBox2D();

        LOG.info("Inserting parent at position:"+t.position);

        bodyContainer.bodyDef.position.set(t.position);
        bodyContainer.bodyDef.angle = t.angle;

        // If the constructor has fixtures, they should be drawn at origin.
        Translation fixtureOffset = new Translation();
        bodyContainer.addFixtures(structureMapper.get(entity),fixtureOffset);

        return bodyContainer;
    }

    @Override
    public PhysicsBodyDef insertedChild(Entity entity, OrientationRelative rootOrientation, OrientationRelative constructorOrientation, OrientationRelative parentOrientation, OrientationRelative childOrientation, PhysicsBodyDef bodyDefContainer) {

        // Use position relativeType to constructor.
        Translation translation = childOrientation.worldTranslation.subtractCopy(constructorOrientation.worldTranslation);
        LOG.info("Inserting child at position:"+translation.position);

        bodyDefContainer.addFixtures(structureMapper.get(entity), translation);

        return bodyDefContainer;
    }


}
