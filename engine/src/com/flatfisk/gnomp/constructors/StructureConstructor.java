package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.components.roots.StructureDef;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;


/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class StructureConstructor extends Constructor<StructureDef,StructureRelative, StructureRelative> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public StructureConstructor(PooledEngine engine) {
        super(engine,StructureDef.class,StructureRelative.class);
    }

    @Override
    public StructureRelative parentAdded(Entity entity, SpatialRelative rootOrientation, SpatialRelative structureOrientation) {
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
    public StructureRelative insertedChild(Entity entity, SpatialRelative rootOrientation, SpatialRelative constructorOrientation, SpatialRelative parentOrientation, SpatialRelative childOrientation, StructureRelative constructorDTO) {
        StructureRelative structure = relationshipMapper.get(entity);

        // Use vector relativeType to constructor.
        Spatial spatial = childOrientation.worldSpatial.subtractedCopy(constructorOrientation.worldSpatial);
        LOG.info("Inserting child at vector:"+ spatial.vector);

        if(structure.shape !=null){
            constructorDTO.textureCoordinates = structure.shape.getTextureCoordinates(constructorDTO.textureCoordinates, spatial);
        }

        constructorDTO.boundingRectangle = constructorDTO.textureCoordinates.getBoundingRectangle();

        return constructorDTO;
    }


}
