package com.flatfisk.gnomp.gdx;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;

import java.util.HashMap;


public class GnompEngineApplicationListener implements ApplicationListener {

    protected HashMap<Class, ApplicationModule> modules;
    protected PooledEngine world;

    @Override
    public void create() {
        world = new PooledEngine();
        modules = new HashMap<Class, ApplicationModule>();
    }

    @Override
    public void resize(int width, int height) {
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).resize(width, height);
            }
        }

        for (ApplicationModule module : modules.values()) {
            module.resize(width, height);
        }
    }

    @Override
    public void render() {
        for (ApplicationModule module : modules.values()) {
            module.render();
        }
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).render();
            }
        }
    }

    @Override
    public void pause() {
        for (ApplicationModule module : modules.values()) {
            module.pause();
        }
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).pause();
            }
        }
    }

    @Override
    public void resume() {
        for (ApplicationModule module : modules.values()) {
            module.resume();
        }
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).resume();
            }
        }
    }

    @Override
    public void dispose() {
        for (ApplicationModule module : modules.values()) {
            module.dispose();
        }
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).dispose();
            }
        }
    }
}