package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Geometry;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;


/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class BoundsConstructor extends Constructor<Renderable,Renderable.Node, TextureCoordinates> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ComponentMapper<Geometry> structureNodeComponentMapper;

    public BoundsConstructor() {
        super(Renderable.class,Renderable.Node.class);
        structureNodeComponentMapper = ComponentMapper.getFor(Geometry.class);
    }

    @Override
    public void parentAddedFinal(Entity entity,Spatial.Node constructorOrientation,TextureCoordinates textureCoordinates){
        Renderable renderable = constructorMapper.get(entity);
        if(textureCoordinates!=null) {
            renderable.boundingRectangle = textureCoordinates.getBoundingRectangle();
        }
    }

    @Override
    public TextureCoordinates parentAdded(Entity entity, Spatial.Node structureOrientation) {
        Geometry structure = structureNodeComponentMapper.get(entity);
        Renderable.Node renderableNode = relationshipMapper.get(entity);

        Transform transform = Pools.obtainSpatial();
        TextureCoordinates textureCoordinates = null;

        if (structure.shape != null && renderableNode!=null && !renderableNode.intermediate) {
            textureCoordinates = structure.shape.getTextureCoordinates(null, transform);
        }

        return textureCoordinates;
    }

    @Override
    public TextureCoordinates insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, TextureCoordinates textureCoordinates) {
        Geometry geometry = structureNodeComponentMapper.get(entity);
        Renderable.Node renderableNode = relationshipMapper.get(entity);

        Transform transform = childOrientation.world.subtractedCopy(constructorOrientation.world);

        if(geometry.shape !=null &&! renderableNode.intermediate){
            textureCoordinates = geometry.shape.getTextureCoordinates(textureCoordinates, transform);
        }

        return textureCoordinates;
    }

    @Override
    public void parentRemoved(Entity entity) {

    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
