package com.flatfisk.gnomp.shape.texture;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.math.Spatial;

public interface ShapeTexture {
    public abstract void draw(StructureRelative structure, Spatial orientation);
    public abstract Texture createTexture();
    public abstract Vector2 getOffset();
}
