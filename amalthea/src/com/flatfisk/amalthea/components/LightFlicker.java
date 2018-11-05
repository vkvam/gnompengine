package com.flatfisk.amalthea.components;

import com.badlogic.ashley.core.Component;



public class LightFlicker implements Component{
    public float timer = 0;
    public float direction = 1f;
    public float speed = 0.1f;
    public float min = 0.7f;
    public float max = 1f;
    public FlickerType flickerType = FlickerType.FADE;
    public static enum FlickerType{
        FLICKER,
        FADE
    }
}
