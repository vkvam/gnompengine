package com.flatfisk.amalthea.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class MapColor implements Component, Pool.Poolable{

    public Color color = Color.CLEAR;

    @Override
    public void reset() {
        color = Color.CLEAR;
    }

}
