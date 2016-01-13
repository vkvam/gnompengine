package com.flatfisk.gnomp.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.RenderConstruction;
import com.flatfisk.gnomp.components.Renderable;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.components.StructureNode;
import com.flatfisk.gnomp.shape.texture.ShapeTexture;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;


public class RenderableConstructionSystem extends NodeSystem<ShapeTexture, StructureNode> {

    public ComponentMapper<RenderConstruction> renderableStructureComponentMapper;
    public ShapeTextureFactory shapeTextureFactory;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public RenderableConstructionSystem(ShapeTextureFactory shapeTextureFactory, int priority) {
        super(Family.all(Root.class,/*StructureRootComponent.class, ,*/RenderConstruction.class).get(), StructureNode.class, priority);
        this.shapeTextureFactory = shapeTextureFactory;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        renderableStructureComponentMapper = ComponentMapper.getFor(RenderConstruction.class);
    }

    @Override
    protected ShapeTexture insertedRoot(Entity e) {
        StructureNode structure = nodeMapper.get(e);
        LOG.info("Structure retrieved:"+structure);
        ShapeTexture px = shapeTextureFactory.createShapeTexture(structure.boundingRectangle);
        if (structure.shape != null) {
            px.draw(structure);
        }
        return px;
    }

    @Override
    protected ShapeTexture insertedChild(Entity e, ShapeTexture dto) {
        // Check that entity has a CRenderableStructure component
        if (renderableStructureComponentMapper.has(e)) {
            StructureNode structure = nodeMapper.get(e);
            if (structure.shape != null) {
                dto.draw(structure);
            }
        }
        return dto;
    }

    @Override
    protected void finalInsertedRoot(Entity e, ShapeTexture dto) {
        Renderable renderable = ((PooledEngine) getEngine()).createComponent(Renderable.class);
        renderable.texture = dto.createTexture();
        renderable.offset = dto.getOffset();
        e.add(renderable);
    }

    @Override
    protected void removedParent(Entity e) {
        //removeSystemComponents(e);
    }

    @Override
    protected void removedChild(Entity e) {
        //removeSystemComponents(e);
    }

}
