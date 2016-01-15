package com.flatfisk.gnomp.components.relatives;

import com.flatfisk.gnomp.components.Node;
import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.RelativeComponent;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 05/12/15.
 */
public class SpatialRelative extends Node implements RelativeComponent {
    public Relative relativeType = Relative.CHILD;
    public Spatial localSpatial;
    public Spatial worldSpatial = Pools.obtainSpatial();
    public TranslationInheritType inheritFromParentType = TranslationInheritType.POSITION_ANGLE;
    protected SpatialRelative() {
        super(SpatialRelative.class);
    }

    @Override
    public void reset() {
        localSpatial.vector.setZero();
        localSpatial.rotation = 0;
        worldSpatial.vector.setZero();
        worldSpatial.rotation = 0;
        relativeType = Relative.CHILD;
    }

    @Override
    public Relative getRelativeType() {
        return relativeType;
    }

    public enum TranslationInheritType{
        POSITION,
        POSITION_ANGLE
    }
}
