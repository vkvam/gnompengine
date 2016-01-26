package com.flatfisk.gnomp.constructors;

/**
 * Created by Vemund Kvam on 06/12/15.
 */

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.RelativeComponent;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.badlogic.ashley.core.GnompEngine;

public abstract class Constructor<CONSTRUCTOR_ROOT extends Component, RELATIONSHIP extends RelativeComponent, CONSTRUCTION_DTO>{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public Class<CONSTRUCTOR_ROOT> constructor;
    public Class<RELATIONSHIP> relationship;

    public ComponentMapper<CONSTRUCTOR_ROOT> constructorMapper;
    public ComponentMapper<RELATIONSHIP> relationshipMapper;

    public GnompEngine engine;

    public Constructor(GnompEngine engine, Class<CONSTRUCTOR_ROOT> constructor, Class<RELATIONSHIP> relationship) {
        this.constructor = constructor;
        this.relationship = relationship;

        constructorMapper = ComponentMapper.getFor(this.constructor);
        relationshipMapper = ComponentMapper.getFor(this.relationship);
        this.engine = engine;
    }

    public boolean isParent(Entity entity){
        return constructorMapper.has(entity) && relationshipMapper.get(entity).getRelativeType()== Relative.PARENT;
    }

    public boolean isChild(Entity entity){
        RELATIONSHIP relationship = relationshipMapper.get(entity);
        return relationship!=null && (relationship.getRelativeType() == Relative.CHILD || relationship.getRelativeType() == Relative.INTERMEDIATE);
    }

    /**
     * Entity added with constructorComponent for constructor
     * @param entity added
     * @param constructorOrientation
     * @return
     */
    public abstract CONSTRUCTION_DTO parentAdded(Entity entity, SpatialRelative constructorOrientation);
    public abstract CONSTRUCTION_DTO insertedChild(Entity entity, SpatialRelative constructorOrientation, SpatialRelative parentOrientation, SpatialRelative childOrientation, CONSTRUCTION_DTO constructorDTO);
    public void parentAddedFinal(Entity entity, CONSTRUCTION_DTO construction_dto){};

    public abstract void parentRemoved(Entity entity);
    public abstract void childRemoved(Entity entity);
}
