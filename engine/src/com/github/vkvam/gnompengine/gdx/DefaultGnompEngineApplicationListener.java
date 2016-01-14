package com.github.vkvam.gnompengine.gdx;

import com.badlogic.gdx.physics.box2d.World;
import com.github.vkvam.gnompengine.constructors.OrientationConstructor;
import com.github.vkvam.gnompengine.constructors.PhysicsConstructor;
import com.github.vkvam.gnompengine.constructors.RenderableConstructor;
import com.github.vkvam.gnompengine.constructors.StructureConstructor;
import com.github.vkvam.gnompengine.entitymanagers.ConstructorManager;
import com.github.vkvam.gnompengine.shape.texture.ShapeTextureFactory;
import com.github.vkvam.gnompengine.systems.*;


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

    protected void createConstructorManager(int priority){
        ConstructorManager constructorManager = new ConstructorManager();
        constructorManager.addConstructor(new OrientationConstructor(world),0);
        constructorManager.addConstructor(new StructureConstructor(world),1);
        constructorManager.addConstructor(new PhysicsConstructor(world),2);
        constructorManager.addConstructor(new RenderableConstructor(world,shapeTextureFactory),3);
        world.addEntityListener(constructorManager.rootFamily,priority,constructorManager);
    }

    protected RenderSystem createRenderSystem(int priority){
        RenderSystem renderer = new RenderSystem(priority);
        world.addEntityListener(renderer.getFamily(),priority,renderer);
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
        PhysicsTrackerSystem physicsSystem = new PhysicsTrackerSystem(priority);
        world.addSystem(physicsSystem);
    }
}