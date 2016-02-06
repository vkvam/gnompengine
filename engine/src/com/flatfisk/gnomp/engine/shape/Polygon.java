package com.flatfisk.gnomp.engine.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.GeometryUtils;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 */
public class Polygon extends AbstractShape {
    public com.badlogic.gdx.math.Polygon polygon = new com.badlogic.gdx.math.Polygon();

    public Polygon(float lineWidth, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
        this.lineColor = color;
    }

    public com.badlogic.gdx.math.Polygon getRenderPolygon() {
        return polygon;
    }

    public com.badlogic.gdx.math.Polygon getPhysicsPolygon() {
        return polygon;
    }

    @Override
    public Polygon getCopy() {
        return null;
    }

    @Override
    public FixtureDef[] getFixtureDefinitions(Vector2 offset) {
        com.badlogic.gdx.math.Polygon physicsPolygon = getPhysicsPolygon();

        physicsPolygon.setPosition(offset.x*PhysicsConstants.METERS_PER_PIXEL, offset.y*PhysicsConstants.METERS_PER_PIXEL);
        physicsPolygon.setScale(physicsPolygon.getScaleX()*PhysicsConstants.METERS_PER_PIXEL, physicsPolygon.getScaleX()*PhysicsConstants.METERS_PER_PIXEL);

        com.badlogic.gdx.math.Polygon transformedPolygon = new com.badlogic.gdx.math.Polygon(physicsPolygon.getTransformedVertices());
        com.badlogic.gdx.math.Polygon[] polygons = GeometryUtils.decomposeIntoConvex(transformedPolygon);

        FixtureDef[] fixtureDefs = new FixtureDef[polygons.length];
        int i = 0;
        for (com.badlogic.gdx.math.Polygon p : polygons) {
            fixtureDefs[i++] = getFixtureDef(p);
        }
        physicsPolygon.setScale(1, 1);
        physicsPolygon.setPosition(0, 0);
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
        return fixtureDef;
    }

    public void setVertices(float[] vertices) {
        polygon.setVertices(vertices);
    }

    @Override
    public TextureCoordinates getTextureCoordinates(TextureCoordinates textureCoordinates, Transform transform) {
        Gdx.app.log(getClass().getName(), transform.toString());

        setRotation(transform.rotation);
        com.badlogic.gdx.math.Polygon renderPolygon = getRenderPolygon();
        float[] v = renderPolygon.getTransformedVertices();
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
