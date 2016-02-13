package com.flatfisk.gnomp.gdx;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.GnompEngine;


public class GnompApplicationListener implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    protected GnompEngine engine;

    @Override
    public void create() {
        engine = new GnompEngine();
    }

    @Override
    public void resize(int width, int height) {
        for (EntitySystem system : engine.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).resize(width, height);
            }
        }
    }

    @Override
    public void render() {
        engine.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
        for (EntitySystem system : engine.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).pause();
            }
        }
    }

    @Override
    public void resume() {
        for (EntitySystem system : engine.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).resume();
            }
        }
    }

    @Override
    public void dispose() {
        engine.removeAllEntities();
        engine.clearPools();
        for (EntitySystem system : engine.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).dispose();
            }
        }
    }
}