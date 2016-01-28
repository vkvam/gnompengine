package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Geometry;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;


/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class StructureConstructor extends Constructor<Geometry,Geometry.Node, TextureCoordinates> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public StructureConstructor() {
        super(Geometry.class,Geometry.Node.class);
    }

    @Override
    public void parentAddedFinal(Entity entity,Spatial.Node constructorOrientation,TextureCoordinates textureCoordinates){
        Geometry.Node structure = relationshipMapper.get(entity);
        if(textureCoordinates!=null) {
            structure.boundingRectangle = textureCoordinates.getBoundingRectangle();
        }
    }

    @Override
    public TextureCoordinates parentAdded(Entity entity, Spatial.Node structureOrientation) {
        Geometry.Node structure = relationshipMapper.get(entity);

        Transform transform = Pools.obtainSpatial();
        TextureCoordinates textureCoordinates = null;

        if (structure.shape != null) {
            textureCoordinates = structure.shape.getTextureCoordinates(null, transform);
        }

        return textureCoordinates;
    }

    @Override
    public TextureCoordinates insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, TextureCoordinates textureCoordinates) {
        Geometry.Node structure = relationshipMapper.get(entity);
        Transform transform = childOrientation.world.subtractedCopy(constructorOrientation.world);

        if(structure.shape !=null && !structure.intermediate){
            textureCoordinates = structure.shape.getTextureCoordinates(textureCoordinates, transform);
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
