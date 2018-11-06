package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.flatfisk.gnomp.PhysicsConstants;

public class CameraSystem extends EntitySystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);
    private OrthographicCamera worldCamera;
    private OrthographicCamera mapCamera;
    private OrthographicCamera hudCamera;
    private Matrix4 physicsMatrix = new Matrix4();

    public float virtualWidth;
    public float virtualHeight;
    private float virtualAspectRatio;
    public float scale = 1;
    private float mapFractionOfScreen;

    public FitViewport worldViewPort;
    public FitViewport mapViewPort;
    public FitViewport hudViewPort;

    private Texture mapBackgroundTexture;

    private Matrix4 ONE_MATRIX = new Matrix4();
    private final SpriteBatch batch;

    BackgroundProvider backgroundProvider;
    private Vector2 backgroundPosition = new Vector2();
    public int mapX;
    public int mapY;


    public CameraSystem(int priority, int width, int height, float mapFractionOfScreen) {
        super(priority);


        this.mapFractionOfScreen = mapFractionOfScreen;
        worldCamera = new OrthographicCamera();
        //mapCamera = new OrthographicCamera(width*mapFractionOfScreen, height*mapFractionOfScreen);
        mapCamera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();

        batch = new SpriteBatch();

        createTextures();

        virtualWidth = width;
        virtualHeight = height;
        virtualAspectRatio = virtualWidth / virtualHeight;

        ONE_MATRIX.setToOrtho2D(0, 0, virtualWidth, virtualHeight);
        /*


        batch = new SpriteBatch();
        worldCamera = new OrthographicCamera(width, height);
        mapCamera = new OrthographicCamera(width, height);
        hudCamera = new OrthographicCamera(width, height);
        */
        worldViewPort = new FitViewport(width, height, worldCamera);

        mapViewPort = new FitViewport(width * mapFractionOfScreen, height * mapFractionOfScreen, mapCamera);
        hudViewPort = new FitViewport(width, height, hudCamera);

    }

    Matrix4 getPhysicsMatrix() {
        return physicsMatrix;
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public OrthographicCamera getMapCamera() {
        return mapCamera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    private void createTextures() {
        Pixmap pixMap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixMap.setColor(new Color(0, 0.2f, 0.35f, 1f));
        pixMap.fill();
        mapBackgroundTexture = new Texture(pixMap);
        pixMap.dispose();
    }

    public void setBackgroundProvider(BackgroundProvider backgroundProvider) {
        this.backgroundProvider = backgroundProvider;
    }

    private void drawBackground() {
        if(this.backgroundProvider == null){
            return;
        }
        backgroundPosition.set(worldCamera.position.x, worldCamera.position.y).scl(0.2f);

        for (Sprite t : this.backgroundProvider.getTexture(backgroundPosition)) {

            batch.draw(
                    t.getTexture(),                   // Texture texture
                    t.getX(),                          // float x
                    t.getY(),                         // float y
                    0,                             // float originX
                    0,                             // float originY
                    1280,                                 // float width
                    720,                                 // float height
                    1,                                  // float scaleX
                    1,                                  // float scaleY
                    0,                 // float rotation
                    0,                                  // int srcX
                    0,                                  // int srcY
                    t.getTexture().getWidth(),                                 // int srcHeight
                    t.getTexture().getHeight(),                                 // int srcWidth
                    false,                              // boolean flipX
                    true                                // boolean flipY
            );
        }

    }


    @Override
    public void update(float f) {
        //Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
        Gdx.gl.glViewport(worldViewPort.getScreenX(), worldViewPort.getScreenY(), worldViewPort.getScreenWidth(), worldViewPort.getScreenHeight());
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Gdx.gl.glClearColor(0,1f,1f,1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT); //


        // Draw a background for the clipped 16/9 area

        batch.setProjectionMatrix(ONE_MATRIX);
        batch.begin();
        drawBackground();
        batch.end();


        worldCamera.update();
        //worldCamera.update();
        mapCamera.update();
        hudCamera.update();

        physicsMatrix.set(worldCamera.combined);
        physicsMatrix.scale(PhysicsConstants.PIXELS_PER_METER, PhysicsConstants.PIXELS_PER_METER, 1);

        worldCamera.zoom = 2f;
        mapCamera.zoom = 50f;


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
    public void resize(int width, int height) {
        worldViewPort.update(width, height);
        mapViewPort.update(
                (int) ((float) worldViewPort.getScreenWidth() * mapFractionOfScreen),
                (int) ((float) worldViewPort.getScreenHeight() * mapFractionOfScreen)
        );
        hudViewPort.update(width, height);

        mapViewPort.setScreenPosition(
                worldViewPort.getScreenWidth()-mapViewPort.getScreenWidth()+worldViewPort.getLeftGutterWidth(),
                worldViewPort.getScreenHeight()-mapViewPort.getScreenHeight()+worldViewPort.getTopGutterHeight()
        );

    }

}
