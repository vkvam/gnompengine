package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Geometry;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTexture;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class RenderableConstructor extends Constructor<Renderable,Renderable.Node,ShapeTexture> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ShapeTextureFactory shapeTextureFactory;
    private ComponentMapper<Geometry.Node> structureRelativeComponentMapper;

    public RenderableConstructor(GnompEngine engine,ShapeTextureFactory shapeTextureFactory) {
        super(engine,Renderable.class,Renderable.Node.class);
        structureRelativeComponentMapper = ComponentMapper.getFor(Geometry.Node.class);
        this.shapeTextureFactory = shapeTextureFactory;
    }


    @Override
    public void parentAddedFinal(Entity entity, Spatial.Node constructorOrientation, ShapeTexture shapeTexture) {

        Renderable renderableDef = constructorMapper.get(entity);

        Renderable.Constructed renderable = engine.addComponent(Renderable.Constructed.class, entity);
        renderable.texture = shapeTexture.createTexture();
        renderable.offset = shapeTexture.getOffset();
        renderable.zIndex = renderableDef.zIndex;

        LOG.info("Inserting parent final:"+ renderable);
    }

    @Override
    public ShapeTexture parentAdded(Entity entity, Spatial.Node constructorOrientation) {
        Geometry.Node structure = structureRelativeComponentMapper.get(entity);
        ShapeTexture px = shapeTextureFactory.createShapeTexture(structure.boundingRectangle);

        // If the constructor has a shape, the shape should be drawn at origin.
        Transform transform = Pools.obtainSpatial();
        if (structure.shape != null) {
            px.draw(structure, transform);
        }

        LOG.info("Inserting parent at vector:"+ transform.vector);

        return px;
    }

    @Override
    public ShapeTexture insertedChild(Entity entity,
                                      Spatial.Node constructorOrientation,
                                      Spatial.Node parentOrientation,
                                      Spatial.Node childOrientation,
                                      ShapeTexture constructorDTO) {

        Geometry.Node geometry = structureRelativeComponentMapper.get(entity);


        // Use vector relativeType to constructor.
        Transform transform = childOrientation.world.subtractedCopy(constructorOrientation.world);
        LOG.info("Inserting child at vector:"+ transform.vector);

        if(!geometry.intermediate && !relationshipMapper.get(entity).intermediate && geometry.shape != null) {
            constructorDTO.draw(geometry, transform);
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
