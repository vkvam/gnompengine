package com.flatfisk.gnomp.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;

/**
 * Created by: Vemund Kvam 004213
 * Date: 11/12/13
 * Time: 12:14 AM
 * Project:Raven
 */
public abstract class Shape implements Pool.Poolable {
    public float lineWidth;
    public Color lineColor;
    public Color fillColor;

    public Shape(float lineWidth, Color lineColor, Color fillColor) {
        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
        this.fillColor = fillColor;
    }

    public Shape(){
        Gdx.app.log(getClass().getName(),"Calling constructor of shape, so not using Pool.");
    }

    public void init(float lineWidth, Color lineColor, Color fillColor){
        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
        this.fillColor = fillColor;
    }

    public abstract void drawFrom(Vector2 vector);
    public abstract void drawVia(Vector2 vector);
    public abstract void drawTo(Vector2 vector);

    public abstract void setRotation(float angle);

    public void dispose() {
        lineColor = null;
        fillColor = null;
    }

    public abstract FixtureDef[] getFixtureDefinitions(Vector2 offset);

    public abstract TextureCoordinates getTextureCoordinates(TextureCoordinates textureCoordinates, Translation translation);

    protected TextureCoordinates getTextureCoordinatesFromVertices(TextureCoordinates textureCoordinates, float[] v, float centerX, float centerY) {
        if (textureCoordinates == null) {
            textureCoordinates = new TextureCoordinates();
            textureCoordinates.x0 = Float.MAX_VALUE;
            textureCoordinates.x1 = Float.MIN_VALUE;
            textureCoordinates.y0 = Float.MAX_VALUE;
            textureCoordinates.y1 = Float.MIN_VALUE;
        }

        float tdX0 = textureCoordinates.x0;
        float tdY0 = textureCoordinates.y0;
        float tdX1 = textureCoordinates.x1;
        float tdY1 = textureCoordinates.y1;

        float l = v.length;

        float x, y;
        for (int i = 0; i < l; i += 2) {
            x = v[i] + centerX;
            y = v[i + 1] + centerY;
            if (x < tdX0) {
                tdX0 = x;
            }
            if (x > tdX1) {
                tdX1 = x;
            }
            if (y < tdY0) {
                tdY0 = y;
            }
            if (y > tdY1) {
                tdY1 = y;
            }
        }

        textureCoordinates.x0 = tdX0 - lineWidth / 2;
        textureCoordinates.x1 = tdX1 + lineWidth / 2;
        textureCoordinates.y0 = tdY0 - lineWidth / 2;
        textureCoordinates.y1 = tdY1 + lineWidth / 2;
        return textureCoordinates;
    }
}
