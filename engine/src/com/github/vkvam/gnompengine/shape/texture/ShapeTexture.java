package com.github.vkvam.gnompengine.shape.texture;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.github.vkvam.gnompengine.components.relatives.StructureRelative;
import com.github.vkvam.gnompengine.math.Translation;

public interface ShapeTexture {
    public abstract void draw(StructureRelative structure, Translation orientation);
    public abstract Texture createTexture();
    public abstract Vector2 getOffset();
}
