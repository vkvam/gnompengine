package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;

public class StatsSystem extends EntitySystem implements ApplicationListener {
    private static float ROOT2 = (float) Math.sqrt(2);

    public SpriteBatch batch;
    BitmapFont font; //or use alex answer to use custom font

    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);
    private CameraSystem cameraSystem;
    private String stats = "";


    Texture t;
    public StatsSystem(int priority, CameraSystem cameraSystem) {
        super(priority);

        this.cameraSystem = cameraSystem;
        this.font =  new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.batch = new SpriteBatch();


        this.batch = new SpriteBatch();
        Pixmap pm = new Pixmap(Gdx.files.internal("data/9-128.png"));
        t = new Texture(pm);
        pm.dispose();

    }

    public void addStat(String stat){
        stats+=stat+"\n";
    }
    public void addLine() {
        addStat("               ");
    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    @Override
    public void update(float f) {
        super.update(f);
        OrthographicCamera orthographicCamera = cameraSystem.getHudCamera();

        batch.setProjectionMatrix(orthographicCamera.combined);

        float w = orthographicCamera.viewportWidth,h=orthographicCamera.viewportHeight;

        batch.begin();
        font.draw(batch,stats,-w/2+2,h/2-2,w/4, Align.topLeft,true);
        batch.end();
        stats="";
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
