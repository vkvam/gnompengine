package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.abstracts.ISerializable;

/**
 * Created by Vemund Kvam on 15/01/16.
 */
public class EndPoint implements ISerializable<EndPoint>,Component,Pool.Poolable{

    @Override
    public void reset() {

    }

    @Override
    public EndPoint addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }
}
