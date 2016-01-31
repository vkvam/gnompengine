package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.EntitySystem;

public class RenderFinalizeSystem extends EntitySystem{
    private static float ROOT2 = (float) Math.sqrt(2);


    public RenderFinalizeSystem(int priority) {
        super(priority);
    }

    @Override
    public void update(float f) {
        getEngine().getSystem(RenderSystem.class).batch.end();
    }

}
