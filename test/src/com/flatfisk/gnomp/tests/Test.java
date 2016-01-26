package com.flatfisk.gnomp.tests;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.gdx.DefaultGnompEngineApplicationListener;
import com.flatfisk.gnomp.systems.PhysicsSystem;
import com.flatfisk.gnomp.systems.RenderSystem;

public class Test extends DefaultGnompEngineApplicationListener {

    public void createSystems(Vector2 gravity){
        World physicsWorld = new World(gravity,true);

        addScenegraphSystem(0);
        initializeConstructorManager(100, physicsWorld);
        RenderSystem renderSystem = addRenderSystem(50);
        renderSystem.setProcessing(true);
        addPhysicsTrackerSystem(400);
        addDebugRenderer(500,physicsWorld,renderSystem,false);
        PhysicsSystem physicsSystem = addPhysicsSystem(600, physicsWorld);
        physicsSystem.setFixedStep(true);
    }

}
