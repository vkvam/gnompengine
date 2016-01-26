package com.flatfisk.gnomp.tests;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.gdx.DefaultGnompEngineApplicationListener;
import com.flatfisk.gnomp.systems.RenderSystem;

public class Test extends DefaultGnompEngineApplicationListener {

    public void createSystems(Vector2 gravity){
        World physicsWorld = new World(gravity,true);

        createScenegraphSystem(0);
        createConstructorManager(100,physicsWorld);
        RenderSystem renderSystem = createRenderSystem(50);
        renderSystem.setProcessing(true);
        createPhysicsTrackerSystem(400);
        //createDebugRenderer(500,physicsWorld,renderSystem,false);
        createPhysicsSystem(600,physicsWorld);
    }

}
