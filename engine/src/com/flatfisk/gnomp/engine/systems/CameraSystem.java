package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Logger;

public class CameraSystem extends EntitySystem implements ApplicationListener {
    private static float ROOT2 = (float) Math.sqrt(2);


    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private OrthographicCamera orthographicCamera;

    public CameraSystem(int priority) {
        super(priority);
        orthographicCamera = new OrthographicCamera(640, 480);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    public OrthographicCamera getCamera() {
        return orthographicCamera;
    }

    @Override
    public void update(float f) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthographicCamera.update(true);
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

    }


    @Override
    public void resize(final int w, final int h) {
        LOG.info("Resizing render system: w="+w+"h="+h);

        //Gdx.graphics.setDisplayMode(w, h, false);
        Gdx.graphics.setWindowedMode(w,h);
        orthographicCamera.update();
        orthographicCamera.viewportWidth = w;
        orthographicCamera.viewportHeight = h;
    }

}
