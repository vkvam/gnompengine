package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.shape.Shape;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;


public class StructureNode extends Node {
    public Shape shape;
    public Translation worldTranslation;
    public Translation localTranslation;

    public Entity scenegraphRoot;
    public TextureCoordinates.BoundingRectangle boundingRectangle;
    public float density = 1;

    public StructureNode(){
        super(StructureNode.class);
    }

    public FixtureDef[] getFixtureDefinitions() {
        shape.setRotation(worldTranslation.angle);
        FixtureDef[] fixtureDefinitions = shape.getFixtureDefinitions(worldTranslation.position);

        for(FixtureDef fixtureDef:fixtureDefinitions){
            fixtureDef.density = density;
        }

        shape.setRotation(0);
        return fixtureDefinitions;
    }
}
