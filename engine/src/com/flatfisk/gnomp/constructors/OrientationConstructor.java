package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.roots.SpatialDef;
import com.flatfisk.gnomp.math.Spatial;

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
        constructorOrientation.worldSpatial.setCopy(rootOrientation.localSpatial);
        return rootOrientation;
    }

    @Override
    public SpatialRelative insertedChild(Entity entity,
                                     SpatialRelative rootOrientation,
                                     SpatialRelative constructorOrientation,
                                     SpatialRelative parentOrientation,
                                     SpatialRelative childOrientation,
                                     SpatialRelative constructorDTO) {

        Spatial parentWorld = parentOrientation.worldSpatial;
        Spatial childLocal = childOrientation.localSpatial;
        Spatial childWorld = childOrientation.worldSpatial;

        boolean transferAngle = childOrientation.inheritFromParentType.equals(SpatialRelative.SpatialInheritType.POSITION_ANGLE);

        childWorld.setCopy(parentWorld.vector, transferAngle ? parentWorld.rotation : 0);
        childWorld.addRotated(childLocal.vector,childWorld.rotation);

        return constructorOrientation;
    }

}
