package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Constructable;
import com.flatfisk.gnomp.components.Structure;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.utils.Pools;


/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class StructureConstructor extends Constructor<Structure,Structure.Node, TextureCoordinates> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public StructureConstructor(GnompEngine engine) {
        super(engine,Structure.class,Structure.Node.class);
    }

    @Override
    public void parentAddedFinal(Entity entity,Constructable.Node constructorOrientation,TextureCoordinates textureCoordinates){
        Structure.Node structure = relationshipMapper.get(entity);
        if(textureCoordinates!=null) {
            structure.boundingRectangle = textureCoordinates.getBoundingRectangle();
        }
    }

    @Override
    public TextureCoordinates parentAdded(Entity entity, Constructable.Node structureOrientation) {
        Structure.Node structure = relationshipMapper.get(entity);

        Spatial spatial = Pools.obtainSpatial();
        TextureCoordinates textureCoordinates = null;

        if (structure.shape != null) {
            textureCoordinates = structure.shape.getTextureCoordinates(null,spatial);
        }

        return textureCoordinates;
    }

    @Override
    public TextureCoordinates insertedChild(Entity entity, Constructable.Node constructorOrientation, Constructable.Node parentOrientation, Constructable.Node childOrientation, TextureCoordinates textureCoordinates) {
        Structure.Node structure = relationshipMapper.get(entity);
        Spatial spatial = childOrientation.world.subtractedCopy(constructorOrientation.world);

        if(structure.shape !=null){
            textureCoordinates = structure.shape.getTextureCoordinates(textureCoordinates, spatial);
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
