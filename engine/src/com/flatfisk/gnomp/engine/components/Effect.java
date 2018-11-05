package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;

/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class Effect implements ISerializable<Effect>, Pool.Poolable  {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);
    public String effectFileName = "";
    public Array<String> initialEmitters = new Array<String>(1);

    @Override
    public Effect addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }

    @Override
    public void reset() {
        effectFileName = "";
        initialEmitters.clear();
    }

    public static class Container implements Component, Pool.Poolable {
        private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);
        public Array<ParticleEmitter> unusedEmitters = new Array<ParticleEmitter>();
        
        public ParticleEffectPool.PooledEffect particleEffect;


        @Override
        public void reset() {
            Gdx.app.debug(getClass().getName(), "REMOVED LIGHT");
            // Add emitters back to effect before freeing it.
            for(ParticleEmitter emitter : unusedEmitters){
                particleEffect.getEmitters().add(emitter);
            }

            particleEffect.free(); // Calls reset as well
            unusedEmitters.clear();
            particleEffect.dispose();
        }

    }

}
