package com.flatfisk.amalthea.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class Bullet implements ISerializable<Bullet>,Component, Pool.Poolable {

    public boolean inert = false;

    @Override
    public void reset() {
        inert = false;
    }

    @Override
    public Bullet addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }
}
