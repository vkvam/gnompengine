package com.flatfisk.gnomp.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Vemund Kvam on 13/01/16.
 */
public class Pools {
    private static Vector2PoolProvider vector2Pool = new Vector2PoolProvider();

    public static Vector2 obtainFromCopy(Vector2 vector){
        return vector2Pool.createCopy(vector);
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
}
