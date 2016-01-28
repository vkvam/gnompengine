package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.components.Spatial;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class SpatialConstructor extends Constructor<Spatial,Spatial.Node,Spatial.Node> {
    public SpatialConstructor(GnompEngine engine) {
        super(engine, Spatial.class, Spatial.Node.class);
    }


    @Override
    public Spatial.Node parentAdded(Entity entity,
                                       Spatial.Node constructorOrientation) {
        constructorOrientation.local.setCopy(constructorOrientation.world);
        return constructorOrientation;
    }


    @Override
    public Spatial.Node insertedChild(Entity entity,
                                         Spatial.Node constructorOrientation,
                                         Spatial.Node parentOrientation,
                                         Spatial.Node childOrientation,
                                         Spatial.Node constructorDTO) {

        Transform parentWorld = parentOrientation.world;
        Transform childLocal = childOrientation.local;
        Transform childWorld = childOrientation.world;

        boolean transferAngle = childOrientation.inheritFromParentType.equals(Spatial.Node.SpatialInheritType.POSITION_ANGLE);

        childWorld.set(Pools.obtainVector2FromCopy(parentWorld.vector),transferAngle?parentWorld.rotation:0);
        childWorld.vector.add(Pools.obtainVector2FromCopy(childLocal.vector).rotate(childWorld.rotation));
        childWorld.rotation += childLocal.rotation;

        return constructorOrientation;
    }

    @Override
    public void parentRemoved(Entity entity) {

    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
