package com.flatfisk.gnomp.components.relatives;

import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.RelativeComponent;

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
