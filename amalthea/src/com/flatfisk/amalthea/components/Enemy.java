package com.flatfisk.amalthea.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Vemund Kvam on 27/01/16.
 */
public class Enemy implements Component, Pool.Poolable{
    public int shotTimes = 0;
    public float keepStatusFor = 0;
    public Vector2 target = new Vector2(0,0);
    public Vector2 tempTarget = new Vector2(0,0);
    public boolean approachingLastTarget = false;
    public float targetReachedLimit = 2;

    public int pathIndex = 0;
    public Array<Vector2> pathToTarget;

    public MovingStatus movingStatus = MovingStatus.IDLE;


    @Override
    public void reset() {

    }

    public static enum MovingStatus{
        IDLE,
        APPROACHING_TARGET,
        AT_TARGET, REACHED_TARGET
    }
}
