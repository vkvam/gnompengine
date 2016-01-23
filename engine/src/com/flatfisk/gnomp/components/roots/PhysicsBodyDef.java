package com.flatfisk.gnomp.components.roots;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.ConstructorComponent;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.math.Spatial;

public class PhysicsBodyDef implements ConstructorComponent<PhysicsBodyDef>, Pool.Poolable {
    // TODO: ConstructorComponents should not change!
    // So, the FixtureDef part should be moved to a constructed component.

    public BodyDef bodyDef;
    public Array<FixtureDef> fixtureDefs;

    public PhysicsBodyDef() {
        fixtureDefs = new Array<FixtureDef>();
        bodyDef = new BodyDef();
    }

    public void addFixtures(StructureRelative structure,Spatial spatial) {
        if (structure.shape != null) {
            FixtureDef[] structureFixtureDefs = structure.getFixtureDefinitions(spatial);
            fixtureDefs.addAll(structureFixtureDefs);
        }

    }


    @Override
    public void reset() {
        bodyDef = null;
        fixtureDefs.clear();
    }

    @Override
    public PhysicsBodyDef addCopy(GnompEngine gnompEngine, Entity entity) {
        PhysicsBodyDef physicsBodyDef = gnompEngine.addComponent(getClass(),entity);
        physicsBodyDef.bodyDef = bodyDef;
        // TODO: A constructed value, should not be on this component.
        physicsBodyDef.fixtureDefs.clear(); //= fixtureDefs;
        return physicsBodyDef;
    }
}
