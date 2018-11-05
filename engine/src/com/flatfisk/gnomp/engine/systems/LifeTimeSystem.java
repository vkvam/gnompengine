package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.LifeTime;

/**
 * Controls the lifetime of entities.
 *
 * When lifeTime reaches zero, it is removed from the engine.
 */
public class LifeTimeSystem extends IteratingSystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);
    private final ComponentMapper<LifeTime> lifeTimeComponentMapper = ComponentMapper.getFor(LifeTime.class);
    private float passedTime = 0;

    public LifeTimeSystem(int priority) {
        super(Family.all(LifeTime.class).get(), priority);
    }


    @Override
    public void processEntity(Entity entity, float f) {
        LifeTime bullet = lifeTimeComponentMapper.get(entity);
        if ((bullet.lifeTime -= passedTime) < 0) {
            getEngine().removeEntity(entity);
        }
    }

    @Override
    public void update(float deltaTime) {
        passedTime += deltaTime;
        if (passedTime > 0.1f) {
            super.update(deltaTime);
            passedTime = 0;
        }
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

    }
}
