package com.flatfisk.gnomp.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.systems.NodeSystem;

public class PhysicsBody implements NodeSystem.IterateDTO, Component, Pool.Poolable {
    public Body body;
    public BodyDef bodyDef;
    public Array<FixtureDef> fixtureDefs;
    public PhysicsBody() {
        fixtureDefs = new Array<FixtureDef>(1);
    }

    public void addBodyDef(PhysicsConstruction structure) {
        // TODO: Insert more values
        bodyDef = new BodyDef();
        bodyDef.type = structure.bodyType;
    }

    public void addFixtures(StructureNode structure) {
        if (structure.shape != null) {
            FixtureDef[] structureFixtureDefs = structure.getFixtureDefinitions();
            fixtureDefs.addAll(structureFixtureDefs);
        }

    }

    public Translation getTranslation(){
        return new Translation(getPosition().cpy(),getAngle());
    }
    public Translation getVelocity(){
        return new Translation(getLinearVelocity().cpy(),getAngularVelocity());
    }
    private Vector2 getLinearVelocity(){
        return body.getLinearVelocity();
    }
    private float getAngularVelocity(){
        return body.getAngularVelocity();
    }

    private Vector2 getPosition(){
        return body.getPosition();
    }
    private float getAngle(){
        return body.getAngle();
    }

    @Override
    public void reset() {
        body = null;
        bodyDef = null;
        fixtureDefs.clear();
    }
}
