package com.github.vkvam.gnompengine.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Logger;
import com.github.vkvam.gnompengine.components.relatives.OrientationRelative;
import com.github.vkvam.gnompengine.components.relatives.StructureRelative;
import com.github.vkvam.gnompengine.components.roots.StructureDef;
import com.github.vkvam.gnompengine.math.Translation;


/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class StructureConstructor extends Constructor<StructureDef,StructureRelative, StructureRelative> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public StructureConstructor(PooledEngine engine) {
        super(engine,StructureDef.class,StructureRelative.class);
    }

    @Override
    public StructureRelative parentAdded(Entity entity, OrientationRelative rootOrientation, OrientationRelative structureOrientation) {
        StructureRelative structure = relationshipMapper.get(entity);

        // If the constructor has a shape, the shape should be drawn at origin.
        Translation translation = new Translation();

        if (structure.shape != null) {
            structure.textureCoordinates = structure.shape.getTextureCoordinates(null, translation);
        }

        LOG.info("Inserting parent at position:"+translation.position);

        structure.boundingRectangle = structure.textureCoordinates.getBoundingRectangle();
        return structure;
    }

    @Override
    public StructureRelative insertedChild(Entity entity, OrientationRelative rootOrientation, OrientationRelative constructorOrientation, OrientationRelative parentOrientation, OrientationRelative childOrientation, StructureRelative constructorDTO) {
        StructureRelative structure = relationshipMapper.get(entity);

        // Use position relativeType to constructor.
        Translation translation = childOrientation.worldTranslation.subtractCopy(constructorOrientation.worldTranslation);
        LOG.info("Inserting child at position:"+translation.position);

        if(structure.shape !=null){
            constructorDTO.textureCoordinates = structure.shape.getTextureCoordinates(constructorDTO.textureCoordinates,translation);
        }

        constructorDTO.boundingRectangle = constructorDTO.textureCoordinates.getBoundingRectangle();

        return constructorDTO;
    }


}
