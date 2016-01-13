package com.flatfisk.gnomp.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 * Project:Raven
 */
public class RectangularLineShape extends PolygonShape {
    public float rectangleWidth;
    public Vector2 from;
    public Vector2 to;

    public RectangularLineShape(){
        super();
    }
    public RectangularLineShape(float lineWidth, float rectangleWidth, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
        this.rectangleWidth = rectangleWidth;
    }

    public void dispose() {
        super.dispose();
        from = null;
        to = null;
    }

    public void createPolygonVertices() {
        Vector2 angle = to.cpy().sub(from);
        angle.rotate(90).nor().scl(rectangleWidth);

        float[] vertices = new float[8];
        vertices[0] = from.x + angle.x;
        vertices[1] = from.y + angle.y;

        vertices[2] = from.x - angle.x;
        vertices[3] = from.y - angle.y;

        vertices[4] = to.x - angle.x;
        vertices[5] = to.y - angle.y;

        vertices[6] = to.x + angle.x;
        vertices[7] = to.y + angle.y;

        setVertices(vertices);
    }
}
