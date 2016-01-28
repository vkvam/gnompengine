package com.flatfisk.gnomp.engine.shape.texture;


public abstract class ShapeTextureFactory {
    public abstract ShapeTexture createShapeTexture(TextureCoordinates.BoundingRectangle boundingRectangle);
}
