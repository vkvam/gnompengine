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
    public float halfRectangleWith;
    public final Vector2 from = new Vector2(), to=new Vector2();

    public RectangularLine(float lineWidth, float halfRectangleWith, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
        this.halfRectangleWith = halfRectangleWith;
    }

    public void reset() {
        super.reset();
        from.setZero();
        to.setZero();
    }

    public void createPolygonVertices() {
        Vector2 direction = Pools.obtain(Vector2.class);
        direction.set(to).sub(from).rotate(90).nor().scl(halfRectangleWith);

        float[] vertices = new float[8];
        vertices[0] = from.x + direction.x;
        vertices[1] = from.y + direction.y;

        vertices[2] = from.x - direction.x;
        vertices[3] = from.y - direction.y;

        vertices[4] = to.x - direction.x;
        vertices[5] = to.y - direction.y;

        vertices[6] = to.x + direction.x;
        vertices[7] = to.y + direction.y;

        setVertices(vertices);
        Pools.free(direction);
    }

}
