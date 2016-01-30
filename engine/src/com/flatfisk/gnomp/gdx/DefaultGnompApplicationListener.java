package com.flatfisk.gnomp.gdx;

import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.engine.constructors.BoundsConstructor;
import com.flatfisk.gnomp.engine.constructors.SpatialConstructor;
import com.flatfisk.gnomp.engine.constructors.PhysicsConstructor;
import com.flatfisk.gnomp.engine.constructors.RenderableConstructor;
import com.flatfisk.gnomp.engine.ConstructorManager;
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

    protected void initializeConstructorManager(World physicsWorld){
        ConstructorManager constructorManager = world.getConstructorManager();//;new ConstructorManager(world);
        constructorManager.addConstructor(new SpatialConstructor(),0);
        constructorManager.addConstructor(new BoundsConstructor(),1);
        constructorManager.addConstructor(new PhysicsConstructor(world,physicsWorld),2);
        constructorManager.addConstructor(new RenderableConstructor(world,shapeTextureFactory),3);
    }

    protected CameraSystem addCameraSystem(int priority){
        CameraSystem cameraSystem = new CameraSystem(priority);
        world.addSystem(cameraSystem);
        return cameraSystem;
    }

    protected RenderSystem addRenderSystem(int priority){
        RenderSystem renderer = new RenderSystem(priority);
        world.addSystem(renderer);
        return renderer;
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

    protected PositionPhysicsScenegraphSystem addPhysicsTrackerSystem(int priority){
        PositionPhysicsScenegraphSystem physicsSystem = new PositionPhysicsScenegraphSystem(priority);
        world.addSystem(physicsSystem);
        return physicsSystem;
    }
}