package com.flatfisk.gnomp.engine;

/**
 * Created by Vemund Kvam on 06/12/15.
 */

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Spatial;

public abstract class Constructor<CONSTRUCTOR_ROOT extends Component, RELATIONSHIP extends Component, CONSTRUCTED extends Component, CONSTRUCTION_DTO>{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);
    public Class<CONSTRUCTOR_ROOT> constructor;
    public Class<RELATIONSHIP> relationship;
    public Class<CONSTRUCTED> constructed;

    public ComponentMapper<CONSTRUCTOR_ROOT> constructorMapper;
    public ComponentMapper<RELATIONSHIP> relationshipMapper;
    public ComponentMapper<CONSTRUCTED> constructedMapper;

    //public GnompEngine engine;

    public Constructor(Class<CONSTRUCTOR_ROOT> constructor, Class<RELATIONSHIP> relationship, Class<CONSTRUCTED> constructed) {
        this.constructor = constructor;
        this.relationship = relationship;
        this.constructed = constructed;

        constructorMapper = ComponentMapper.getFor(this.constructor);
        relationshipMapper = ComponentMapper.getFor(this.relationship);
        constructedMapper = constructed==null?null:ComponentMapper.getFor(this.constructed);
    }

    public boolean hasConstructor(Entity entity){
        return constructed==null?true:constructorMapper.has(entity);
    }

    public boolean isParent(Entity entity){
        return constructorMapper.has(entity);
    }

    public boolean isChild(Entity entity){
        return relationshipMapper.has(entity)&&!constructorMapper.has(entity);
    }

    /**
     * Entity added with constructorComponent for constructor
     * @param entity added
     * @param constructorOrientation
     * @return
     */
    public abstract CONSTRUCTION_DTO parentAdded(Entity entity, Spatial.Node constructorOrientation);
    public CONSTRUCTION_DTO insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, CONSTRUCTION_DTO constructorDTO){
        return null;
    }
    public void parentAddedFinal(Entity entity, Spatial.Node constructorOrientation, CONSTRUCTION_DTO construction_dto){}

    public abstract void parentRemoved(Entity entity);
    public abstract void childRemoved(Entity entity);
}
