package com.flatfisk.gnomp.components.roots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.ConstructorComponent;
import com.badlogic.ashley.core.GnompEngine;


public class StructureDef implements ConstructorComponent<StructureDef>,Pool.Poolable {

    @Override
    public void reset() {
    }

    @Override
    public StructureDef addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }
}
