package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Structure;
import com.flatfisk.gnomp.components.Renderable;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.shape.texture.ShapeTexture;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class RenderableConstructor extends Constructor<Renderable,Renderable.Node,ShapeTexture> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ShapeTextureFactory shapeTextureFactory;
    private ComponentMapper<Structure.Node> structureRelativeComponentMapper;

    public RenderableConstructor(GnompEngine engine,ShapeTextureFactory shapeTextureFactory) {
        super(engine,Renderable.class,Renderable.Node.class);
        structureRelativeComponentMapper = ComponentMapper.getFor(Structure.Node.class);
        this.shapeTextureFactory = shapeTextureFactory;
    }


    @Override
    public void parentAddedFinal(Entity entity, com.flatfisk.gnomp.components.Constructor.Node constructorOrientation, ShapeTexture shapeTexture) {

        Renderable renderableDef = constructorMapper.get(entity);

        Renderable.Constructed renderable = engine.addComponent(Renderable.Constructed.class, entity);
        renderable.texture = shapeTexture.createTexture();
        renderable.offset = shapeTexture.getOffset();
        renderable.zIndex = renderableDef.zIndex;

        LOG.info("Inserting parent final:"+ renderable);
    }

    @Override
    public ShapeTexture parentAdded(Entity entity, com.flatfisk.gnomp.components.Constructor.Node constructorOrientation) {
        Structure.Node structure = structureRelativeComponentMapper.get(entity);
        ShapeTexture px = shapeTextureFactory.createShapeTexture(structure.boundingRectangle);

        // If the constructor has a shape, the shape should be drawn at origin.
        Spatial spatial = Pools.obtainSpatial();
        if (structure.shape != null) {
            px.draw(structure, spatial);
        }

        LOG.info("Inserting parent at vector:"+ spatial.vector);

        return px;
    }

    @Override
    public ShapeTexture insertedChild(Entity entity,
                                      com.flatfisk.gnomp.components.Constructor.Node constructorOrientation,
                                      com.flatfisk.gnomp.components.Constructor.Node parentOrientation,
                                      com.flatfisk.gnomp.components.Constructor.Node childOrientation,
                                      ShapeTexture constructorDTO) {

        Structure.Node structure = structureRelativeComponentMapper.get(entity);

        // Use vector relativeType to constructor.
        Spatial spatial = childOrientation.world.subtractedCopy(constructorOrientation.world);
        LOG.info("Inserting child at vector:"+ spatial.vector);

        if (structure.shape != null) {
            constructorDTO.draw(structure, spatial);
        }
        return constructorDTO;
    }

    @Override
    public void parentRemoved(Entity entity) {
        entity.remove(Renderable.Constructed.class);
    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
