package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class SpatialConstructor extends Constructor<Spatial,Spatial.Node,Component, Spatial.Node> {
    public SpatialConstructor() {
        super(Spatial.class, Spatial.Node.class, null);
    }

    @Override
    public Spatial.Node parentAdded(Entity entity,
                                       Spatial.Node constructorOrientation) {
        constructorOrientation.local.set(constructorOrientation.world);
        return constructorOrientation;
    }

    @Override
    public Spatial.Node insertedChild(Entity entity,
                                      Spatial.Node constructorOrientation,
                                      Spatial.Node parentOrientation,
                                      Spatial.Node childOrientation,
                                      Spatial.Node constructorDTO) {

        Transform parentWorld = parentOrientation.world;
        childOrientation.addParentWorld(parentWorld);
        return constructorOrientation;
    }


    @Override
    public void parentRemoved(Entity entity) {

    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
