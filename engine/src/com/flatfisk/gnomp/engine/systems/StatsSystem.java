package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Spatial;

import java.util.Comparator;

public class StatsSystem extends EntitySystem implements ApplicationListener {
    private static float ROOT2 = (float) Math.sqrt(2);

    public SpriteBatch batch;

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private Array<Entity> renderQueue = new Array<Entity>();

    public ComponentMapper<Renderable.Constructed> renderableMapper;
    public ComponentMapper<Spatial.Node> orientationMapper;
    private Comparator comperator;
    private CameraSystem cameraSystem;

    public StatsSystem(int priority, CameraSystem cameraSystem) {
        super(priority);

        this.cameraSystem = cameraSystem;

        batch = new SpriteBatch();

    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    @Override
    public void update(float f) {
        super.update(f);
        OrthographicCamera orthographicCamera = cameraSystem.getCamera();

        batch.setProjectionMatrix(orthographicCamera.combined);

        batch.begin();
        batch.end();
    }

    @Override
    public void create() {

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
        batch.dispose();
    }


    @Override
    public void resize(final int w, final int h) {
        LOG.info("Resizing render system: w="+w+"h="+h);

    }

}
