package com.flatfisk.gnomp.tests;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.engine.systems.*;
import com.flatfisk.gnomp.gdx.DefaultGnompApplicationListener;

public class Test extends DefaultGnompApplicationListener {

    public void createSystems(Vector2 gravity, boolean physicsDebug, boolean showStats){
        World physicsWorld = new World(gravity,true);

        RayHandler rayHandler = new RayHandler(physicsWorld);
        initializeConstructorManager(physicsWorld,rayHandler);

        addScenegraphSystem(100);
        CameraSystem cameraSystem = addCameraSystem(200, 640, 480);
        cameraSystem.getWorldCamera().zoom = 14f;
        RenderSystem renderSystem = addRenderSystem(300,cameraSystem.getWorldCamera());

        addPhysicsTrackerSystem(400);
        PhysicsSystem physicsSystem = addPhysicsSystem(600, physicsWorld);

        if(physicsDebug) {
            // addDebugRenderer(500, physicsWorld,cameraSystem);
        }

        //LightSystem lightSystem = new LightSystem(700,rayHandler, cameraSystem);
        //engine.addSystem(lightSystem);

        EffectSystem effectSystem = new EffectSystem(800,cameraSystem);
        engine.addSystem(effectSystem);

        renderSystem.setProcessing(true);
        physicsSystem.setFixedStep(1f/60f);

        if(showStats){
            StatsSystem statsSystem = new StatsSystem(10000, cameraSystem);
            engine.addSystem(statsSystem);
            physicsSystem.setStatsSystem(statsSystem);
            renderSystem.setStatsSystem(statsSystem);
            effectSystem.setStatsSystem(statsSystem);
            //lightSystem.setStatsSystem(statsSystem);
        }
    }

}
