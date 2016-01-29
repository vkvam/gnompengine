package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;


public class Renderable implements ISerializable<Renderable>,Pool.Poolable {
    public int zIndex=0;
    public TextureCoordinates.BoundingRectangle boundingRectangle;

    @Override
    public void reset() {
    }

    @Override
    public Renderable addCopy(GnompEngine gnompEngine, Entity entity) {
        Renderable renderableDef = gnompEngine.addComponent(getClass(),entity);
        renderableDef.zIndex = zIndex;
        return renderableDef;
    }

    /**
     * Created by Vemund Kvam on 04/12/15.
     */
    public static class Node implements ISerializable<Node> {
        public boolean intermediate = false;

        @Override
        public void reset() {

        }

        public Node addCopy(GnompEngine gnompEngine,Entity entity){
            Node relative = gnompEngine.addComponent(getClass(),entity);
            return relative;
        }
    }

    public static class Constructed implements Component,Pool.Poolable {
        private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
        public Vector2 offset;
        public Texture texture;
        public int zIndex = 0;

        @Override
        public void reset() {
            LOG.info("Disposing texture, " + texture.toString());
            texture.dispose();
            texture = null;

            offset.setZero();
            zIndex = 0;
        }
    }
}
