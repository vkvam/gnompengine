package com.flatfisk.gnomp.gdx;

<<<<<<< HEAD
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.constructors.OrientationConstructor;
import com.flatfisk.gnomp.constructors.PhysicsConstructor;
import com.flatfisk.gnomp.constructors.RenderableConstructor;
import com.flatfisk.gnomp.constructors.StructureConstructor;
import com.flatfisk.gnomp.entitymanagers.ConstructorManager;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.systems.*;
=======
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.systems.RenderableConstructionSystem;
import com.flatfisk.gnomp.systems.RenderSystem;
import com.flatfisk.gnomp.systems.ScenegraphSystem;
import com.flatfisk.gnomp.systems.StructureSystem;
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a


public class DefaultGnompEngineApplicationListener extends GnompEngineApplicationListener{

    protected ShapeTextureFactory shapeTextureFactory;
<<<<<<< HEAD

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
=======
    protected StructureSystem structureSystem;
    protected RenderableConstructionSystem renderableCompiler;
    protected ScenegraphSystem scenegraph;
    protected RenderSystem renderer;

    @Override
    public void create() {
        super.create();

        structureSystem = new StructureSystem(0);
        world.addSystem(structureSystem);
        world.addEntityListener(0, structureSystem);

        renderableCompiler = new RenderableConstructionSystem(shapeTextureFactory,100);
        world.addSystem(renderableCompiler);
        world.addEntityListener(100, renderableCompiler);

        scenegraph = new ScenegraphSystem(200);
        world.addSystem(scenegraph);
        world.addEntityListener(200, scenegraph);

        renderer = new RenderSystem(1000);
        world.addSystem(renderer);
        world.addEntityListener(1000, renderer);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }
}