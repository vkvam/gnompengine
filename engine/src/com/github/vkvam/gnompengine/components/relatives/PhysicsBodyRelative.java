package com.github.vkvam.gnompengine.components.relatives;

import com.github.vkvam.gnompengine.components.Relative;
import com.github.vkvam.gnompengine.components.RelativeComponent;

/**
 * Created by Vemund Kvam on 04/12/15.
 */
public class PhysicsBodyRelative implements RelativeComponent {
    public Relative relativeType = Relative.CHILD;

    @Override
    public void reset() {
        relativeType = Relative.CHILD;
    }

    @Override
    public Relative getRelativeType() {
        return relativeType;
    }
}
