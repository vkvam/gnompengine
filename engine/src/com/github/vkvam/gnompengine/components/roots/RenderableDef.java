package com.github.vkvam.gnompengine.components.roots;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;


public class RenderableDef implements Component,Pool.Poolable {

    public int zIndex=0;
    @Override
    public void reset() {
    }
}
