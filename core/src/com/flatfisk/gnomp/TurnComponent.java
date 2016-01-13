package com.flatfisk.gnomp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
* Created by Vemund Kvam on 02/12/15.
*/
public class TurnComponent implements Component, Pool.Poolable {
    public float turnAcceleration = 40f;
    public float turnSpeed = 1f;
    public float currentSpeed = 0f;

    @Override
    public void reset() {

    }
}
