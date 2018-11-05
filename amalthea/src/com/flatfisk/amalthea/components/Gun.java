package com.flatfisk.amalthea.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class Gun implements ISerializable<Gun>,Component, Pool.Poolable {

    @Override
    public void reset() {
    }

    @Override
    public Gun addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }
}
