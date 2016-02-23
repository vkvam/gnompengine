package com.flatfisk.gnomp.engine.systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.Light;
import com.flatfisk.gnomp.math.Transform;

import static com.flatfisk.gnomp.engine.GnompMappers.lightMap;
import static com.flatfisk.gnomp.engine.GnompMappers.spatialNodeMap;

/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class LightSystem extends IteratingSystem implements ApplicationListener {
    private CameraSystem cameraSystem;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private RayHandler rayHandler;


    private StatsSystem statsSystem;
    private int lightsDrawn = 0;


    public LightSystem(int priority, RayHandler rayHandler, CameraSystem cameraSystem) {
        super(Family.all(Light.Container.class).get(),priority);
        this.rayHandler = rayHandler;
        this.cameraSystem = cameraSystem;
    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    public void setStatsSystem(StatsSystem statsSystem){
        this.statsSystem = statsSystem;
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
        if(statsSystem!=null){
            statsSystem.addStat("Lights");
            statsSystem.addStat("Processed:"+lightsDrawn);
            statsSystem.addLine();
        }
        lightsDrawn=0;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Light.Container container = lightMap.get(entity);
        box2dLight.Light light = container.light;

        Transform worldTransform = spatialNodeMap.get(entity).world;
        light.setDirection(worldTransform.rotation);
        light.setPosition(
                worldTransform.vector.x * PhysicsConstants.METERS_PER_PIXEL,
                worldTransform.vector.y * PhysicsConstants.METERS_PER_PIXEL
        );
        lightsDrawn++;
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
