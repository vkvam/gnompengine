package com.flatfisk.gnomp.gdx;

import box2dLight.RayHandler;
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.engine.ConstructorManager;
import com.flatfisk.gnomp.engine.constructors.*;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.systems.*;


public class DefaultGnompApplicationListener extends GnompApplicationListener {

    protected ShapeTextureFactory shapeTextureFactory;

    @Override
    public void create(){
        super.create();
    }

    protected ScenegraphSystem addScenegraphSystem(int priority){
        ScenegraphSystem scenegraphSystem = new ScenegraphSystem(priority);
        world.addSystem(scenegraphSystem);
        return scenegraphSystem;
    }

    protected void initializeConstructorManager(World physicsWorld, RayHandler rayHandler){
        ConstructorManager constructorManager = world.getConstructorManager();

        constructorManager.addConstructor(new SpatialConstructor(),0);
        constructorManager.addConstructor(new BoundsConstructor(),1);
        constructorManager.addConstructor(new PhysicsConstructor(world,physicsWorld),2);
        constructorManager.addConstructor(new LightConstructor(rayHandler,world),3);
        constructorManager.addConstructor(new RenderableConstructor(world,shapeTextureFactory),4);
    }

    protected CameraSystem addCameraSystem(int priority){
        CameraSystem cameraSystem = new CameraSystem(priority, 640, 480);
        world.addSystem(cameraSystem);
        return cameraSystem;
    }

    protected RenderSystem addRenderSystem(int priority){
        RenderSystem renderer = new RenderSystem(priority);
        world.addSystem(renderer);
        return renderer;
    }

    protected RenderFinalizeSystem addRenderFinalizeSystem(int priority){
        RenderFinalizeSystem renderFinalizeSystem = new RenderFinalizeSystem(300);
        world.addSystem(renderFinalizeSystem);
        return renderFinalizeSystem;
    }

    protected PhysicsDebugRenderer addDebugRenderer(int priority, World physicsWorld, CameraSystem cameraSystem){
        PhysicsDebugRenderer debugRenderer = new PhysicsDebugRenderer(cameraSystem.getCamera(),physicsWorld,priority);

        world.addSystem(debugRenderer);
        return debugRenderer;
    }

    protected PhysicsSystem addPhysicsSystem(int priority,World physicsWorld){
        PhysicsSystem physicsSystem = new PhysicsSystem(physicsWorld,priority);
        world.addEntityListener(physicsSystem.getFamily(),priority,physicsSystem);
        world.addSystem(physicsSystem);
        return physicsSystem;
    }

    protected PhysicsScenegraphSystem addPhysicsTrackerSystem(int priority){
        PhysicsScenegraphSystem physicsSystem = new PhysicsScenegraphSystem(priority);
        world.addSystem(physicsSystem);
        return physicsSystem;
    }
}