package com.flatfisk.gnomp.components.roots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.ConstructorComponent;
import com.badlogic.ashley.core.GnompEngine;


public class RenderableDef implements ConstructorComponent<RenderableDef>,Pool.Poolable {

    public int zIndex=0;
    @Override
    public void reset() {
    }

    @Override
    public RenderableDef addCopy(GnompEngine gnompEngine, Entity entity) {
        RenderableDef renderableDef = gnompEngine.addComponent(getClass(),entity);
        renderableDef.zIndex = zIndex;
        return renderableDef;
    }
}
