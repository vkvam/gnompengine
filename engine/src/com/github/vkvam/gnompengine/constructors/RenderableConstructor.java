package com.github.vkvam.gnompengine.constructors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Logger;
import com.github.vkvam.gnompengine.components.constructed.Renderable;
import com.github.vkvam.gnompengine.components.relatives.OrientationRelative;
import com.github.vkvam.gnompengine.components.relatives.RenderableRelative;
import com.github.vkvam.gnompengine.components.relatives.StructureRelative;
import com.github.vkvam.gnompengine.components.roots.RenderableDef;
import com.github.vkvam.gnompengine.math.Translation;
import com.github.vkvam.gnompengine.shape.texture.ShapeTexture;
import com.github.vkvam.gnompengine.shape.texture.ShapeTextureFactory;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class RenderableConstructor extends Constructor<RenderableDef,RenderableRelative,ShapeTexture> {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ShapeTextureFactory shapeTextureFactory;
    private ComponentMapper<StructureRelative> structureRelativeComponentMapper;

    public RenderableConstructor(PooledEngine engine,ShapeTextureFactory shapeTextureFactory) {
        super(engine,RenderableDef.class,RenderableRelative.class);
        structureRelativeComponentMapper = ComponentMapper.getFor(StructureRelative.class);
        this.shapeTextureFactory = shapeTextureFactory;
    }


    @Override
    public void parentAddedFinal(Entity entity, ShapeTexture shapeTexture) {

        RenderableDef renderableDef = constructorMapper.get(entity);

        Renderable renderable = engine.createComponent(Renderable.class);
        renderable.texture = shapeTexture.createTexture();
        renderable.offset = shapeTexture.getOffset();
        renderable.zIndex = renderableDef.zIndex;
        entity.add(renderable);
    }

    @Override
    public ShapeTexture parentAdded(Entity entity, OrientationRelative rootOrientation, OrientationRelative constructorOrientation) {
        StructureRelative structure = structureRelativeComponentMapper.get(entity);
        ShapeTexture px = shapeTextureFactory.createShapeTexture(structure.boundingRectangle);

        // If the constructor has a shape, the shape should be drawn at origin.
        Translation translation = new Translation();
        if (structure.shape != null) {
            px.draw(structure,translation);
        }

        LOG.info("Inserting parent at position:"+translation.position);

        return px;
    }

    @Override
    public ShapeTexture insertedChild(Entity entity,
                                      OrientationRelative rootOrientation,
                                      OrientationRelative constructorOrientation,
                                      OrientationRelative parentOrientation,
                                      OrientationRelative childOrientation,
                                      ShapeTexture constructorDTO) {

        StructureRelative structure = structureRelativeComponentMapper.get(entity);

        // Use position relativeType to constructor.
        Translation translation = childOrientation.worldTranslation.subtractCopy(constructorOrientation.worldTranslation);
        LOG.info("Inserting child at position:"+translation.position);

        if (structure.shape != null) {
            constructorDTO.draw(structure,translation);
        }
        return constructorDTO;
    }
}
