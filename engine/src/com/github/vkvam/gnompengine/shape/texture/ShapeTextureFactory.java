package com.github.vkvam.gnompengine.shape.texture;


public abstract class ShapeTextureFactory {
    public abstract ShapeTexture createShapeTexture(TextureCoordinates.BoundingRectangle boundingRectangle);
}
