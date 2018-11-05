package com.flatfisk.amalthea.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.components.LightFlicker;
import com.flatfisk.gnomp.engine.components.Light;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class LightFlickerSystem extends IteratingSystem{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);


    public LightFlickerSystem(int priority) {
        super(Family.all(LightFlicker.class).get(), priority);
    }


    @Override
    protected void processEntity(Entity entity, float v) {
        // timer += v;
        LightFlicker flicker = entity.getComponent(LightFlicker.class);
        Light.Container light = entity.getComponent(Light.Container.class);

        if (light == null){
            return;
        }

        if(flicker.flickerType == LightFlicker.FlickerType.FADE) {
            light.light.getColor().a = flicker.timer;
            light.light.setColor(light.light.getColor());
            flicker.timer += v*flicker.direction*flicker.speed;

            if(flicker.timer>flicker.max){
                flicker.timer = flicker.max;
                flicker.direction = -1;
            }else if (flicker.timer<flicker.min){
                flicker.timer = flicker.min;
                flicker.direction = 1;
            }
        }else {
            if (Math.random() > 0.994f) {
                flicker.timer = (float) Math.random() * 0.4f;
            }

            if (flicker.timer > 0) {
                flicker.timer -= v;
                light.light.getColor().a = (float) Math.random() * 0.2f + 0.5f;
                light.light.setColor(light.light.getColor());
            } else {
                flicker.timer = 0;
                light.light.getColor().a = 0.7f;
                light.light.setColor(light.light.getColor());
            }
        }
    }

}
