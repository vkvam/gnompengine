package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.abstracts.IRelative;
import com.flatfisk.gnomp.components.abstracts.ISerializable;
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
        public Shape shape;
        public Relative relativeType = Relative.CHILD;
        public TextureCoordinates.BoundingRectangle boundingRectangle;


        public Node(){
            boundingRectangle = new TextureCoordinates.BoundingRectangle();
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

            relative.shape = shape.getCopy();
            relative.shape.fillColor = shape.fillColor.cpy();
            relative.shape.lineColor = shape.lineColor.cpy();
            relative.shape.lineWidth = shape.lineWidth;

            return relative;
        }
    }
}
