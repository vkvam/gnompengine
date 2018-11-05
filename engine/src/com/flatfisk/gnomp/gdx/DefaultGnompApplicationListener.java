package com.flatfisk.gnomp.gdx;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.flatfisk.gnomp.engine.ConstructorManager;
import com.flatfisk.gnomp.engine.constructors.*;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.systems.*;


public class DefaultGnompApplicationListener extends GnompApplicationListener{

    protected ShapeTextureFactory shapeTextureFactory;

    @Override
    public void create(){
        super.create();
    }

    @Override
    public void dispose(){
        super.dispose();
        shapeTextureFactory.dispose();
    }

    protected ScenegraphSystem addScenegraphSystem(int priority){
        ScenegraphSystem scenegraphSystem = new ScenegraphSystem(priority);
        engine.addSystem(scenegraphSystem);
        return scenegraphSystem;
    }

    protected void initializeConstructorManager(World physicsWorld, RayHandler rayHandler){
        ConstructorManager constructorManager = engine.getConstructorManager();

        constructorManager.addConstructor(new SpatialConstructor(),10);
        constructorManager.addConstructor(new BoundsConstructor(),20);
        constructorManager.addConstructor(new PhysicsConstructor(engine,physicsWorld),30);
        constructorManager.addConstructor(new PhysicsSteerableConstructor(engine),35);
        constructorManager.addConstructor(new LightConstructor(rayHandler, engine),40);
        constructorManager.addConstructor(new RenderableConstructor(engine,shapeTextureFactory),50);
        constructorManager.addConstructor(new EffectConstructor(engine),60);
    }

    protected CameraSystem addCameraSystem(int priority, int width, int height){
        CameraSystem cameraSystem = new CameraSystem(priority, width, height, 1f/4f);
        engine.addSystem(cameraSystem);
        return cameraSystem;
    }

    protected RenderSystem addRenderSystem(int priority,Camera cameraSystem){
        RenderSystem renderer = new RenderSystem(priority, cameraSystem);
        engine.addSystem(renderer);
        return renderer;
    }



    protected PhysicsDebugRenderer addDebugRenderer(int priority, World physicsWorld, CameraSystem cameraSystem){
        PhysicsDebugRenderer debugRenderer = new PhysicsDebugRenderer(physicsWorld,priority,cameraSystem);
        engine.addSystem(debugRenderer);
        return debugRenderer;
    }

    protected PhysicsSystem addPhysicsSystem(int priority,World physicsWorld){
        PhysicsSystem physicsSystem = new PhysicsSystem(physicsWorld,priority);
        engine.addSystem(physicsSystem);
        return physicsSystem;
    }

    protected PhysicsScenegraphSystem addPhysicsTrackerSystem(int priority){
        PhysicsScenegraphSystem physicsSystem = new PhysicsScenegraphSystem(priority);
        engine.addSystem(physicsSystem);
        return physicsSystem;
    }
}