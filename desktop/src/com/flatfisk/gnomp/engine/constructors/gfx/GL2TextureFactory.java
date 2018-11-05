package com.flatfisk.gnomp.engine.constructors.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;

import com.flatfisk.gnomp.engine.shape.AbstractShape;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.engine.shape.Line;
import com.flatfisk.gnomp.engine.shape.Polygon;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTexture;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by a-004213 on 27/04/14.
 */
public class GL2TextureFactory extends ShapeTextureFactory {



    @Override
    public ShapeTexture createShapeTexture(TextureCoordinates.BoundingRectangle boundingRectangle, int textureId){
        return new GL2ShapeTexture(boundingRectangle);
    }

    @Override
    public void dispose() {

    }

    public static class GL2ShapeTexture extends Texture implements ShapeTexture {
        public TextureCoordinates.BoundingRectangle bounds;
        private Vector2 offset;

        private Pixmap mp;


        FrameBuffer fbo;

        float scaleX=0.5f, scaleY=0.5f;

        static GL2ShapeRenderer shapeRenderer = null;

        public GL2ShapeTexture(TextureCoordinates.BoundingRectangle envelope){
            this(Math.round(envelope.width), Math.round(envelope.height));
            offset = new Vector2(envelope.offsetX, envelope.offsetY);
            setFilter(TextureFilter.Linear, TextureFilter.Linear);
            bounds = envelope;
        }


        private GL2ShapeTexture(int width, int height){
            super(width, height, Pixmap.Format.RGBA8888);

            if(shapeRenderer==null){
                shapeRenderer= new GL2ShapeRenderer();
            }

            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 1, 1, false);
            fbo.begin();

            float sWidth = Gdx.graphics.getWidth();
            float sHeight = Gdx.graphics.getHeight();

            scaleX = sWidth;
            scaleY = sHeight;

            Gdx.gl.glViewport(0, 0, 1,1);
            Gdx.gl.glClearColor(0f, 0f, 0f, 0f); //transparent black
            Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT); //clear the color buffer


        }

        @Override
        public boolean isCached() {
            return false;
        }

        @Override
        public void draw(com.flatfisk.gnomp.engine.components.Shape structure, Transform transform){
            if (structure == null || structure.geometry == null) {
                return;
            }

            shapeRenderer.begin(GL2ShapeRenderer.ShapeType.Filled);
            drawFilled(structure, transform);
            shapeRenderer.end();

            shapeRenderer.begin(GL2ShapeRenderer.ShapeType.Line);
            drawLine(structure, transform);
            shapeRenderer.end();

        }

        private void drawFilled(com.flatfisk.gnomp.engine.components.Shape structure, Transform transform){
            if (structure.geometry.fillColor != null) {
                shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
                drawShape(structure, transform);
            }
        }

        private void drawLine(com.flatfisk.gnomp.engine.components.Shape structure, Transform transform){
            if (structure.geometry.lineColor != null) {
                drawShape(structure, transform);
            }
        }

        private void drawShape(com.flatfisk.gnomp.engine.components.Shape structure, Transform transform) {

            Vector2 pos = transform.vector;
            Vector2 offsetPosition = new Vector2(
                    pos.x+getWidth()/2-offset.x,
                    pos.y+getHeight()/2-offset.y
            );


            offsetPosition.scl(1/scaleX, 1/scaleY);
            float angle = transform.rotation;

            AbstractShape geAbstractShape = structure.geometry;
            if (geAbstractShape instanceof Polygon) {
                Polygon ps = (com.flatfisk.gnomp.engine.shape.Polygon) structure.geometry;

                com.badlogic.gdx.math.Polygon poly = ps.getRenderPolygon();

                poly.setRotation(angle);
                poly.setPosition(offsetPosition.x, offsetPosition.y);
                poly.setScale(1/scaleX, 1/scaleY);
                shapeRenderer.polygon(poly.getTransformedVertices());
                poly.setPosition(0,0);
                poly.setRotation(0);

            }else if (geAbstractShape instanceof Line) {
                Line c = (Line) structure.geometry;
                c.polyline.setRotation(angle);
                c.polyline.setPosition(offsetPosition.x, offsetPosition.y);
                shapeRenderer.polygon(c.polyline.getTransformedVertices());
                c.polyline.setRotation(0);
                c.polyline.setPosition(0,0);
            }else if (geAbstractShape instanceof Circle) {
                Circle c = (Circle) structure.geometry;
                shapeRenderer.circle(c.circle.x+offsetPosition.x, c.circle.y+offsetPosition.x, c.circle.radius, 5);
            }
        }

        public Texture createTexture() {
            Texture myTex = fbo.getColorBufferTexture();
            fbo.end();
            return myTex;
        }

        @Override
        public Vector2 getOffset() {
            return offset;
        }



    }
}
