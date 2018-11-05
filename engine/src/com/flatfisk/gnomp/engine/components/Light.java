package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.components.light.LightDef;

/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class Light implements ISerializable<Light>, Pool.Poolable  {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);
    public LightDef lightDef;

    @Override
    public Light addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }

    @Override
    public void reset() {

    }

    public static class Container implements Component, Pool.Poolable {
        private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);
        public box2dLight.Light light;

        @Override
        public void reset() {
            Gdx.app.debug(getClass().getName(),"REMOVED LIGHT");
            light.remove();
        }

    }

}
