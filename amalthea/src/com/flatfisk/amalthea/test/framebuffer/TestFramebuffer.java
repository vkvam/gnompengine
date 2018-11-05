package com.flatfisk.amalthea.test.framebuffer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.factories.procedural.DockGenerator;
import com.flatfisk.amalthea.factories.procedural.IslandGenerator;
import com.flatfisk.amalthea.factories.procedural.IslandPopulator;
import com.flatfisk.gnomp.utils.Pools;

import java.util.List;

public class TestFramebuffer implements ApplicationListener {

    private Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);

    ShapeRenderer shapeRenderer;
    FrameBuffer fbo;
    Texture myTex;
    SpriteBatch batch;


    private void createBackground() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 320, 240, false);
        fbo.begin();
        Gdx.gl.glViewport(0, 0, 640,480);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f); //transparent black
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT); //clear the color buffer

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(0, 0, 32,24);
        myTex = fbo.getColorBufferTexture();
        shapeRenderer.end();

        fbo.end();

    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        createBackground();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, 640,480);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(myTex,0,0, 0,0,320,240,1,1,0,0,0,320,240,false, true);
        batch.end();
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
}
