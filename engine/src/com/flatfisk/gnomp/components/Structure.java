package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.abstracts.IRelative;
import com.flatfisk.gnomp.components.abstracts.ISerializable;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.shape.Shape;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;


public class Structure implements ISerializable<Structure>,Pool.Poolable {

    @Override
    public void reset() {
    }

    @Override
    public Structure addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }

    public static class Node implements ISerializable<Node>,IRelative {
        public Relative relativeType = Relative.CHILD;
        public TextureCoordinates.BoundingRectangle boundingRectangle;
        public TextureCoordinates textureCoordinates;
        public Shape shape;
        public float density = 1;
        public float friction = 1;
        public boolean isSensor = false;
        public short categoryBits = 1;
        public short maskBits = 1;

        public Node(){
            boundingRectangle = new TextureCoordinates.BoundingRectangle();
            textureCoordinates = new TextureCoordinates();
        }

        public FixtureDef[] getFixtureDefinitions(Spatial spatial) {

            shape.setRotation(spatial.rotation);
            FixtureDef[] fixtureDefinitions = shape.getFixtureDefinitions(spatial.vector);

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
            if(shape!=null) {
                shape.reset();
                shape=null;
            }
            boundingRectangle.height=0;
            boundingRectangle.width=0;
            boundingRectangle.offsetX=0;
            boundingRectangle.offsetY=0;
            textureCoordinates.x0=0;
            textureCoordinates.x1=0;
            textureCoordinates.y0=0;
            textureCoordinates.y1=0;
            density = 1;
            relativeType = Relative.CHILD;
        }

        @Override
        public Relative getRelativeType() {
            return relativeType;
        }

        public Node addCopy(GnompEngine gnompEngine,Entity entity){
            Node relative = gnompEngine.addComponent(getClass(),entity);
            relative.relativeType = relativeType;
            relative.boundingRectangle = boundingRectangle;

            relative.textureCoordinates.x0 = textureCoordinates.x0;
            relative.textureCoordinates.x1 = textureCoordinates.x1;
            relative.textureCoordinates.y0 = textureCoordinates.y0;
            relative.textureCoordinates.y1 = textureCoordinates.y1;

            relative.shape = shape.getCopy();
            relative.shape.fillColor = shape.fillColor.cpy();
            relative.shape.lineColor = shape.lineColor.cpy();
            relative.shape.lineWidth = shape.lineWidth;

            relative.density = density;
            relative.friction = friction;
            relative.isSensor = isSensor;
            relative.categoryBits = categoryBits;
            relative.maskBits = maskBits;
            return relative;
        }
    }
}
