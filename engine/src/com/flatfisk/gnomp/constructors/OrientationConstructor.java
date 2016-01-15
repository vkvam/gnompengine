package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.roots.SpatialDef;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class OrientationConstructor extends Constructor<SpatialDef,SpatialRelative,SpatialRelative> {
    public OrientationConstructor(PooledEngine engine) {
        super(engine, SpatialDef.class, SpatialRelative.class);
    }

    @Override
    public SpatialRelative parentAdded(Entity entity,
                                   SpatialRelative rootOrientation,
                                   SpatialRelative constructorOrientation) {
        constructorOrientation.world.setCopy(rootOrientation.local);
        return rootOrientation;
    }

    @Override
    public SpatialRelative insertedChild(Entity entity,
                                     SpatialRelative rootOrientation,
                                     SpatialRelative constructorOrientation,
                                     SpatialRelative parentOrientation,
                                     SpatialRelative childOrientation,
                                     SpatialRelative constructorDTO) {

        Spatial parentWorld = parentOrientation.world;
        Spatial childLocal = childOrientation.local;
        Spatial childWorld = childOrientation.world;

        boolean transferAngle = childOrientation.inheritFromParentType.equals(SpatialRelative.SpatialInheritType.POSITION_ANGLE);

        childWorld.set(Pools.obtainVector2FromCopy(parentWorld.vector),transferAngle?parentWorld.rotation:0);
        childWorld.vector.add(Pools.obtainVector2FromCopy(childLocal.vector).rotate(childWorld.rotation));
        childWorld.rotation += childLocal.rotation;

        return constructorOrientation;
    }

}
