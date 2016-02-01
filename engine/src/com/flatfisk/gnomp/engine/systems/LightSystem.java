package com.flatfisk.gnomp.engine.systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.Light;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;

;

/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class LightSystem extends IteratingSystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    RayHandler rayHandler;
    Matrix4 debugMatrix;

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

        debugMatrix = new Matrix4(getEngine().getSystem(CameraSystem.class).getCamera().combined);
        debugMatrix.scale(PhysicsConstants.PIXELS_PER_METER, PhysicsConstants.PIXELS_PER_METER, 1);

        rayHandler.setCombinedMatrix(debugMatrix);
        rayHandler.updateAndRender();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Light.Container container = lightMapper.get(entity);
        box2dLight.Light light = container.light;

        // TODO: reuse more
        Transform parentTransform = spatialMapper.get(entity).world.getCopy();
        parentTransform.vector.add(com.flatfisk.gnomp.utils.Pools.obtainVector2FromCopy(container.offset.vector).rotate(parentTransform.rotation));
        parentTransform.rotation+=container.offset.rotation;

        // Lights use angle instead of radians for rotation,...
        light.setDirection(parentTransform.rotation);
        parentTransform.toBox2D();
        // but position is in box2d units.
        light.setPosition(parentTransform.vector);
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {
        rayHandler.useCustomViewport(0,0,width,height);
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
