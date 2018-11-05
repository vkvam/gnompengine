package com.flatfisk.gnomp.engine.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;


/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 * Project:Raven
 */
public class Circle extends AbstractShape {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);
    public com.badlogic.gdx.math.Circle circle = new com.badlogic.gdx.math.Circle();

    protected Circle(){
        super(1, null, null);
        this.circle = new com.badlogic.gdx.math.Circle(0, 0, 1);
    }

    public void setRadius(float radius){
        this.circle.radius = radius;
    }

    @Override
    public Circle getCopy(){
        Circle circleShape = com.badlogic.gdx.utils.Pools.obtain(Circle.class);
        circleShape.circle.set(circle.x,circle.y,circle.radius);
        return circleShape;
    }

    @Override
    public void init(float lineWidth, Color lineColor, Color fillColor){
        super.init(lineWidth,lineColor,fillColor);
        this.circle = new com.badlogic.gdx.math.Circle(0,0,1);
    }

    @Override
    public FixtureDef[] getFixtureDefinitions(Vector2 offset) {
        com.badlogic.gdx.physics.box2d.CircleShape circleShape = new com.badlogic.gdx.physics.box2d.CircleShape();
        circleShape.setPosition(new Vector2(offset.x, offset.y).scl(PhysicsConstants.METERS_PER_PIXEL));
        circleShape.setRadius(circle.radius * PhysicsConstants.METERS_PER_PIXEL);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        return new FixtureDef[]{fixtureDef};
    }


    @Override
    public TextureCoordinates getTextureCoordinates(TextureCoordinates textureCoordinates, Transform transform) {

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

        float centerX = transform.vector.x;
        float centerY = transform.vector.y;
        float radius = this.circle.radius;

        float x0 = centerX - radius - lineWidth / 2;
        float x1 = centerX + radius + lineWidth / 2;
        float y0 = centerY - radius - lineWidth / 2;
        float y1 = centerY + radius + lineWidth / 2;

        if (x0 < tdX0) {
            textureCoordinates.x0 = x0;
        }
        if (x1 > tdX1) {
            textureCoordinates.x1 = x1;
        }
        if (y0 < tdY0) {
            textureCoordinates.y0 = y0;
        }
        if (y1 > tdY1) {
            textureCoordinates.y1 = y1;
        }
        return textureCoordinates;
    }

    @Override
    public void reset() {
        super.reset();
    }
}
