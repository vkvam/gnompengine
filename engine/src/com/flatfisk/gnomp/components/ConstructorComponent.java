package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.ashley.core.GnompEngine;

/**
 * Created by Vemund Kvam on 17/01/16.
 */
public interface ConstructorComponent<T extends ConstructorComponent> extends Component, Pool.Poolable {
    public T addCopy(GnompEngine gnompEngine,Entity entity);
}
