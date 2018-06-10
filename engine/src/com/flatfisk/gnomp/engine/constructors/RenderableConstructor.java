package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTexture;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.math.Transform;

import static com.flatfisk.gnomp.engine.GnompMappers.shapeMap;


public class RenderableConstructor extends Constructor<Renderable,Renderable.Node,Renderable.Container, ShapeTexture> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ShapeTextureFactory shapeTextureFactory;

    private GnompEngine engine;

    public RenderableConstructor(GnompEngine engine,ShapeTextureFactory shapeTextureFactory) {
        super(Renderable.class,Renderable.Node.class, Renderable.Container.class);
        this.engine = engine;
        this.shapeTextureFactory = shapeTextureFactory;
    }


    @Override
    public void parentAddedFinal(Entity entity, Spatial.Node constructorOrientation, ShapeTexture shapeTexture) {
        Renderable renderableDef = constructorMapper.get(entity);
        Renderable.Container renderable = engine.addComponent(Renderable.Container.class, entity);
        renderable.texture = shapeTexture.createTexture();
        renderable.offset = shapeTexture.getOffset();
        renderable.zIndex = renderableDef.zIndex;
    }

    @Override
    public ShapeTexture parentAdded(Entity entity, Spatial.Node constructorOrientation) {
        Renderable geometry = constructorMapper.get(entity);
        ShapeTexture px = shapeTextureFactory.createShapeTexture(geometry.boundingRectangle);
        Shape shape = shapeMap.get(entity);
        // If the constructor has a geometry, the geometry should be drawn at origin.
        Transform transform = Pools.obtain(Transform.class);
        if (shape.getGeometry() != null && !relationshipMapper.get(entity).intermediate) {
            px.draw(shape, transform);
        }
        Pools.free(transform);
        return px;
    }

    @Override
    public ShapeTexture insertedChild(Entity entity,
                                      Spatial.Node constructorOrientation,
                                      Spatial.Node parentOrientation,
                                      Spatial.Node childOrientation,
                                      ShapeTexture constructorDTO) {

        Shape shape = shapeMap.get(entity);

        Transform transform = Pools.obtain(Transform.class).set(childOrientation.world).subtract(constructorOrientation.world);
        transform.vector.rotate(-constructorOrientation.world.rotation);

        if(shape.getGeometry() != null && !relationshipMapper.get(entity).intermediate) {
            constructorDTO.draw(shape, transform);
        }

        Pools.free(transform);
        return constructorDTO;
    }

    @Override
    public void parentRemoved(Entity entity) {
        entity.remove(Renderable.Container.class);
    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
