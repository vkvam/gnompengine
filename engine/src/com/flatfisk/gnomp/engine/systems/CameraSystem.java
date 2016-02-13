package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;

public class CameraSystem extends EntitySystem implements ApplicationListener{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private OrthographicCamera worldCamera;
    private OrthographicCamera hudCamera;
    private Matrix4 physicsMatrix = new Matrix4();

    private float virtualWidth, virtualHeight;
    private float virtualAspectRatio;
    public float scale = 1;
    public Rectangle viewport = new Rectangle();

    public CameraSystem(int priority, int width, int height) {
        super(priority);

        worldCamera = new OrthographicCamera(width, height);
        hudCamera = new OrthographicCamera(width, height);
        virtualWidth = width;
        virtualHeight = height;
        virtualAspectRatio = virtualWidth/virtualHeight;

    }

    public Matrix4 getPhysicsMatrix() {
        return physicsMatrix;
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    @Override
    public void update(float f) {
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldCamera.update();
        hudCamera.update();
        physicsMatrix.set(worldCamera.combined);
        physicsMatrix.scale(PhysicsConstants.PIXELS_PER_METER, PhysicsConstants.PIXELS_PER_METER, 1);

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
        float screenAspectRatio = (float)width/(float)height;
        //float scale;
        Vector2 crop = new Vector2(0f, 0f);

        if(screenAspectRatio > virtualAspectRatio){
            scale = (float) height / virtualHeight;
            crop.x = (width - virtualWidth*scale)/2f;
        }else if(screenAspectRatio < virtualAspectRatio){
            scale = (float) width / virtualWidth;
            crop.y = (height - virtualHeight*scale)/2f;
        }else{
            scale = (float) width / virtualWidth;
        }

        float w = virtualWidth*scale;
        float h = virtualHeight*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
    }

}
