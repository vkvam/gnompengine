package com.flatfisk.gnomp.components.relatives;

import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.components.ConstructorComponent;
import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.RelativeComponent;
import com.badlogic.ashley.core.GnompEngine;

/**
 * Created by Vemund Kvam on 04/12/15.
 */
public class PhysicsBodyRelative implements ConstructorComponent<PhysicsBodyRelative>,RelativeComponent {
    public Relative relativeType = Relative.CHILD;

    @Override
    public void reset() {
        relativeType = Relative.CHILD;
    }

    @Override
    public Relative getRelativeType() {
        return relativeType;
    }

    public PhysicsBodyRelative addCopy(GnompEngine gnompEngine,Entity entity){
        PhysicsBodyRelative relative = gnompEngine.addComponent(getClass(),entity);
        relative.relativeType = relativeType;
        return relative;
    }
}
