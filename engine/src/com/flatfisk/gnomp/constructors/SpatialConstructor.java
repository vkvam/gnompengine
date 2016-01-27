package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class SpatialConstructor extends com.flatfisk.gnomp.constructors.Constructor {
    public SpatialConstructor(GnompEngine engine) {
        super(engine, com.flatfisk.gnomp.components.Constructor.class, com.flatfisk.gnomp.components.Constructor.Node.class);
    }

    @Override
    public com.flatfisk.gnomp.components.Constructor.Node parentAdded(Entity entity,
                                       com.flatfisk.gnomp.components.Constructor.Node constructorOrientation) {
        //constructorOrientation.world.setCopy(rootOrientation.local);
        constructorOrientation.local.setCopy(constructorOrientation.world);
        return constructorOrientation;
    }

    @Override
    public com.flatfisk.gnomp.components.Constructor.Node insertedChild(Entity entity,
                                         com.flatfisk.gnomp.components.Constructor.Node constructorOrientation,
                                         com.flatfisk.gnomp.components.Constructor.Node parentOrientation,
                                         com.flatfisk.gnomp.components.Constructor.Node childOrientation,
                                         com.flatfisk.gnomp.components.Constructor.Node constructorDTO) {

        com.flatfisk.gnomp.math.Spatial parentWorld = parentOrientation.world;
        com.flatfisk.gnomp.math.Spatial childLocal = childOrientation.local;
        com.flatfisk.gnomp.math.Spatial childWorld = childOrientation.world;

        boolean transferAngle = childOrientation.inheritFromParentType.equals(com.flatfisk.gnomp.components.Constructor.Node.SpatialInheritType.POSITION_ANGLE);

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
