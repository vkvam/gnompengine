package com.flatfisk.gnomp.components.relatives;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.components.ConstructorComponent;
import com.flatfisk.gnomp.components.Node;
import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.RelativeComponent;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 05/12/15.
 */
public class SpatialRelative extends Node implements ConstructorComponent<SpatialRelative>, RelativeComponent {
    public Relative relativeType = Relative.CHILD;
    public Spatial local = Pools.obtainSpatial();
    public Spatial world = Pools.obtainSpatial();
    public SpatialInheritType inheritFromParentType = SpatialInheritType.POSITION_ANGLE;
    //public boolean reconstruct = false;

    protected SpatialRelative() {
        super(SpatialRelative.class);
    }

    @Override
    public void reset() {
        super.reset();
        local.vector.setZero();
        local.rotation = 0;
        world.vector.setZero();
        world.rotation = 0;
        relativeType = Relative.CHILD;
    }

    @Override
    public Relative getRelativeType() {
        return relativeType;
    }

    public SpatialRelative addCopy(GnompEngine gnompEngine,Entity entity){
        SpatialRelative relative = (SpatialRelative) super.addCopy(gnompEngine,entity);
        relative.relativeType = relativeType;
        relative.local = Pools.obtainSpatialFromCopy(local);
        relative.world = Pools.obtainSpatialFromCopy(world);
        relative.inheritFromParentType = inheritFromParentType;

        return relative;
    }

    public enum SpatialInheritType {
        POSITION,
        POSITION_ANGLE
    }

    public interface Controller extends Component{

    }
}
