package com.flatfisk.gnomp.components.roots;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.math.Spatial;

public class PhysicsBodyDef implements  Component, Pool.Poolable {
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

}
