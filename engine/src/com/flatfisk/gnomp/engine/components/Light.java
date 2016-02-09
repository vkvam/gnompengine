package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.components.light.LightDef;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class Light implements ISerializable<Light>, Pool.Poolable  {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public LightDef lightDef;

    @Override
    public Light addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }

    @Override
    public void reset() {

    }

    public static class Container implements Component, Pool.Poolable {
        private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
        public box2dLight.Light light;
        public Transform offset = new Transform();

        public Transform worldTransform = new Transform();
        public Vector2 worldRotatedOffset = new Vector2();

        @Override
        public void reset() {
            LOG.info("REMOVED LIGHT");
            light.remove();
            offset.setZero();
        }

    }

}
