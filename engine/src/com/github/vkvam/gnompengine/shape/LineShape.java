package com.github.vkvam.gnompengine.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.github.vkvam.gnompengine.math.Translation;
import com.github.vkvam.gnompengine.shape.texture.TextureCoordinates;


/**
 * Created by: Vemund Kvam 004213
 * Date: 11/11/13
 * Time: 9:22 PM
 * Project:Raven
 */
public class LineShape extends Shape {
    public Polygon polyline;

    public LineShape(Polygon polyline, float lineWidth, Color color) {
        super(lineWidth, color, null);
        this.polyline = polyline;
    }
    public LineShape(){
        super(1,Color.BLACK,null);
        this.polyline = new Polygon();
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
        polyline = null;
    }

    @Override
    public FixtureDef[] getFixtureDefinitions(Vector2 offset) {
        // TODO: Add proper if this is to be used.
        return new FixtureDef[0];
    }

    @Override
    public void setRotation(float angle) {
        this.polyline.rotate(angle);
    }


    @Override
    public TextureCoordinates getTextureCoordinates(TextureCoordinates textureCoordinates, Translation translation) {
        setRotation(translation.angle);
        float[] v = this.polyline.getTransformedVertices();
        setRotation(-translation.angle);
        float centerX = translation.position.x;
        float centerY = translation.position.y;
        return getTextureCoordinatesFromVertices(textureCoordinates, v, centerX, centerY);
    }

    @Override
    public void reset() {

    }
}
