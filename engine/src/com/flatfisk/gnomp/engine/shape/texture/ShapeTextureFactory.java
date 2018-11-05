package com.flatfisk.gnomp.engine.shape.texture;


import com.badlogic.gdx.utils.Disposable;

public abstract class ShapeTextureFactory implements Disposable{
    public abstract ShapeTexture createShapeTexture(TextureCoordinates.BoundingRectangle boundingRectangle, int textureId);
}
