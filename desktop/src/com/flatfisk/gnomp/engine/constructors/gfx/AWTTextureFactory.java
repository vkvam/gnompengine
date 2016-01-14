package com.flatfisk.gnomp.engine.constructors.gfx;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.shape.*;
import com.flatfisk.gnomp.shape.LineShape;
import com.flatfisk.gnomp.shape.texture.ShapeTexture;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;

/**
 * Created by a-004213 on 27/04/14.
 */
public class AWTTextureFactory extends ShapeTextureFactory {
    @Override
    public ShapeTexture createShapeTexture(TextureCoordinates.BoundingRectangle boundingRectangle){
        return new DesktopShapeTexture(boundingRectangle);
    }

    public static class DesktopShapeTexture extends Texture implements ShapeTexture {
        public TextureCoordinates.BoundingRectangle bounds;
        private BufferedImage bufferImg;
        private IntBuffer buffer;
        private final Color BACKGROUND = new Color(0, 0, 0, 0);
        private Vector2 offset;
        private Graphics2D g2d;

        public DesktopShapeTexture(TextureCoordinates.BoundingRectangle envelope){
            this(Math.round(envelope.width), Math.round(envelope.height));
            offset = new Vector2(envelope.offsetX,envelope.offsetY);
            setFilter(TextureFilter.Linear, TextureFilter.Linear);

            bounds = envelope;
        }


        private DesktopShapeTexture(int width, int height){
            super(width, height, Pixmap.Format.RGBA8888);
            bufferImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            buffer = BufferUtils.newIntBuffer(width * height);
            initGraphics2D();
        }

        private void initGraphics2D() {
            g2d = (Graphics2D) bufferImg.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setBackground(BACKGROUND);
            g2d.clearRect(0, 0, bufferImg.getWidth(), bufferImg.getHeight());
        }

        @Override
        public void draw(StructureRelative structure, Translation translation){
            if (structure == null || structure.shape == null) {
                return;
            }

            Shape jShape = createAWTShape(structure,translation);

            drawFilled(structure,jShape);
            drawLine(structure, jShape);
        }

        private void drawFilled(StructureRelative structure, Shape shape){
            if (structure.shape.fillColor != null) {
                Color color = gdxToAwtColor(structure.shape.fillColor);
                g2d.setColor(color);
                g2d.fill(shape);
            }
        }

        private void drawLine(StructureRelative structure, Shape shape){
            if (structure.shape.lineColor != null) {
                Color color = gdxToAwtColor(structure.shape.lineColor);
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(
                                structure.shape.lineWidth,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND)
                );
                g2d.draw(shape);
            }
        }


        public Texture createTexture() {
            //you could probably cache this rather than requesting it every upload
            int[] pixels = ((DataBufferInt) bufferImg.getRaster().getDataBuffer())
                    .getData();
            this.bind();
            buffer.rewind();
            buffer.put(pixels);
            buffer.flip();
            Gdx.gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, 0, 0, bufferImg.getWidth(), bufferImg.getHeight(),
                    GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
            g2d.dispose();
            return this;
        }

        @Override
        public Vector2 getOffset() {
            return offset;
        }


        private Shape createAWTShape(StructureRelative structure, Translation translation) {

            Vector2 pos = translation.position;//worldTranslation.position;
            Vector2 offsetPosition = new Vector2(
                    pos.x+getWidth()/2-offset.x,
                    pos.y+getHeight()/2-offset.y
            );

            float angle = translation.angle;//worldTranslation.angle;

            com.flatfisk.gnomp.shape.Shape geShape = structure.shape;
            Shape awtShape = null;
            if (geShape instanceof PolygonShape) {
                PolygonShape ps = (PolygonShape) structure.shape;
                ps.polygon.setRotation(angle);
                awtShape = polygonToShape(ps.polygon, offsetPosition);
                ps.polygon.setRotation(0);
            }else if (geShape instanceof LineShape) {
                LineShape c = (LineShape) structure.shape;
                c.polyline.setRotation(angle);
                awtShape = polyLineToShape(c.polyline, offsetPosition);
                c.polyline.setRotation(0);
            }else if (geShape instanceof CircleShape) {
                CircleShape c = (CircleShape) structure.shape;
                awtShape = circleToShape(c.circle, offsetPosition);
            }
            return awtShape;
        }

        private Shape polyLineToShape(com.badlogic.gdx.math.Polygon polyline, Vector2 offsetPosition) {
            float[] v = polyline.getTransformedVertices();
            return verticesToGeneralShape(v,offsetPosition,false);
        }

        private Shape polygonToShape(com.badlogic.gdx.math.Polygon polygon, Vector2 offsetPosition) {
            float[] v = polygon.getTransformedVertices();
            return verticesToGeneralShape(v,offsetPosition,true);
        }

        private Shape verticesToGeneralShape(float[] v,Vector2 offsetPosition,boolean polygon){
            GeneralPath shape = new GeneralPath();

            float x0, y0;
            int l = v.length;

            shape.moveTo(v[0] + offsetPosition.x, v[1] + offsetPosition.y);

            for (int i = 2; i < l; i += 2) {
                x0 = v[i] + offsetPosition.x;
                y0 = v[i + 1] + offsetPosition.y;
                shape.lineTo(x0, y0);
            }

            // If polygon
            if(polygon)
            {
                shape.lineTo(v[0] + offsetPosition.x, v[1] + offsetPosition.y);
            }

            return shape;
        }

        private Shape circleToShape(Circle circle, Vector2 offsetPosition) {
            Ellipse2D.Float awtCircle = new Ellipse2D.Float();
            float circleDiam = circle.radius;
            float circleX = circle.x + offsetPosition.x - circleDiam;
            float circleY = circle.y + offsetPosition.y - circleDiam;
            circleDiam *= 2;

            awtCircle.width = circleDiam;
            awtCircle.height = circleDiam;
            awtCircle.x = circleX;
            awtCircle.y = circleY;

            return awtCircle;
        }

        public Color gdxToAwtColor(com.badlogic.gdx.graphics.Color libGDXColor) {
            Color color = new Color(libGDXColor.r, libGDXColor.g, libGDXColor.b, libGDXColor.a);
            return color;
        }

    }
}
