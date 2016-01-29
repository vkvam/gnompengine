package com.flatfisk.gnomp.engine.shape.texture;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.engine.components.Geometry;
import com.flatfisk.gnomp.math.Transform;

public interface ShapeTexture {
    public abstract void draw(Geometry structure, Transform orientation);
    public abstract Texture createTexture();
    public abstract Vector2 getOffset();
}
