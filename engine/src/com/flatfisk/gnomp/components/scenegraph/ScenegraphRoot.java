package com.flatfisk.gnomp.components.scenegraph;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.ConstructorComponent;
import com.badlogic.ashley.core.GnompEngine;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class ScenegraphRoot implements ConstructorComponent<ScenegraphRoot>,Pool.Poolable {

    @Override
    public void reset() {

    }

    @Override
    public ScenegraphRoot addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(getClass(),entity);
    }
}
