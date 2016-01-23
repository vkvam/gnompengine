package com.flatfisk.gnomp.components.roots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.ConstructorComponent;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;

/**
 * Created by a-004213 on 22/04/14.
 */
public class SpatialDef implements SpatialRelative.Controller, ConstructorComponent<SpatialDef>, Pool.Poolable{
    @Override
    public void reset() {

    }


    @Override
    public SpatialDef addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }
}
