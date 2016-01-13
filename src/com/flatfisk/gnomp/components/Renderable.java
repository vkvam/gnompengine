package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;


public class Renderable implements Component,Pool.Poolable {
    public Vector2 offset;
    public Texture texture;

    @Override
    public void reset() {
        texture.dispose();
        Gdx.app.debug(getClass().getName(), "Disposing texture, " + texture.toString());
        texture = null;
        offset = null;
    }
}
