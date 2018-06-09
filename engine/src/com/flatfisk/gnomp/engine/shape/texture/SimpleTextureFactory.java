package com.flatfisk.gnomp.engine.shape.texture;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.shape.AbstractShape;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.engine.shape.Polygon;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.math.Transform;

public class SimpleTextureFactory extends ShapeTextureFactory {
    @Override
    public ShapeTexture createShapeTexture(TextureCoordinates.BoundingRectangle boundingRectangle) {
        return new SimpleShapeTexture(boundingRectangle);
    }

    /**
     * Created by a-004213 on 27/04/14.
     */
    public static class SimpleShapeTexture extends Pixmap implements ShapeTexture {
        public Vector2 offset;

        public SimpleShapeTexture(TextureCoordinates.BoundingRectangle envelope) {
            super(Math.round(envelope.width), Math.round(envelope.height), Format.RGBA8888);
            offset = new Vector2(envelope.offsetX, envelope.offsetY);
        }

        public void draw(Shape structure,Transform orientation) {
            Vector2 pos = orientation.vector;
            AbstractShape abstractShape = structure.getGeometry();
            int centerX = Math.round(pos.x + getWidth() / 2 - offset.x);
            int centerY = Math.round(pos.y + getHeight() / 2 - offset.y);
            Gdx.app.log("Draw vector:", centerX + "," + centerY);

            if (abstractShape instanceof Circle) {
                if (structure.getGeometry().fillColor != null) {
                    setColor(structure.getGeometry().fillColor);
                    int radius = Math.round(((Circle) abstractShape).circle.radius);
                    fillCircle(centerX, centerY, radius);
                }
                if (structure.getGeometry().lineColor != null) {
                    setColor(structure.getGeometry().lineColor);
                    int radius = Math.round(((Circle) abstractShape).circle.radius);
                    drawCircle(centerX, centerY, radius);
                }

            } else if (abstractShape instanceof RectangularLine) {
                if (structure.getGeometry().fillColor != null) {
                    setColor(structure.getGeometry().fillColor);
                    RectangularLine ls = (RectangularLine) abstractShape;
                    ls.getRenderPolygon().rotate(orientation.rotation);
                    float[] vertices = ls.getRenderPolygon().getTransformedVertices();
                    ls.getRenderPolygon().rotate(-orientation.rotation);

                    fillPolygon(vertices, centerX, centerY);
                }
                if (structure.getGeometry().lineColor != null) {
                    setColor(structure.getGeometry().lineColor);
                    RectangularLine ls = (RectangularLine) abstractShape;
                    ls.getRenderPolygon().rotate(orientation.rotation);
                    float[] vertices = ls.getRenderPolygon().getTransformedVertices();
                    ls.getRenderPolygon().rotate(-orientation.rotation);

                    drawPolygon(vertices, centerX, centerY);
                }
            } else if (abstractShape instanceof Polygon) {
                if (structure.getGeometry().fillColor != null) {
                    setColor(structure.getGeometry().fillColor);
                    Polygon ls = (Polygon) abstractShape;
                    ls.getRenderPolygon().rotate(orientation.rotation);
                    float[] vertices = ls.getRenderPolygon().getTransformedVertices();
                    ls.getRenderPolygon().rotate(-orientation.rotation);

                    fillPolygon(vertices, centerX, centerY);
                }
                if (structure.getGeometry().lineColor != null) {
                    setColor(structure.getGeometry().lineColor);
                    Polygon ls = (Polygon) abstractShape;
                    ls.getRenderPolygon().rotate(orientation.rotation);
                    float[] vertices = ls.getRenderPolygon().getTransformedVertices();
                    ls.getRenderPolygon().rotate(-orientation.rotation);

                    drawPolygon(vertices, centerX, centerY);
                }
            }
        }

        public void drawPolygon(float[] vertices, float centerX, float centerY) {
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
            Texture t = new Texture(this);
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            this.dispose();
            return t;
        }

        @Override
        public Vector2 getOffset() {
            return offset;
        }

    }
}
