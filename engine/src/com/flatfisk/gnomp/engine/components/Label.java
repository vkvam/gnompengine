package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Label implements Pool.Poolable, Component {
    public String text;
    public Vector2 offset = new Vector2();
    public float scale = 1;

    @Override
    public void reset() {
        offset.setZero();
        text = null;
    }
}
