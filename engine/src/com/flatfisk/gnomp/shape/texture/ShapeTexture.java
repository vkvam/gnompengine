package com.flatfisk.gnomp.shape.texture;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.components.Structure;
import com.flatfisk.gnomp.math.Spatial;

public interface ShapeTexture {
    public abstract void draw(Structure.Node structure, Spatial orientation);
    public abstract Texture createTexture();
    public abstract Vector2 getOffset();
}
