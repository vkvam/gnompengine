package com.flatfisk.gnomp.constructors;

/**
 * Created by Vemund Kvam on 06/12/15.
 */

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.RelativeComponent;
import com.flatfisk.gnomp.components.relatives.OrientationRelative;

public abstract class Constructor<CONSTRUCTOR_ROOT extends Component, RELATIONSHIP extends RelativeComponent, CONSTRUCTION_DTO>{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public Class<CONSTRUCTOR_ROOT> constructor;
    public Class<RELATIONSHIP> relationship;

    public ComponentMapper<CONSTRUCTOR_ROOT> constructorMapper;
    public ComponentMapper<RELATIONSHIP> relationshipMapper;

    public PooledEngine engine;

    public Constructor(PooledEngine engine, Class<CONSTRUCTOR_ROOT> constructor, Class<RELATIONSHIP> relationship) {
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
        return relationship!=null&&relationship.getRelativeType() == Relative.CHILD;
    }

    public void parentAddedFinal(Entity entity, CONSTRUCTION_DTO construction_dto){};
    public abstract CONSTRUCTION_DTO parentAdded(Entity entity, OrientationRelative rootOrientation, OrientationRelative constructorOrientation);
    public abstract CONSTRUCTION_DTO insertedChild(Entity entity, OrientationRelative rootOrientation, OrientationRelative constructorOrientation, OrientationRelative parentOrientation, OrientationRelative childOrientation, CONSTRUCTION_DTO constructorDTO);
}
