package com.flatfisk.gnomp.gdx;

import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.constructors.SpatialConstructor;
import com.flatfisk.gnomp.constructors.PhysicsConstructor;
import com.flatfisk.gnomp.constructors.RenderableConstructor;
import com.flatfisk.gnomp.constructors.StructureConstructor;
import com.flatfisk.gnomp.ConstructorManager;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.systems.*;


public class DefaultGnompEngineApplicationListener extends GnompEngineApplicationListener{

    protected ShapeTextureFactory shapeTextureFactory;

    @Override
    public void create(){
        super.create();
    }

    protected void createScenegraphSystem(int priority){
        ScenegraphSystem scenegraphSystem = new ScenegraphSystem(priority);
        world.addSystem(scenegraphSystem);
    }

    protected void createConstructorManager(int priority,World physicsWorld){
        ConstructorManager constructorManager = world.getConstructorManager();//;new ConstructorManager(world);
        constructorManager.addConstructor(new SpatialConstructor(world),0);
        constructorManager.addConstructor(new StructureConstructor(world),1);
        constructorManager.addConstructor(new PhysicsConstructor(world,physicsWorld),2);
        constructorManager.addConstructor(new RenderableConstructor(world,shapeTextureFactory),3);
        //world.addEntityListener(constructorManager.rootFamily,priority,constructorManager);
    }

    protected RenderSystem createRenderSystem(int priority){
        RenderSystem renderer = new RenderSystem(priority);
        //world.addEntityListener(renderer.getFamily(),priority,renderer);
        world.addSystem(renderer);
        return renderer;
    }

    protected void createDebugRenderer(int priority, World physicsWorld, RenderSystem renderer, boolean clear){
        PhysicsDebugRenderer debugRenderer = new PhysicsDebugRenderer(renderer.getCamera(),physicsWorld,priority);
        debugRenderer.setClearScreen(clear);
        world.addSystem(debugRenderer);
    }

    protected void createPhysicsSystem(int priority,World physicsWorld){
        PhysicsSystem physicsSystem = new PhysicsSystem(physicsWorld,priority);
        world.addEntityListener(physicsSystem.getFamily(),priority,physicsSystem);
        world.addSystem(physicsSystem);
    }

    protected void createPhysicsTrackerSystem(int priority){
        PositionPhysicsScenegraphSystem physicsSystem = new PositionPhysicsScenegraphSystem(priority);
        world.addSystem(physicsSystem);
    }
}