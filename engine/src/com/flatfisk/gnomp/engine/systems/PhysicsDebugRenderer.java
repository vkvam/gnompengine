package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;

public class PhysicsDebugRenderer extends EntitySystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private Box2DDebugRenderer debugRenderer;
    private Camera camera;
    private World box2DWorld;
    private Matrix4 debugMatrix;
    private boolean clearScreen;

    public PhysicsDebugRenderer(Camera camera, World box2DWorld, int priority) {
        this.priority = priority;
        this.camera = camera;
        this.box2DWorld = box2DWorld;
    }

    public void setClearScreen(boolean clearScreen) {
        this.clearScreen = clearScreen;
    }

    private void rescale() {
        debugMatrix = new Matrix4(camera.combined);
        debugMatrix.scale(PhysicsConstants.PIXELS_PER_METER, PhysicsConstants.PIXELS_PER_METER, 1);
    }

    @Override
    public void addedToEngine(Engine engine) {
        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.setDrawVelocities(true);
        debugRenderer.setDrawBodies(true);
        debugRenderer.setDrawAABBs(true);
        rescale();
    }

    @Override
    public void update(float f) {
        rescale();
        if (clearScreen) {
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        debugRenderer.render(box2DWorld, debugMatrix);
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {
        if (clearScreen) {
            camera.viewportWidth = width;
            camera.viewportHeight = height;
            Gdx.graphics.setDisplayMode(width, height, false);
            camera.update();
        }
        rescale();
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
        debugRenderer.dispose();
    }
}
