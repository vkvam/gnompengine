package com.flatfisk.gnomp.engine.shape.texture;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.math.Transform;

public interface ShapeTexture {
    void draw(Shape structure, Transform orientation);
    Texture createTexture();
    Vector2 getOffset();
}
