package com.flatfisk.gnomp.engine.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by: Vemund Kvam 004213
 * Date: 10/5/13
 * Time: 12:19 AM
 */
public class CatmullPolygon extends Polygon {

    public int physicsResolution = 1;
    public int renderResolution = 10;

    public CatmullPolygon(float lineWidth, Color color, Color fillColor) {
        super(lineWidth, color, fillColor);
    }

    @Override
    public com.badlogic.gdx.math.Polygon getRenderPolygon() {
        return getPolygon(((polygon.getVertices().length+1)/2)*renderResolution);
    }
    @Override
    public com.badlogic.gdx.math.Polygon getPhysicsPolygon() {
        return getPolygon(((polygon.getVertices().length+1)/2)*physicsResolution);
    }

    private com.badlogic.gdx.math.Polygon getPolygon(int fidelity){

        int k = fidelity; //increase k for more fidelity to the spline
        float[] vertices = polygon.getVertices();

        // TODO: Should probably rotate polygon before tranforming int "catmull-polygon".
        float[] newVertices = new float[k*2-2];
        Vector2[] points = new Vector2[(vertices.length+1)/2];

        for(int i=0,l=points.length;i<l;i++){
            Vector2 p = Pools.obtainVector();
            p.x = vertices[i*2];
            p.y = vertices[i*2+1];
            points[i] = p;
        }


        CatmullRomSpline<Vector2> myCatmull = new CatmullRomSpline<Vector2>(points, true);
        myCatmull.continuous = true;


        Vector2 point = Pools.obtainVector();
        for(int i = 0; i < k-1; ++i)
        {
            myCatmull.valueAt(point, ((float)(i+1))/((float)k-1));
            newVertices[i*2]=point.x;
            newVertices[i*2+1]=point.y;
        }

        // TODO: Create better pool.
        com.badlogic.gdx.math.Polygon returnPolygon = com.badlogic.gdx.utils.Pools.obtain(com.badlogic.gdx.math.Polygon.class);
        returnPolygon.setVertices(newVertices);
        return returnPolygon;
    }

}
