package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.*;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.shape.AbstractShape;


/**
 * A geometric shape, used to construct both fixtures and textures.
 */
public class Shape<SHAPETYPE extends AbstractShape> implements ISerializable<Shape> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private static ShapePool POOL = new ShapePool(10, 1000);
    private SHAPETYPE geometry;

    public Shape(){
    }

    public SHAPETYPE getGeometry(){
        return geometry;
    }

    public SHAPETYPE obtain (Class<SHAPETYPE> type) {
        geometry = POOL.obtain(type);
        return geometry;
    }

    @Override
    public void reset() {
        LOG.info("Resetting shape component");
        if(geometry !=null) {
            LOG.info("Freeing shape");
            POOL.free(geometry);
            geometry.reset();
            geometry =null;
        }

    }

    public Shape addCopy(GnompEngine gnompEngine,Entity entity){
        return null;
    }



    private static class ShapePool {
        private ObjectMap<Class<?>, ReflectionPool> pools;
        private int initialSize;
        private int maxSize;

        ShapePool(int initialSize, int maxSize) {
            this.pools = new ObjectMap<Class<?>, ReflectionPool>();
            this.initialSize = initialSize;
            this.maxSize = maxSize;
        }

        public <T> T obtain (Class<T> type) {
            ReflectionPool pool = pools.get(type);

            if (pool == null) {
                pool = new ReflectionPool(type, initialSize, maxSize);
                pools.put(type, pool);
            }

            return (T)pool.obtain();
        }

        void free(Object object) {
            if (object == null) {
                throw new IllegalArgumentException("object cannot be null.");
            }

            ReflectionPool pool = pools.get(object.getClass());

            if (pool == null) {
                return; // Ignore freeing an object that was never retained.
            }

            pool.free(object);
        }

        public void freeAll (Array objects) {
            if (objects == null) throw new IllegalArgumentException("objects cannot be null.");

            for (int i = 0, n = objects.size; i < n; i++) {
                Object object = objects.get(i);
                if (object == null) continue;
                free(object);
            }
        }

        public void clear () {
            for (Pool pool : pools.values()) {
                pool.clear();
            }
        }
    }
}
