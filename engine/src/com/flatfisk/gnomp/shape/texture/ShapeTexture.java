package com.flatfisk.gnomp.shape.texture;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.components.StructureNode;
import com.flatfisk.gnomp.systems.NodeSystem;

public interface ShapeTexture extends NodeSystem.IterateDTO {
    public void draw(StructureNode structure);

    public Texture createTexture();

    public Vector2 getOffset();
}
