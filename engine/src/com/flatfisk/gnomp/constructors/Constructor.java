package com.flatfisk.gnomp.constructors;

/**
 * Created by Vemund Kvam on 06/12/15.
 */

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Spatial;

public abstract class Constructor<CONSTRUCTOR_ROOT extends Component, RELATIONSHIP extends Component/*IRelative*/, CONSTRUCTION_DTO>{
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
        return constructorMapper.has(entity);
    }

    public boolean isChild(Entity entity){
        return !constructorMapper.has(entity);
    }

    /**
     * Entity added with constructorComponent for constructor
     * @param entity added
     * @param constructorOrientation
     * @return
     */
    public abstract CONSTRUCTION_DTO parentAdded(Entity entity, Spatial.Node constructorOrientation);
    public abstract CONSTRUCTION_DTO insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, CONSTRUCTION_DTO constructorDTO);
    public void parentAddedFinal(Entity entity, Spatial.Node constructorOrientation, CONSTRUCTION_DTO construction_dto){};

    public abstract void parentRemoved(Entity entity);
    public abstract void childRemoved(Entity entity);
}
