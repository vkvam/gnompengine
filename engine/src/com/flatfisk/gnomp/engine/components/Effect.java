package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class Effect implements ISerializable<Effect>, Pool.Poolable  {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public String effectFileName = "";
    public Transform offset = new Transform();
    public Array<String> initialEmitters = new Array<String>(1);

    @Override
    public Effect addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }

    @Override
    public void reset() {
        effectFileName = "";
        initialEmitters.clear();
        offset.setZero();
    }

    public static class Container implements Component, Pool.Poolable {
        private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
        public Array<ParticleEmitter> unusedEmitters = new Array<ParticleEmitter>();
        
        public ParticleEffectPool.PooledEffect particleEffect;
        public Transform offset = new Transform();

        private float scaleCompensation = 1;

        @Override
        public void reset() {
            LOG.info("REMOVED LIGHT");
            scaleCompensation = 1;
            // Add emitters back to effect before removing it.
            for(ParticleEmitter emitter : unusedEmitters){
                particleEffect.getEmitters().add(emitter);
            }

            // Also calls reset
            particleEffect.free();
            unusedEmitters.clear();

            offset.setZero();
        }

    }

}
