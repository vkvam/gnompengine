package com.flatfisk.gnomp.shape.texture;


public abstract class ShapeTextureFactory {
    public abstract ShapeTexture createShapeTexture(TextureCoordinates.BoundingRectangle boundingRectangle);
}
