package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.shape.AbstractShape;

/**
 * A geometric shape, used to construct both fixtures and textures.
 */
public class Shape<SHAPETYPE extends AbstractShape> implements ISerializable<Shape> {
    public SHAPETYPE geometry;

    public Shape(){
    }

    @Override
    public void reset() {
        if(geometry !=null) {
            geometry.reset();
            geometry =null;
        }

    }

    public Shape addCopy(GnompEngine gnompEngine,Entity entity){
        Shape relative = gnompEngine.addComponent(getClass(),entity);

        relative.geometry = geometry.getCopy();
        relative.geometry.fillColor = geometry.fillColor.cpy();
        relative.geometry.lineColor = geometry.lineColor.cpy();
        relative.geometry.lineWidth = geometry.lineWidth;

        return relative;
    }
}
