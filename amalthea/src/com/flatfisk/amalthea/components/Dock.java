package com.flatfisk.amalthea.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Dock implements Component,Pool.Poolable{
    // If visitor id = -1, there is no visitor;
    public Array<Entity> visitorEntities = new Array<Entity>(1);
    public int visitor_capacity = 1;

    // 1 = open, 0 = closed
    public float doorPosition = 0;
    public boolean closedDesired = true;
    public boolean closed = true;
    public Entity leftDoor;
    public Entity rightDoor;
    public float doorWidth = 100;


    @Override
    public void reset() {
        visitor_capacity = 1;
        visitorEntities.clear();
    }
}
