package com.flatfisk.gnomp.engine.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.GeometryUtils;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.PhysicsConstants;

/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 * Project:Raven
 */
public class Polygon extends AbstractShape {
    public com.badlogic.gdx.math.Polygon polygon;

    public Polygon(){
        super();
    }
    public Polygon(float[] vertices, float lineWidth, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
        this.polygon = new com.badlogic.gdx.math.Polygon(vertices);
        this.lineColor = color;
    }

    public Polygon(float lineWidth, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
        this.lineColor = color;
    }

    @Override
    public Polygon getCopy() {
        Polygon lineShape = Pools.obtain(Polygon.class);

        com.badlogic.gdx.math.Polygon polygon = Pools.obtain(com.badlogic.gdx.math.Polygon.class);
        polygon.setVertices(this.polygon.getVertices());
        polygon.setPosition(this.polygon.getX(), this.polygon.getY());
        polygon.setOrigin(this.polygon.getOriginX(), this.polygon.getOriginY());
        polygon.setRotation(this.polygon.getRotation());
        polygon.setScale(this.polygon.getScaleX(), this.polygon.getScaleY());

        lineShape.polygon = polygon;
        return lineShape;
    }

    @Override
    public void drawFrom(Vector2 vector) {

    }

    @Override
    public void drawVia(Vector2 vector) {

    }

    @Override
    public void drawTo(Vector2 vector) {

    }

    @Override
    public FixtureDef[] getFixtureDefinitions(Vector2 offset) {
        Vector2 scaledOffset = offset.cpy().scl(PhysicsConstants.METERS_PER_PIXEL);
        polygon.setPosition(scaledOffset.x, scaledOffset.y);
        polygon.setScale(PhysicsConstants.METERS_PER_PIXEL, PhysicsConstants.METERS_PER_PIXEL);

        com.badlogic.gdx.math.Polygon transformedPolygon = new com.badlogic.gdx.math.Polygon(polygon.getTransformedVertices());
        com.badlogic.gdx.math.Polygon[] polygons = GeometryUtils.decomposeIntoConvex(transformedPolygon);

        FixtureDef[] fixtureDefs = new FixtureDef[polygons.length];
        int i = 0;
        for (com.badlogic.gdx.math.Polygon p : polygons) {
            fixtureDefs[i++] = getFixtureDef(p);
        }
        polygon.setScale(1, 1);
        polygon.setPosition(0, 0);
        return fixtureDefs;
    }

    @Override
    public void setRotation(float angle) {
        this.polygon.setRotation(angle);
    }

    private FixtureDef getFixtureDef(com.badlogic.gdx.math.Polygon polygon) {
        FixtureDef fixtureDef = new FixtureDef();
        com.badlogic.gdx.physics.box2d.PolygonShape shape = new com.badlogic.gdx.physics.box2d.PolygonShape();
        shape.set(polygon.getVertices());
        fixtureDef.shape = shape;
        //TODO: set correct density!
        fixtureDef.density = 1;

        return fixtureDef;
    }

    public com.badlogic.gdx.math.Polygon getPolygon() {
        return polygon;
    }

    public void setVertices(float[] vertices) {
        if (polygon != null) {
            polygon.setVertices(vertices);
        } else {
            polygon = new com.badlogic.gdx.math.Polygon(vertices);
        }

    }

    @Override
    public TextureCoordinates getTextureCoordinates(TextureCoordinates textureCoordinates, Transform transform) {
        Gdx.app.log(getClass().getName(), transform.toString());
        setRotation(transform.rotation);
        float[] v = this.polygon.getTransformedVertices();
        setRotation(-transform.rotation);
        float centerX = transform.vector.x;
        float centerY = transform.vector.y;
        return getTextureCoordinatesFromVertices(textureCoordinates, v, centerX, centerY);
    }

    @Override
    public void reset() {
        super.reset();
    }
}
