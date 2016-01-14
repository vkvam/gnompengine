package com.github.vkvam.gnompengine.components.roots;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.github.vkvam.gnompengine.components.relatives.StructureRelative;
import com.github.vkvam.gnompengine.math.Translation;

public class PhysicsBodyDef implements  Component, Pool.Poolable {
    public BodyDef bodyDef;
    public Array<FixtureDef> fixtureDefs;

    public PhysicsBodyDef() {
        fixtureDefs = new Array<FixtureDef>();
        bodyDef = new BodyDef();
    }

    public void addFixtures(StructureRelative structure,Translation translation) {
        if (structure.shape != null) {
            FixtureDef[] structureFixtureDefs = structure.getFixtureDefinitions(translation);
            fixtureDefs.addAll(structureFixtureDefs);
        }

    }


    @Override
    public void reset() {
        bodyDef = null;
        fixtureDefs.clear();
    }

}
