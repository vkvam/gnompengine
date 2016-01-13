package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public interface RelativeComponent extends Component, Pool.Poolable {
    public Relative getRelativeType();
}
