package com.flatfisk.gnomp.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.PhysicsConstants;


/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 * Project:Raven
 */
public class CircleShape extends Shape{
    public Circle circle;
    private Vector2 from,to;

    public CircleShape(float lineWidth, float radius, Color lineColor, Color fillColor) {
        super(lineWidth, lineColor, fillColor);
        this.circle = new Circle(0, 0, radius);
    }
    public CircleShape(){
        super();
    }

    @Override
    public void drawFrom(Vector2 vector) {
        from = vector;
    }

    @Override
    public void drawVia(Vector2 vector) {
        circle.radius = from.dst(vector);
    }

    @Override
    public void drawTo(Vector2 vector) {
        circle.radius = from.dst(vector);
    }

    @Override
    public void init(float lineWidth, Color lineColor, Color fillColor){
        super.init(lineWidth,lineColor,fillColor);
        this.circle = new Circle(0,0,1);
    }

    @Override
    public FixtureDef[] getFixtureDefinitions(Vector2 offset) {
        com.badlogic.gdx.physics.box2d.CircleShape circleShape = new com.badlogic.gdx.physics.box2d.CircleShape();
        circleShape.setPosition(new Vector2(offset.x, offset.y).scl(PhysicsConstants.WORLD_TO_BOX));
        circleShape.setRadius(circle.radius * PhysicsConstants.WORLD_TO_BOX);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        //TODO: set correct density!
        fixtureDef.density = 1;
        return new FixtureDef[]{fixtureDef};
    }

    @Override
    public void setRotation(float angle) {
        // No point in rotating a circle.
    }


    @Override
    public TextureCoordinates getTextureCoordinates(TextureCoordinates textureCoordinates, Translation translation) {

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

        float centerX = translation.position.x;
        float centerY = translation.position.y;
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

    }
}
