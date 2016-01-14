package com.github.vkvam.gnompengine.shape.texture;


public class TextureCoordinates {
    public static float MARGIN = 1;
    public float x0, y0, x1, y1;

    public BoundingRectangle getBoundingRectangle() {

        float x0a = x0 - MARGIN,
                y0a = y0 - MARGIN,
                x1a = x1 + MARGIN,
                y1a = y1 + MARGIN;


        float width = x1a - x0a;
        float height = y1a - y0a;

        float centerX = (x0a + width / 2);
        float centerY = (y0a + height / 2);
        BoundingRectangle boundingRectangle = new BoundingRectangle();
        boundingRectangle.width = width;
        boundingRectangle.height = height;
        boundingRectangle.offsetX = centerX;
        boundingRectangle.offsetY = centerY;

        return boundingRectangle;
    }

    public static class BoundingRectangle {
        public float offsetX, offsetY, width, height;
    }
}
