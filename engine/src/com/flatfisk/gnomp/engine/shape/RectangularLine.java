package com.flatfisk.gnomp.engine.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 * Project:Raven
 */
public class RectangularLine extends Polygon {
    public float rectangleWidth;
    public final Vector2 from = new Vector2(),
            to=new Vector2();

    public RectangularLine(float lineWidth, float rectangleWidth, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
        this.rectangleWidth = rectangleWidth;
    }

    public void reset() {
        super.reset();
        from.setZero();
        to.setZero();
    }

    public void createPolygonVertices() {
        Vector2 angle = Pools.obtain(Vector2.class);
        angle.set(to).sub(from).rotate(90).nor().scl(rectangleWidth);

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
        Pools.free(angle);
    }
}
