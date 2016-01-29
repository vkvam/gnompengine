package com.flatfisk.gnomp.tests;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.PhysicsDebugRenderer;
import com.flatfisk.gnomp.engine.systems.PhysicsSystem;
import com.flatfisk.gnomp.engine.systems.RenderSystem;
import com.flatfisk.gnomp.gdx.DefaultGnompApplicationListener;

public class Test extends DefaultGnompApplicationListener {

    public void createSystems(Vector2 gravity){
        World physicsWorld = new World(gravity,true);

        // Runs before all systems
        initializeConstructorManager(physicsWorld);

        CameraSystem cameraSystem = addCameraSystem(0);
        addScenegraphSystem(25);
        RenderSystem renderSystem = addRenderSystem(50);
        addPhysicsTrackerSystem(400);
        PhysicsDebugRenderer debugRenderer = addDebugRenderer(500,physicsWorld,cameraSystem);
        PhysicsSystem physicsSystem = addPhysicsSystem(600, physicsWorld);

        debugRenderer.setProcessing(false);
        renderSystem.setProcessing(true);
        physicsSystem.setFixedStep(false);
    }

}
