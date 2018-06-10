package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;


public class PhysicalProperties implements ISerializable<PhysicalProperties>, Pool.Poolable {
    public boolean isSensor = false;
    public float density = 1;
    public float friction = 1;
    public short categoryBits = 1;
    public short maskBits = 1;

    @Override
    public PhysicalProperties addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }

    @Override
    public void reset() {

    }
}
