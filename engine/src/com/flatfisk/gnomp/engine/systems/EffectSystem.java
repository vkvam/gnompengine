package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.engine.components.Effect;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;


/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class EffectSystem extends IteratingSystem implements ApplicationListener {
    private final CameraSystem cameraSystem;
    private final Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public static final ObjectMap<String, ParticleEffectPoolWrapper> EFFECT_POOLS = new ObjectMap();
    public final SpriteBatch spriteBatch = new SpriteBatch();
    public final ComponentMapper<Effect.Container> containerComponentMapper = ComponentMapper.getFor(Effect.Container.class);
    public final ComponentMapper<Spatial.Node> spatialMapper = ComponentMapper.getFor(Spatial.Node.class);


    public EffectSystem(int priority, CameraSystem cameraSystem) {
        super(Family.all(Effect.Container.class).get(),priority);
        this.cameraSystem = cameraSystem;
    }


    @Override
    public void update(float deltaTime) {
        spriteBatch.setProjectionMatrix(cameraSystem.getCamera().combined);
        spriteBatch.begin();
        super.update(deltaTime);
        spriteBatch.end();

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Effect.Container effect = containerComponentMapper.get(entity);
        Transform world = spatialMapper.get(entity).world;

        Vector2 vector2 = Pools.obtain(Vector2.class);
        vector2.set(effect.offset.vector);
        vector2.rotate(world.rotation);

        effect.particleEffect.setPosition(world.vector.x+vector2.x,world.vector.y+vector2.y);
        effect.particleEffect.draw(spriteBatch, deltaTime);
        Pools.free(vector2);
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        for(ParticleEffectPoolWrapper wrapper : EFFECT_POOLS.values()){
            wrapper.effect.dispose();
        }
    }

    public static class ParticleEffectPoolWrapper{
        public ParticleEffectPoolWrapper(ParticleEffect effect) {
            this.pool = new ParticleEffectPool(effect,1,10);
            this.effect = effect;
        }

        public ParticleEffect effect;
        public ParticleEffectPool pool;
    }
}
