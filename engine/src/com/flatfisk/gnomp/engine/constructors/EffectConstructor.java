package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.Effect;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.systems.EffectSystem;

import java.util.Iterator;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class EffectConstructor extends Constructor<Effect,Spatial.Node,Effect.Container, Effect.Container> {
    public final GnompEngine engine;




    public EffectConstructor(GnompEngine engine) {
        super(Effect.class, Spatial.Node.class, null);
        this.engine = engine;
    }

    @Override
    public Effect.Container parentAdded(Entity entity, Spatial.Node constructorOrientation) {
        Effect effectDef = constructorMapper.get(entity);

        Effect.Container container = engine.addComponent(Effect.Container.class,entity);

        EffectSystem.ParticleEffectPoolWrapper effectPoolWrapper = EffectSystem.EFFECT_POOLS.get(effectDef.effectFileName);
        ParticleEffectPool effectPool = effectPoolWrapper==null?null:effectPoolWrapper.pool;

        if(effectPool==null) {

            ParticleEffect constructorEffect = new ParticleEffect();
            constructorEffect.load(Gdx.files.internal(effectDef.effectFileName), Gdx.files.internal("data"));

            EffectSystem.ParticleEffectPoolWrapper wrapper = new EffectSystem.ParticleEffectPoolWrapper(constructorEffect);

            EffectSystem.EFFECT_POOLS.put(
                    effectDef.effectFileName,
                    wrapper
            );

            effectPool = wrapper.pool;
        }

        container.particleEffect = effectPool.obtain();

        Iterator<ParticleEmitter> particleEmitterIterator = container.particleEffect.getEmitters().iterator();
        while(particleEmitterIterator.hasNext()){
            ParticleEmitter emitter = particleEmitterIterator.next();

            if(! effectDef.initialEmitters.contains(emitter.getName(),false) ){
                container.unusedEmitters.add(emitter);
                particleEmitterIterator.remove();
            }

        }


        return container;
    }

    @Override
    public void parentRemoved(Entity entity) {

    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
