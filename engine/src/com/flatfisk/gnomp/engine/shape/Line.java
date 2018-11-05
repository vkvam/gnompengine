package com.flatfisk.gnomp.engine.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.Transform;


/**
 * Created by: Vemund Kvam 004213
 * Date: 11/11/13
 * Time: 9:22 PM
 * Project:Raven
 */
public class Line extends AbstractShape {
    public Polygon polyline = new com.badlogic.gdx.math.Polygon();

    protected Line(){
        super(1, null, null);
        polyline = new Polygon();
    }

    @Override
    public Line getCopy(){
        Line lineShape = Pools.obtain(Line.class);

        Polygon polyline = Pools.obtain(Polygon.class);
        polyline.setVertices(this.polyline.getVertices());
        polyline.setPosition(this.polyline.getX(), this.polyline.getY());
        polyline.setOrigin(this.polyline.getOriginX(), this.polyline.getOriginY());
        polyline.setRotation(this.polyline.getRotation());
        polyline.setScale(this.polyline.getScaleX(), this.polyline.getScaleY());

        lineShape.polyline = polyline;
        return lineShape;
    }

    @Override
    public FixtureDef[] getFixtureDefinitions(Vector2 offset) {
        return new FixtureDef[0];
    }

    @Override
    public void setRotation(float angle) {
        this.polyline.rotate(angle);
    }


    @Override
    public TextureCoordinates getTextureCoordinates(TextureCoordinates textureCoordinates, Transform transform) {
        setRotation(transform.rotation);
        float[] v = this.polyline.getTransformedVertices();
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
