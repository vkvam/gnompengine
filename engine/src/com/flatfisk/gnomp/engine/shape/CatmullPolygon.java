package com.flatfisk.gnomp.engine.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;


/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 */
public class CatmullPolygon extends Polygon {

    public int physicsResolution = 1;
    public int renderResolution = 10;

    private final com.badlogic.gdx.math.Polygon renderPolygon = new com.badlogic.gdx.math.Polygon();
    private final com.badlogic.gdx.math.Polygon physicsPolygon = new com.badlogic.gdx.math.Polygon();

    public CatmullPolygon() {
        super();
    }

    @Override
    public com.badlogic.gdx.math.Polygon getRenderPolygon() {
        return setPolygon(renderPolygon, ((polygon.getVertices().length + 1) / 2) * renderResolution);
    }

    @Override
    public com.badlogic.gdx.math.Polygon getPhysicsPolygon() {
        return setPolygon(physicsPolygon, ((polygon.getVertices().length + 1) / 2) * physicsResolution);
    }

    private com.badlogic.gdx.math.Polygon setPolygon(com.badlogic.gdx.math.Polygon out, int fidelity) {

        int k = fidelity; //increase k for more fidelity to the spline
        float[] vertices = polygon.getVertices();

        // TODO: Should probably rotate polygon before tranforming int "catmull-polygon".
        float[] newVertices = new float[k * 2 - 2];
        Vector2[] points = new Vector2[(vertices.length + 1) / 2];

        for (int i = 0, l = points.length; i < l; i++) {
            Vector2 p = Pools.obtain(Vector2.class);
            p.x = vertices[i * 2];
            p.y = vertices[i * 2 + 1];
            points[i] = p;
        }


        CatmullRomSpline<Vector2> myCatmull = new CatmullRomSpline<Vector2>(points, true);
        myCatmull.continuous = true;


        Vector2 point = Pools.obtain(Vector2.class);
        for (int i = 0; i < k - 1; ++i) {
            myCatmull.valueAt(point, ((float) (i + 1)) / ((float) k - 1));
            newVertices[i * 2] = point.x;
            newVertices[i * 2 + 1] = point.y;
        }
        Pools.free(point);

        Vector2[] controlPoints = myCatmull.controlPoints;
        for (int i = 0, l = controlPoints.length; i < l; i++) {
            Pools.free(controlPoints[i]);
        }

        out.setVertices(newVertices);
        out.setScale(polygon.getScaleX(), polygon.getScaleY());
        return out;
    }

}
