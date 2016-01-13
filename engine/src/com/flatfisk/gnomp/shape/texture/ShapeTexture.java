package com.flatfisk.gnomp.shape.texture;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
<<<<<<< HEAD
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.math.Translation;

public interface ShapeTexture {
    public abstract void draw(StructureRelative structure, Translation orientation);
    public abstract Texture createTexture();
    public abstract Vector2 getOffset();
=======
import com.flatfisk.gnomp.components.StructureNode;
import com.flatfisk.gnomp.systems.NodeSystem;

public interface ShapeTexture extends NodeSystem.IterateDTO {
    public void draw(StructureNode structure);

    public Texture createTexture();

    public Vector2 getOffset();
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
}
