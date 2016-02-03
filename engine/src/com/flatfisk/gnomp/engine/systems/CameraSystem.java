package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;

public class CameraSystem extends EntitySystem implements ApplicationListener{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private OrthographicCamera orthographicCamera;

    private float virtualWidth,
            virtualHeight;
    private float setAspectRatio;
    public Rectangle viewport = new Rectangle();

    public CameraSystem(int priority, int width, int height) {
        super(priority);

        orthographicCamera = new OrthographicCamera(width, height);
        virtualWidth = width;
        virtualHeight = height;
        setAspectRatio = virtualWidth/virtualHeight;

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

        // update camera
        orthographicCamera.update();

        // set viewport
        Gdx.gl.glViewport((int) viewport.x,(int) viewport.y, (int) viewport.width, (int) viewport.height);
        // clear previous frame
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


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
    public void resize(int width, int height)
    {
        // calculate new viewport
        float aspectRatio = (float)width/(float)height;
        float scale;
        Vector2 crop = new Vector2(0f, 0f);

        if(aspectRatio > setAspectRatio)
        {
            scale = (float)height/virtualHeight;
            crop.x = (width - virtualWidth*scale)/2f;
        }
        else if(aspectRatio < setAspectRatio)
        {
            scale = (float)width/virtualWidth;
            crop.y = (height - virtualHeight*scale)/2f;
        }
        else
        {
            scale = (float)width/virtualWidth;
        }

        float w = virtualWidth*scale;
        float h = virtualHeight*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
    }

}
