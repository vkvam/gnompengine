package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.Transform;



/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class BoundsConstructor extends Constructor<Renderable,Renderable.Node, Component, TextureCoordinates> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<Shape> structureNodeComponentMapper;

    public BoundsConstructor() {
        super(Renderable.class,Renderable.Node.class, null);
        structureNodeComponentMapper = ComponentMapper.getFor(Shape.class);
    }

    @Override
    public void parentAddedFinal(Entity entity,Spatial.Node constructorOrientation,TextureCoordinates textureCoordinates){
        Renderable renderable = constructorMapper.get(entity);
        if (textureCoordinates != null) {
            renderable.boundingRectangle = textureCoordinates.getBoundingRectangle();
        }
    }

    @Override
    public TextureCoordinates parentAdded(Entity entity, Spatial.Node structureOrientation) {
        Shape structure = structureNodeComponentMapper.get(entity);
        Renderable.Node renderableNode = relationshipMapper.get(entity);

        Transform transform = Pools.obtain(Transform.class);
        TextureCoordinates textureCoordinates = null;

        if (structure.geometry != null && renderableNode != null && !renderableNode.intermediate) {
            textureCoordinates = structure.geometry.getTextureCoordinates(null, transform);
        }

        Pools.free(transform);

        return textureCoordinates;
    }

    @Override
    public TextureCoordinates insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, TextureCoordinates textureCoordinates) {
        Shape shape = structureNodeComponentMapper.get(entity);
        Renderable.Node renderableNode = relationshipMapper.get(entity);

        Transform transform = Pools.obtain(Transform.class).set(childOrientation.world).subtract(constructorOrientation.world);

        if(shape.geometry !=null &&! renderableNode.intermediate){
            textureCoordinates = shape.geometry.getTextureCoordinates(textureCoordinates, transform);
        }
        Pools.free(transform);
        return textureCoordinates;
    }

    @Override
    public void parentRemoved(Entity entity) {

    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
