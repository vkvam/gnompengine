package com.flatfisk.gnomp.tests;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.LightSystem;
import com.flatfisk.gnomp.engine.systems.PhysicsSystem;
import com.flatfisk.gnomp.engine.systems.RenderSystem;
import com.flatfisk.gnomp.gdx.DefaultGnompApplicationListener;

public class Test extends DefaultGnompApplicationListener {

    public void createSystems(Vector2 gravity, boolean physicsDebug){
        World physicsWorld = new World(gravity,true);

        RayHandler rayHandler = new RayHandler(physicsWorld);
        initializeConstructorManager(physicsWorld,rayHandler);

        addScenegraphSystem(5);
        CameraSystem cameraSystem = addCameraSystem(100);
        RenderSystem renderSystem = addRenderSystem(200);
        addRenderFinalizeSystem(300);


        addPhysicsTrackerSystem(400);
        if(physicsDebug) {
            addDebugRenderer(500, physicsWorld, cameraSystem);
        }
        PhysicsSystem physicsSystem = addPhysicsSystem(600, physicsWorld);

        LightSystem lightSystem = new LightSystem(650,rayHandler);
        world.addSystem(lightSystem);

        renderSystem.setProcessing(true);
        physicsSystem.setFixedStep(false);
    }

}
