package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.shape.Shape;

/**
 * Geometry is a container for a shape
 */
public class Geometry implements ISerializable<Geometry> {
    public Shape shape;

    public Geometry(){
    }

    @Override
    public void reset() {
        if(shape!=null) {
            shape.reset();
            shape=null;
        }

    }

    public Geometry addCopy(GnompEngine gnompEngine,Entity entity){
        Geometry relative = gnompEngine.addComponent(getClass(),entity);

        relative.shape = shape.getCopy();
        relative.shape.fillColor = shape.fillColor.cpy();
        relative.shape.lineColor = shape.lineColor.cpy();
        relative.shape.lineWidth = shape.lineWidth;

        return relative;
    }
}
