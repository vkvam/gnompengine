package com.flatfisk.gnomp.gdx;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;
<<<<<<< HEAD
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;


public class GnompEngineApplicationListener implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
=======

import java.util.HashMap;


public class GnompEngineApplicationListener implements ApplicationListener {

    protected HashMap<Class, ApplicationModule> modules;
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    protected PooledEngine world;

    @Override
    public void create() {
        world = new PooledEngine();
<<<<<<< HEAD
=======
        modules = new HashMap<Class, ApplicationModule>();
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

    @Override
    public void resize(int width, int height) {
        for (EntitySystem system : world.getSystems()) {
<<<<<<< HEAD
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).resize(width, height);
            }
        }
=======
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).resize(width, height);
            }
        }

        for (ApplicationModule module : modules.values()) {
            module.resize(width, height);
        }
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

    @Override
    public void render() {
<<<<<<< HEAD
        world.update(Gdx.graphics.getDeltaTime());
=======
        for (ApplicationModule module : modules.values()) {
            module.render();
        }
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).render();
            }
        }
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

    @Override
    public void pause() {
<<<<<<< HEAD
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).pause();
=======
        for (ApplicationModule module : modules.values()) {
            module.pause();
        }
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).pause();
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
            }
        }
    }

    @Override
    public void resume() {
<<<<<<< HEAD
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).resume();
=======
        for (ApplicationModule module : modules.values()) {
            module.resume();
        }
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).resume();
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
            }
        }
    }

    @Override
    public void dispose() {
<<<<<<< HEAD
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof ApplicationListener) {
                ((ApplicationListener) system).dispose();
            }
        }
        world.clearPools();
=======
        for (ApplicationModule module : modules.values()) {
            module.dispose();
        }
        for (EntitySystem system : world.getSystems()) {
            if (system instanceof GdxSystem) {
                ((GdxSystem) system).dispose();
            }
        }
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }
}