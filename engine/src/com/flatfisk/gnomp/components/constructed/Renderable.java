package com.flatfisk.gnomp.components.constructed;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;


public class Renderable implements Component,Pool.Poolable {
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
