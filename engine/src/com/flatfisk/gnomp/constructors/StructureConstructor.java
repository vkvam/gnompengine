package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Constructable;
import com.flatfisk.gnomp.components.Structure;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;


/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class StructureConstructor extends Constructor<Structure,Structure.Node, Structure.Node> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public StructureConstructor(GnompEngine engine) {
        super(engine,Structure.class,Structure.Node.class);
    }

    @Override
    public Structure.Node parentAdded(Entity entity, Constructable.Node structureOrientation) {
        Structure.Node structure = relationshipMapper.get(entity);

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
    public Structure.Node insertedChild(Entity entity, Constructable.Node constructorOrientation, Constructable.Node parentOrientation, Constructable.Node childOrientation, Structure.Node constructorDTO) {
        Structure.Node structure = relationshipMapper.get(entity);

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
