package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class SpatialConstructor extends Constructor<Spatial,Spatial.Node,Spatial.Node> {
    public SpatialConstructor() {
        super(Spatial.class, Spatial.Node.class);
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
        Transform childLocal = childOrientation.local;
        Transform childWorld = childOrientation.world;

        boolean transferAngle = childOrientation.inheritFromParentType.equals(Spatial.Node.SpatialInheritType.POSITION_ANGLE);

        childWorld.set(parentWorld.vector, transferAngle ? parentWorld.rotation : 0);

        Vector2 localVectorWorldRotated = Pools.obtain(Vector2.class);

        localVectorWorldRotated.set(childLocal.vector);
        localVectorWorldRotated.rotate(childWorld.rotation);
        childWorld.vector.add(localVectorWorldRotated);
        childWorld.rotation += childLocal.rotation;

        Pools.free(localVectorWorldRotated);

        return constructorOrientation;
    }

    @Override
    public void parentRemoved(Entity entity) {

    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
