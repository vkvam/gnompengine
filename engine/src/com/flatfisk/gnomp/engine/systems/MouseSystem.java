package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MouseSystem extends EntitySystem implements ApplicationListener {

    public SpriteBatch batch;
    private CameraSystem cameraSystem;
    Texture t;

    public MouseSystem(int priority, CameraSystem cameraSystem) {
        super(priority);
        this.cameraSystem = cameraSystem;
        this.batch = new SpriteBatch();
        Pixmap pm = new Pixmap(Gdx.files.internal("data/9-128.png"));
        t = new Texture(pm);
        pm.dispose();

    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    @Override
    public void update(float f) {


        CameraSystem s = getEngine().getSystem(CameraSystem.class);
        Viewport p = s.worldViewPort;

        Vector3 v = s.getHudCamera().getPickRay(Gdx.input.getX(), Gdx.input.getY(),
                p.getScreenX(), p.getScreenY(), p.getScreenWidth(), p.getScreenHeight()).origin;



        batch.begin();
        batch.setProjectionMatrix(
                getEngine().getSystem(CameraSystem.class).getHudCamera().combined
        );
        batch.draw(t,v.x-16,v.y-16);
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
        Gdx.app.log(getClass().getName(), "Resizing render system: w="+w+"h="+h);

    }
}
