package com.flatfisk.gnomp.engine.components.abstracts;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;


public interface ISerializable<T extends ISerializable> extends Component, Pool.Poolable {
    T addCopy(GnompEngine gnompEngine, Entity entity);
}
