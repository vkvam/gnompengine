package com.flatfisk.gnomp.engine.systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.Light;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;


/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class LightSystem extends IteratingSystem implements ApplicationListener {
    private CameraSystem cameraSystem;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    RayHandler rayHandler;

    ComponentMapper<Light.Container> lightMapper;
    ComponentMapper<Spatial.Node> spatialMapper;


    public LightSystem(int priority, RayHandler rayHandler, CameraSystem cameraSystem) {
        super(Family.all(Light.Container.class).get(),priority);
        this.rayHandler = rayHandler;
        this.cameraSystem = cameraSystem;
        this.lightMapper = ComponentMapper.getFor(Light.Container.class);
        this.spatialMapper = ComponentMapper.getFor(Spatial.Node.class);
    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        rayHandler.setCombinedMatrix(cameraSystem.getPhysicsMatrix(),
                cameraSystem.viewport.x,
                cameraSystem.viewport.y,
                cameraSystem.viewport.width*PhysicsConstants.PIXELS_PER_METER,
                cameraSystem.viewport.height*PhysicsConstants.PIXELS_PER_METER);
        rayHandler.updateAndRender();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Light.Container container = lightMapper.get(entity);
        box2dLight.Light light = container.light;

        Transform lightOffset = container.offset;

        Transform worldTransform = container.worldTransform.set(spatialMapper.get(entity).world);
        Vector2 worldRotatedOffset = container.worldRotatedOffset.set(lightOffset.vector).rotate(worldTransform.rotation);
        worldTransform.add(worldRotatedOffset, lightOffset.rotation);

        light.setDirection(worldTransform.rotation);
        light.setPosition(worldTransform.vector.scl(PhysicsConstants.METERS_PER_PIXEL));
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {
        Rectangle viewport= cameraSystem.viewport;
        rayHandler.useCustomViewport((int)viewport.x,(int)viewport.y,(int)viewport.width,(int)viewport.height);
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
}
