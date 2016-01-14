package com.github.vkvam.gnompengine.gdx;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;


public class GnompEngineApplicationListener implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    protected PooledEngine world;

    @Override
    public void create() {
        world = new PooledEngine();
    }

    @Override
    public void resize(int width, int height) {
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).resize(width, height);
            }
        }
    }

    @Override
    public void render() {
        world.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).pause();
            }
        }
    }

    @Override
    public void resume() {
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).resume();
            }
        }
    }

    @Override
    public void dispose() {
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).dispose();
            }
        }
        world.clearPools();
    }
}