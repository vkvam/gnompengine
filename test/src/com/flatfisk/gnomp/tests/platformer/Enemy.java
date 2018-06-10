package com.flatfisk.gnomp.tests.platformer;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;


public class Enemy implements Component, Pool.Poolable{
    int shotTimes = 0;
    float movedAmount = 0;
    float amountToMove = 1f;
    boolean movingLeft = false;
    boolean startedMoving=false;
    float startingPositionX;
    @Override
    public void reset() {

    }
}
