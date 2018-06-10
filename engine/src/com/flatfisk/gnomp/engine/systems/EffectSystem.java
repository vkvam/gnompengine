package com.flatfisk.gnomp.engine.systems;

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
import com.flatfisk.gnomp.engine.components.Effect;
import com.flatfisk.gnomp.math.Transform;

import static com.flatfisk.gnomp.engine.GnompMappers.*;


public class EffectSystem extends IteratingSystem implements ApplicationListener {
    private final CameraSystem cameraSystem;
    private final Logger LOG = new Logger(this.getClass().getName(), Logger.DEBUG);
    public static final ObjectMap<String, ParticleEffectPoolWrapper> EFFECT_POOLS = new ObjectMap<String, ParticleEffectPoolWrapper>();
    private final SpriteBatch spriteBatch = new SpriteBatch();

    private StatsSystem statsSystem;
    private int effectsDrawn = 0;


    public EffectSystem(int priority, CameraSystem cameraSystem) {
        super(Family.all(Effect.Container.class).get(), priority);
        this.cameraSystem = cameraSystem;
    }

    public void setStatsSystem(StatsSystem statsSystem) {
        this.statsSystem = statsSystem;
    }

    @Override
    public void update(float deltaTime) {
        spriteBatch.setProjectionMatrix(cameraSystem.getWorldCamera().combined);
        spriteBatch.begin();
        super.update(deltaTime);
        spriteBatch.end();

        if (statsSystem != null) {
            statsSystem.addStat("Effects");
            statsSystem.addStat("Processed:" + effectsDrawn);
            statsSystem.addLine();
        }
        effectsDrawn = 0;

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Effect.Container effect = effectMap.get(entity);
        Transform world = spatialNodeMap.get(entity).world;
        Vector2 worldVector = world.vector;

        effect.particleEffect.setPosition(worldVector.x, worldVector.y);
        effect.particleEffect.draw(spriteBatch, deltaTime);
        effectsDrawn++;
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
        LOG.info("Disposing effect batch");
        spriteBatch.dispose();
        for (ParticleEffectPoolWrapper wrapper : EFFECT_POOLS.values()) {
            LOG.info("Disposing effect");
            wrapper.effect.dispose();
        }
    }

    public static class ParticleEffectPoolWrapper {
        public ParticleEffectPoolWrapper(ParticleEffect effect) {
            this.pool = new ParticleEffectPool(effect, 1, 10);
            this.effect = effect;
        }

        ParticleEffect effect;
        public ParticleEffectPool pool;
    }


}
