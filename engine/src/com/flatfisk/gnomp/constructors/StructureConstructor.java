package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.components.roots.StructureDef;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;


/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class StructureConstructor extends Constructor<StructureDef,StructureRelative, StructureRelative> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public StructureConstructor(GnompEngine engine) {
        super(engine,StructureDef.class,StructureRelative.class);
    }

    @Override
    public StructureRelative parentAdded(Entity entity, SpatialRelative structureOrientation) {
        StructureRelative structure = relationshipMapper.get(entity);

        // If the constructor has a shape, the shape should be drawn at origin.
        Spatial spatial = Pools.obtainSpatial();

        if (structure.shape != null) {
            structure.textureCoordinates = structure.shape.getTextureCoordinates(null, spatial);
        }

        LOG.info("Inserting parent at vector:"+ spatial.vector);

        structure.boundingRectangle = structure.textureCoordinates.getBoundingRectangle();
        return structure;
    }

    @Override
    public StructureRelative insertedChild(Entity entity, SpatialRelative constructorOrientation, SpatialRelative parentOrientation, SpatialRelative childOrientation, StructureRelative constructorDTO) {
        StructureRelative structure = relationshipMapper.get(entity);

        // Use vector relativeType to constructor.
        Spatial spatial = childOrientation.world.subtractedCopy(constructorOrientation.world);
        LOG.info("Inserting child at vector:"+ spatial.vector);

        if(structure.shape !=null){
            constructorDTO.textureCoordinates = structure.shape.getTextureCoordinates(constructorDTO.textureCoordinates, spatial);
        }

        constructorDTO.boundingRectangle = constructorDTO.textureCoordinates.getBoundingRectangle();

        return constructorDTO;
    }

    @Override
    public void parentRemoved(Entity entity) {

    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
