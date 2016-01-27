package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.components.Constructable;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class SpatialConstructor extends Constructor<Constructable,Constructable.Node,Constructable.Node> {
    public SpatialConstructor(GnompEngine engine) {
        super(engine, Constructable.class, Constructable.Node.class);
    }

    @Override
    public Constructable.Node parentAdded(Entity entity,
                                       Constructable.Node constructorOrientation) {
        //constructorOrientation.world.setCopy(rootOrientation.local);
        constructorOrientation.local.setCopy(constructorOrientation.world);
        return constructorOrientation;
    }


    @Override
    public Constructable.Node insertedChild(Entity entity,
                                         Constructable.Node constructorOrientation,
                                         Constructable.Node parentOrientation,
                                         Constructable.Node childOrientation,
                                         Constructable.Node constructorDTO) {

        com.flatfisk.gnomp.math.Spatial parentWorld = parentOrientation.world;
        com.flatfisk.gnomp.math.Spatial childLocal = childOrientation.local;
        com.flatfisk.gnomp.math.Spatial childWorld = childOrientation.world;

        boolean transferAngle = childOrientation.inheritFromParentType.equals(Constructable.Node.SpatialInheritType.POSITION_ANGLE);

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
