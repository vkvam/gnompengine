package com.flatfisk.gnomp.components.constructed;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;


public class Renderable implements Component,Pool.Poolable {
    public Vector2 offset;
    public Texture texture;
    public int zIndex = 0;

    @Override
    public void reset() {

        Gdx.app.debug(getClass().getName(), "Disposing texture, " + texture.toString());
        texture.dispose();
        texture = null;

        offset = null;
        zIndex = 0;
    }
}
