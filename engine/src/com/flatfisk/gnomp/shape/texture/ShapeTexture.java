package com.flatfisk.gnomp.shape.texture;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.components.Geometry;
import com.flatfisk.gnomp.math.Transform;

public interface ShapeTexture {
    public abstract void draw(Geometry.Node structure, Transform orientation);
    public abstract Texture createTexture();
    public abstract Vector2 getOffset();
}
