package com.flatfisk.amalthea.factories;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.flatfisk.amalthea.systems.LabelSystem2;
import com.flatfisk.amalthea.systems.LightFlickerSystem;
import com.flatfisk.gnomp.engine.components.SoundComponent;
import com.flatfisk.gnomp.engine.systems.LifeTimeSystem;
import com.flatfisk.amalthea.systems.MapRenderSystem;
import com.flatfisk.gnomp.engine.systems.*;
import com.flatfisk.gnomp.gdx.DefaultGnompApplicationListener;

public class SystemContructors extends DefaultGnompApplicationListener{

    public void createSystems(Vector2 gravity, boolean physicsDebug, boolean showStats) {
        World physicsWorld = new World(gravity, true);

        RayHandler rayHandler = new RayHandler(physicsWorld);
        initializeConstructorManager(physicsWorld, rayHandler);

        addScenegraphSystem(100);
        CameraSystem cameraSystem = addCameraSystem(200, 1280, 720);
        cameraSystem.getWorldCamera().zoom = 0.140f;

        RenderSystem renderSystem = addRenderSystem(300, cameraSystem.getWorldCamera());

        addPhysicsTrackerSystem(400);
        PhysicsSystem physicsSystem = addPhysicsSystem(600, physicsWorld);

        if (physicsDebug) {
            addDebugRenderer(500, physicsWorld,cameraSystem);
        }

        rayHandler.setAmbientLight(0.65f,0.55f,0.5f, 0.1f);

        LightSystem lightSystem = new LightSystem(700, rayHandler, cameraSystem);
        engine.addSystem(lightSystem);

        EffectSystem effectSystem = new EffectSystem(800, cameraSystem);
        engine.addSystem(effectSystem);

        renderSystem.setProcessing(true);
        physicsSystem.setFixedStep(1f / 60f);

        if (showStats) {
            StatsSystem statsSystem = new StatsSystem(10000, cameraSystem);
            engine.addSystem(statsSystem);
            physicsSystem.setStatsSystem(statsSystem);
            renderSystem.setStatsSystem(statsSystem);
            effectSystem.setStatsSystem(statsSystem);
            //lightSystem.setStatsSystem(statsSystem);

            engine.addSystem(new LabelSystem(10000, cameraSystem, 2));
        }

        MapRenderSystem mapRenderSystem = addMapRenderSystem(10001, cameraSystem);

        engine.addSystem(
                new LabelSystem2(10002, cameraSystem, 20)
        );

        engine.addSystem(new LifeTimeSystem(100002));

        LightFlickerSystem flicker = new LightFlickerSystem(10003);
        engine.addSystem(flicker);
    }

    private MapRenderSystem addMapRenderSystem(int priority, CameraSystem cameraSystem) {
        MapRenderSystem renderer = new MapRenderSystem(priority, cameraSystem);
        engine.addSystem(renderer);
        return renderer;
    }


}
