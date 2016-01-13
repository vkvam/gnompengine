package com.flatfisk.gnomp.gdx;

import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.systems.RenderableConstructionSystem;
import com.flatfisk.gnomp.systems.RenderSystem;
import com.flatfisk.gnomp.systems.ScenegraphSystem;
import com.flatfisk.gnomp.systems.StructureSystem;


public class DefaultGnompEngineApplicationListener extends GnompEngineApplicationListener{

    protected ShapeTextureFactory shapeTextureFactory;
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
    }
}