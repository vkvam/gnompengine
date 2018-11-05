package com.flatfisk.gnomp.engine.shape.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.shape.AbstractShape;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.engine.shape.Polygon;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.math.GeometryUtils;
import com.flatfisk.gnomp.math.Transform;

import static com.flatfisk.gnomp.math.GeometryUtils.toVector2Array;

public class SimpleTextureFactory extends ShapeTextureFactory{
    private IntMap<SimpleShapeTexture> shapeTextureCache = new IntMap<SimpleShapeTexture>(100);

    @Override
    public ShapeTexture createShapeTexture(TextureCoordinates.BoundingRectangle boundingRectangle, int textureId) {
        SimpleShapeTexture t;
        if(textureId>=0 && shapeTextureCache.containsKey(textureId)){
            t = shapeTextureCache.get(textureId);
            t.id = textureId;
        }else {
            t = new SimpleShapeTexture(boundingRectangle);
            if (textureId >= 0) {
                shapeTextureCache.put(textureId, t);
            }
        }
        return t;
    }

    @Override
    public void dispose() {
        // TODO: Keep a counter of textures used and dispose once it reaches 0
        for(SimpleShapeTexture tex: shapeTextureCache.values()){
            tex.texture.dispose();
        }
    }

    public static class SimpleShapeTexture extends Pixmap implements ShapeTexture {
        public Vector2 offset;
        public Texture texture;
        public int id = -1;

        public SimpleShapeTexture(TextureCoordinates.BoundingRectangle envelope) {
            super(Math.round(envelope.width), Math.round(envelope.height), Format.RGBA8888);
            offset = new Vector2(envelope.offsetX, envelope.offsetY);
        }

        @Override
        public boolean isCached() {
            return id>=0;
        }

        public void draw(Shape structure, Transform orientation) {
            if(isCached()){
                return;
            }
            Vector2 pos = orientation.vector;
            AbstractShape abstractShape = structure.getGeometry();
            int centerX = Math.round(pos.x + getWidth() / 2 - offset.x);
            int centerY = Math.round(pos.y + getHeight() / 2 - offset.y);
            //Gdx.app.log("Draw vector:", centerX + "," + centerY);

            if (abstractShape instanceof Circle) {
                if (abstractShape.fillColor != null) {
                    setColor(abstractShape.fillColor);
                    int radius = Math.round(((Circle) abstractShape).circle.radius);
                    fillCircle(centerX, centerY, radius);
                }
                if (abstractShape.lineColor != null) {
                    setColor(abstractShape.lineColor);
                    int radius = Math.round(((Circle) abstractShape).circle.radius);
                    drawCircle(centerX, centerY, radius);
                }

            } else if (abstractShape instanceof RectangularLine) {
                if (abstractShape.fillColor != null) {
                    setColor(abstractShape.fillColor);
                    RectangularLine ls = (RectangularLine) abstractShape;
                    ls.setRotation(orientation.rotation);
                    float[] vertices = ls.getRenderPolygon().getTransformedVertices();
                    ls.setRotation(0);
                    fillPolygon(vertices, centerX, centerY);
                }
                if (abstractShape.lineColor != null) {
                    setColor(abstractShape.lineColor);
                    RectangularLine ls = (RectangularLine) abstractShape;
                    ls.setRotation(orientation.rotation);
                    float[] vertices = ls.getRenderPolygon().getTransformedVertices();
                    ls.setRotation(0);
                    // TODO: Major hack warning.
                    drawPolygon(vertices, centerX, centerY);
                }
            } else if (abstractShape instanceof Polygon) {
                if (abstractShape.fillColor != null) {
                    setColor(abstractShape.fillColor);
                    Polygon ls = (Polygon) abstractShape;

                    ls.setRotation(orientation.rotation);
                    com.badlogic.gdx.math.Polygon[] polygons = GeometryUtils.decomposeIntoConvex(ls.getRenderPolygon());
                    for(com.badlogic.gdx.math.Polygon pol: polygons) {
                        //fillPolygon(vertices, centerX, centerY);
                        fillPolygon(pol.getTransformedVertices(), centerX, centerY);
                    }
                    ls.setRotation(0);


                }
                if (abstractShape.lineColor != null) {
                    setColor(abstractShape.lineColor);
                    Polygon ls = (Polygon) abstractShape;
                    ls.getRenderPolygon().rotate(orientation.rotation);
                    float[] vertices = ls.getRenderPolygon().getTransformedVertices();
                    ls.getRenderPolygon().rotate(-orientation.rotation);

                    drawPolygon(vertices, centerX, centerY);
                }
            }
        }

        public void drawPolygon(float[] vertices, float centerX, float centerY) {
            if(isCached()){
                return;
            }
            int x0, y0, x1 = 0, y1 = 0;
            if (vertices.length > 3) {
                for (int i = 3; i < vertices.length; i += 2) {
                    x0 = Math.round(vertices[i - 3] + centerX);
                    y0 = Math.round(vertices[i - 2] + centerY);
                    x1 = Math.round(vertices[i - 1] + centerX);
                    y1 = Math.round(vertices[i] + centerY);
                    drawLine(x0, y0, x1, y1);
                }
                x0 = Math.round(vertices[0] + centerX);
                y0 = Math.round(vertices[1] + centerY);
                drawLine(x1, y1, x0, y0);
            }
        }

        // TODO: Optimize
        public void fillPolygon(float[] vertices, float centerX, float centerY) {
            if(isCached()){
                return;
            }
            FloatArray points = new FloatArray(vertices);
            DelaunayTriangulator dt = new DelaunayTriangulator();
            ShortArray triangles = dt.computeTriangles(vertices, false);
            for (int i = 0; i < triangles.size; i += 3) {
                int p1 = triangles.get(i) * 2;
                int p2 = triangles.get(i + 1) * 2;
                int p3 = triangles.get(i + 2) * 2;
                fillTriangle( //
                        Math.round(points.get(p1) + centerX), Math.round(points.get(p1 + 1) + centerY), //
                        Math.round(points.get(p2) + centerX), Math.round(points.get(p2 + 1) + centerY), //
                        Math.round(points.get(p3) + centerX), Math.round(points.get(p3 + 1) + centerY)
                );
            }
        }

        public Texture createTexture() {
            if(isCached()){
                return texture;
            }
            texture = new Texture(this);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            this.dispose();
            return texture;
        }

        @Override
        public Vector2 getOffset() {
            return offset;
        }

    }
}
