package com.flatfisk.gnomp.engine;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Spatial;

/**
 * Abstract class managing the construction of components from other components.
 *
 * @param <CONSTRUCTOR_ROOT> Component type that triggers construction of a new component
 * @param <RELATIONSHIP> Component type for a child contributing to the component
 * @param <CONSTRUCTED> Component type being constructed
 * @param <CONSTRUCTION_DTO> Type of object used to carry data through parentAdded and insertedChild calls during construction.
 */
public abstract class Constructor<CONSTRUCTOR_ROOT extends Component, RELATIONSHIP extends Component, CONSTRUCTED extends Component, CONSTRUCTION_DTO>{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    Class<CONSTRUCTOR_ROOT> constructor;
    private Class<RELATIONSHIP> relationship;
    Class<CONSTRUCTED> constructed;

    protected ComponentMapper<CONSTRUCTOR_ROOT> constructorMapper;
    protected ComponentMapper<RELATIONSHIP> relationshipMapper;
    private ComponentMapper<CONSTRUCTED> constructedMapper;


    public Constructor(Class<CONSTRUCTOR_ROOT> constructor, Class<RELATIONSHIP> relationship, Class<CONSTRUCTED> constructed) {
        this.constructor = constructor;
        this.relationship = relationship;
        this.constructed = constructed;

        constructorMapper = ComponentMapper.getFor(this.constructor);
        relationshipMapper = ComponentMapper.getFor(this.relationship);
        constructedMapper = constructed==null?null:ComponentMapper.getFor(this.constructed);
    }

    boolean hasConstructor(Entity entity){
        return constructed == null || constructorMapper.has(entity);
    }

    boolean isParent(Entity entity){
        return constructorMapper.has(entity);
    }

    boolean isChild(Entity entity){
        return relationshipMapper.has(entity)&&!constructorMapper.has(entity);
    }


    public abstract CONSTRUCTION_DTO parentAdded(Entity entity, Spatial.Node constructorOrientation);
    public CONSTRUCTION_DTO insertedChild(Entity entity, Spatial.Node constructorOrientation, Spatial.Node parentOrientation, Spatial.Node childOrientation, CONSTRUCTION_DTO constructorDTO){
        return null;
    }
    public void parentAddedFinal(Entity entity, Spatial.Node constructorOrientation, CONSTRUCTION_DTO construction_dto){}

    public abstract void parentRemoved(Entity entity);
    public abstract void childRemoved(Entity entity);
}
