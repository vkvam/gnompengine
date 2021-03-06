package com.flatfisk.gnomp.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.math.Transform;


public class Pools {
    private static Vector2PoolProvider vector2Pool = new Vector2PoolProvider();
    private static SpatialPoolProvider spatialPool = new SpatialPoolProvider();

    public static Transform obtainTransform(){
        return spatialPool.obtain();
    }
    public static Vector2 obtainVector(){
        return vector2Pool.obtain();
    }

    public static Vector2 obtainVector2FromCopy(Vector2 vector){
        return vector2Pool.createCopy(vector);
    }
    public static Transform obtainSpatialFromCopy(Transform transform){
        return spatialPool.createCopy(transform);
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

    public static class SpatialPoolProvider extends Pool<Transform>{

        @Override
        protected Transform newObject() {
            return new Transform();
        }

        public Transform createCopy(Transform transform){
            Transform out = obtain();
            out.vector.x= transform.vector.x;
            out.vector.y= transform.vector.y;
            out.rotation= transform.rotation;
            return out;
        }
    }
}
