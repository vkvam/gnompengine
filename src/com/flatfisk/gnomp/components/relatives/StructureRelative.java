package com.flatfisk.gnomp.components.relatives;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.RelativeComponent;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.shape.Shape;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;


public class StructureRelative implements RelativeComponent {
    public Relative relativeType = Relative.CHILD;
    public TextureCoordinates.BoundingRectangle boundingRectangle;
    public TextureCoordinates textureCoordinates;
    public Shape shape;
    public float density = 1;
    public float friction = 1;
    public boolean isSensor = false;

    public short categoryBits = 1;
    public short maskBits = 1;

    public StructureRelative(){
    }

    public FixtureDef[] getFixtureDefinitions(Translation translation) {

        shape.setRotation(translation.angle);
        FixtureDef[] fixtureDefinitions = shape.getFixtureDefinitions(translation.position);

        for(FixtureDef fixtureDef:fixtureDefinitions){
            fixtureDef.density = density;
            fixtureDef.isSensor = isSensor;
            fixtureDef.friction = friction;

            fixtureDef.filter.categoryBits = categoryBits;
            fixtureDef.filter.maskBits = maskBits;
        }

        shape.setRotation(0);
        return fixtureDefinitions;
    }

    @Override
    public void reset() {
        shape.dispose();
        boundingRectangle.height=0;
        boundingRectangle.width=0;
        boundingRectangle.offsetX=0;
        boundingRectangle.offsetY=0;
        density = 1;
        relativeType = Relative.CHILD;
    }

    @Override
    public Relative getRelativeType() {
        return relativeType;
    }
}
