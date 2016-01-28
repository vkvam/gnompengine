package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.shape.Shape;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;


public class Geometry implements ISerializable<Geometry>,Pool.Poolable {

    @Override
    public void reset() {
    }

    @Override
    public Geometry addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }

    public static class Node implements ISerializable<Node> {
        public Shape shape;
        public TextureCoordinates.BoundingRectangle boundingRectangle;
        public boolean intermediate = false;

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
        }

        public Node addCopy(GnompEngine gnompEngine,Entity entity){
            Node relative = gnompEngine.addComponent(getClass(),entity);

            relative.boundingRectangle = boundingRectangle;
            relative.shape = shape.getCopy();
            relative.shape.fillColor = shape.fillColor.cpy();
            relative.shape.lineColor = shape.lineColor.cpy();
            relative.shape.lineWidth = shape.lineWidth;

            return relative;
        }
    }
}
