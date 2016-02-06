package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
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

    public LightDef lightDef;

    @Override
    public Light addCopy(GnompEngine gnompEngine, Entity entity) {
        return new Light();
    }

    @Override
    public void reset() {

    }

    public static class Container implements Component, Pool.Poolable {
        private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
        public box2dLight.Light light;
        public Transform offset;

        @Override
        public void reset() {
            //light.remove();
        }

    }

}