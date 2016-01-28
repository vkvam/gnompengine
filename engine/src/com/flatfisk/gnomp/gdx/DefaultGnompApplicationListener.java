package com.flatfisk.gnomp.gdx;

import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.engine.constructors.SpatialConstructor;
import com.flatfisk.gnomp.engine.constructors.PhysicsConstructor;
import com.flatfisk.gnomp.engine.constructors.RenderableConstructor;
import com.flatfisk.gnomp.engine.constructors.StructureConstructor;
import com.flatfisk.gnomp.engine.ConstructorManager;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.systems.*;


public class DefaultGnompApplicationListener extends GnompApplicationListener {

    protected ShapeTextureFactory shapeTextureFactory;

    @Override
    public void create(){
        super.create();
    }

    protected void addScenegraphSystem(int priority){
        ScenegraphSystem scenegraphSystem = new ScenegraphSystem(priority);
        world.addSystem(scenegraphSystem);
    }

    protected void initializeConstructorManager(int priority, World physicsWorld){
        ConstructorManager constructorManager = world.getConstructorManager();//;new ConstructorManager(world);
        constructorManager.addConstructor(new SpatialConstructor(world),0);
        constructorManager.addConstructor(new StructureConstructor(world),1);
        constructorManager.addConstructor(new PhysicsConstructor(world,physicsWorld),2);
        constructorManager.addConstructor(new RenderableConstructor(world,shapeTextureFactory),3);
        //world.addEntityListener(constructorManager.rootFamily,priority,constructorManager);
    }

    protected RenderSystem addRenderSystem(int priority){
        RenderSystem renderer = new RenderSystem(priority);
        //world.addEntityListener(renderer.getFamily(),priority,renderer);
        world.addSystem(renderer);
        return renderer;
    }

    protected void addDebugRenderer(int priority, World physicsWorld, RenderSystem renderer, boolean clear){
        PhysicsDebugRenderer debugRenderer = new PhysicsDebugRenderer(renderer.getCamera(),physicsWorld,priority);
        debugRenderer.setClearScreen(clear);
        world.addSystem(debugRenderer);
    }

    protected PhysicsSystem addPhysicsSystem(int priority,World physicsWorld){
        PhysicsSystem physicsSystem = new PhysicsSystem(physicsWorld,priority);
        world.addEntityListener(physicsSystem.getFamily(),priority,physicsSystem);
        world.addSystem(physicsSystem);
        return physicsSystem;
    }

    protected void addPhysicsTrackerSystem(int priority){
        PositionPhysicsScenegraphSystem physicsSystem = new PositionPhysicsScenegraphSystem(priority);
        world.addSystem(physicsSystem);
    }
}