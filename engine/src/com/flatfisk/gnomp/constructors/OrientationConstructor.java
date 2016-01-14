package com.flatfisk.gnomp.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.flatfisk.gnomp.utils.Pools;
import com.flatfisk.gnomp.components.relatives.OrientationRelative;
import com.flatfisk.gnomp.components.roots.OrientationDef;
import com.flatfisk.gnomp.math.Translation;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class OrientationConstructor extends Constructor<OrientationDef,OrientationRelative,OrientationRelative> {
    public OrientationConstructor(PooledEngine engine) {
        super(engine, OrientationDef.class, OrientationRelative.class);
    }

    @Override
    public OrientationRelative parentAdded(Entity entity,
                                   OrientationRelative rootOrientation,
                                   OrientationRelative constructorOrientation) {
        constructorOrientation.worldTranslation.setCopy(rootOrientation.localTranslation);
        return rootOrientation;
    }

    @Override
    public OrientationRelative insertedChild(Entity entity,
                                     OrientationRelative rootOrientation,
                                     OrientationRelative constructorOrientation,
                                     OrientationRelative parentOrientation,
                                     OrientationRelative childOrientation,
                                     OrientationRelative constructorDTO) {

        Translation parentWorldTranslation = parentOrientation.worldTranslation;
        Translation childLocalTranslation = childOrientation.localTranslation;
        Translation childWorldTranslation = childOrientation.worldTranslation;

        boolean transferAngle = childOrientation.inheritFromParentType.equals(OrientationRelative.TranslationInheritType.POSITION_ANGLE);

        childWorldTranslation.set(Pools.obtainFromCopy(parentWorldTranslation.position),transferAngle?parentWorldTranslation.angle:0);
        childWorldTranslation.position.add(Pools.obtainFromCopy(childLocalTranslation.position).rotate(childWorldTranslation.angle));
        childWorldTranslation.angle += childLocalTranslation.angle;

        return constructorOrientation;
    }

}
