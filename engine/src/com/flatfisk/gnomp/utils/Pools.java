package com.flatfisk.gnomp.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.math.Spatial;

/**
 * Created by Vemund Kvam on 13/01/16.
 */
public class Pools {
    private static Vector2PoolProvider vector2Pool = new Vector2PoolProvider();
    private static SpatialPoolProvider spatialPool = new SpatialPoolProvider();

    public static Spatial obtainSpatial(){
        return spatialPool.obtain();
    }
    public static Vector2 obtainVector(){
        return vector2Pool.obtain();
    }

    public static Vector2 obtainVector2FromCopy(Vector2 vector){
        return vector2Pool.createCopy(vector);
    }
    public static Spatial obtainSpatialFromCopy(Spatial spatial){
        return spatialPool.createCopy(spatial);
    }


    public static class Vector2PoolProvider extends Pool<Vector2>{

        @Override
        protected Vector2 newObject() {
            return Vector2.Zero.cpy();
        }

        public Vector2 createCopy(Vector2 vector){
            Vector2 out = obtain();
            out.x=vector.x;
            out.y=vector.y;
            return out;
        }
    }

    public static class SpatialPoolProvider extends Pool<Spatial>{

        @Override
        protected Spatial newObject() {
            return new Spatial();
        }

        public Spatial createCopy(Spatial spatial){
            Spatial out = obtain();
            out.vector.x=spatial.vector.x;
            out.vector.y=spatial.vector.y;
            out.rotation=spatial.rotation;
            return out;
        }
    }
}
