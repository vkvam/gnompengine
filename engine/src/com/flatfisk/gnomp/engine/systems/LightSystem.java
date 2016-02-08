package com.flatfisk.gnomp.engine.systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.Matrix4;
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
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    RayHandler rayHandler;
    Matrix4 debugMatrix= new Matrix4();

    ComponentMapper<Light.Container> lightMapper;
    ComponentMapper<Spatial.Node> spatialMapper;


    public LightSystem(int priority, RayHandler rayHandler) {
        super(Family.all(Light.Container.class).get(),priority);
        this.rayHandler = rayHandler;
        this.lightMapper = ComponentMapper.getFor(Light.Container.class);
        this.spatialMapper = ComponentMapper.getFor(Spatial.Node.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        CameraSystem system = getEngine().getSystem(CameraSystem.class);
        debugMatrix.set(system.getCamera().combined);
        debugMatrix.scale(PhysicsConstants.PIXELS_PER_METER, PhysicsConstants.PIXELS_PER_METER, 1);

        rayHandler.setCombinedMatrix(debugMatrix);
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
        CameraSystem system = getEngine().getSystem(CameraSystem.class);
        Rectangle rect = system.viewport;
        rayHandler.useCustomViewport((int)rect.x,(int)rect.y,(int)rect.width,(int)rect.height);
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
