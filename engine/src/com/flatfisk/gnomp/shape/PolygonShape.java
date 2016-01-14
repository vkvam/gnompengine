package com.flatfisk.gnomp.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.flatfisk.gnomp.math.GeometryUtils;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.PhysicsConstants;

/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 * Project:Raven
 */
public class PolygonShape extends Shape {
    public Polygon polygon;

    public PolygonShape(){
        super();
    }
    public PolygonShape(float[] vertices, float lineWidth, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
        this.polygon = new Polygon(vertices);
        this.lineColor = color;
    }

    public PolygonShape(float lineWidth, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
        this.lineColor = color;
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

    public void dispose() {
        super.dispose();
        polygon = null;
    }

    @Override
    public FixtureDef[] getFixtureDefinitions(Vector2 offset) {
        Vector2 scaledOffset = offset.cpy().scl(PhysicsConstants.WORLD_TO_BOX);
        polygon.setPosition(scaledOffset.x, scaledOffset.y);
        polygon.setScale(PhysicsConstants.WORLD_TO_BOX, PhysicsConstants.WORLD_TO_BOX);

        Polygon transformedPolygon = new Polygon(polygon.getTransformedVertices());
        Polygon[] polygons = GeometryUtils.decomposeIntoConvex(transformedPolygon);

        FixtureDef[] fixtureDefs = new FixtureDef[polygons.length];
        int i = 0;
        for (Polygon p : polygons) {
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

    private FixtureDef getFixtureDef(Polygon polygon) {
        FixtureDef fixtureDef = new FixtureDef();
        com.badlogic.gdx.physics.box2d.PolygonShape shape = new com.badlogic.gdx.physics.box2d.PolygonShape();
        shape.set(polygon.getVertices());
        fixtureDef.shape = shape;
        //TODO: set correct density!
        fixtureDef.density = 1;

        return fixtureDef;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setVertices(float[] vertices) {
        if (polygon != null) {
            polygon.setVertices(vertices);
        } else {
            polygon = new Polygon(vertices);
        }

    }

    @Override
    public TextureCoordinates getTextureCoordinates(TextureCoordinates textureCoordinates, Translation translation) {
        Gdx.app.log(getClass().getName(), translation.toString());
        setRotation(translation.angle);
        float[] v = this.polygon.getTransformedVertices();
        setRotation(-translation.angle);
        float centerX = translation.position.x;
        float centerY = translation.position.y;
        return getTextureCoordinatesFromVertices(textureCoordinates, v, centerX, centerY);
    }

    @Override
    public void reset() {

    }
}
