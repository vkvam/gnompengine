package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.GnompMappers;
import com.flatfisk.gnomp.engine.components.Label;
import com.flatfisk.gnomp.engine.components.Spatial;

// TODO: Add culling
public class LabelSystem extends IteratingSystem implements ApplicationListener {
    private final float scale;
    protected boolean isMap;

    public SpriteBatch batch;
    BitmapFont font; //or use alex answer to use custom font

    private CameraSystem cameraSystem;


    public LabelSystem(int priority, CameraSystem cameraSystem, float scale) {
        super(Family.all(Label.class).get(), priority);

        this.cameraSystem = cameraSystem;
        this.font =  new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.batch = new SpriteBatch();
        this.isMap = false;
        this.scale = scale;

    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    @Override
    public void update(float f) {
        OrthographicCamera hud = isMap ? cameraSystem.getMapCamera(): cameraSystem.getWorldCamera();//cameraSystem.getHudCamera();

        batch.setProjectionMatrix(hud.combined);
        batch.begin();
        super.update(f);
        batch.end();

    }

    @Override
    protected void processEntity(Entity entity, float v) {
        Spatial.Node n = GnompMappers.spatialNodeMap.get(entity);
        Label l = GnompMappers.labelMapper.get(entity);
        font.getData().setScale(l.scale*this.scale);
        font.draw(batch, l.text, n.world.vector.x+l.offset.x, n.world.vector.y+l.offset.y, 0, Align.center, true);

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
