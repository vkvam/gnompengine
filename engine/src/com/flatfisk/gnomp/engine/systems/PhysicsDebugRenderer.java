package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;

public class PhysicsDebugRenderer extends EntitySystem implements ApplicationListener {
    private CameraSystem cameraSystem;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private Box2DDebugRenderer debugRenderer;
    private World box2DWorld;

    public PhysicsDebugRenderer(World box2DWorld, int priority, CameraSystem cameraSystem) {
        this.priority = priority;
        this.box2DWorld = box2DWorld;
        this.cameraSystem = cameraSystem;
    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    @Override
    public void addedToEngine(Engine engine) {
        debugRenderer = new Box2DDebugRenderer();

        debugRenderer.setDrawVelocities(true);
        debugRenderer.setDrawContacts(true);
        debugRenderer.setDrawBodies(true);
        debugRenderer.setDrawAABBs(true);
    }

    @Override
    public void update(float f) {
        debugRenderer.render(box2DWorld, cameraSystem.getPhysicsMatrix());
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {
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
