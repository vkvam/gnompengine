package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.abstracts.AbstractNode;
import com.flatfisk.gnomp.components.abstracts.IRelative;
import com.flatfisk.gnomp.components.abstracts.ISerializable;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.components.abstracts.ISpatialController;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by a-004213 on 22/04/14.
 */
public class Constructor implements ISpatialController, ISerializable<Constructor>, Pool.Poolable{

    @Override
    public void reset() {

    }


    @Override
    public Constructor addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }

    /**
     * Created by Vemund Kvam on 05/12/15.
     */
    public static class Node extends AbstractNode implements ISerializable<Node>, IRelative {
        public Relative relativeType = Relative.CHILD;
        public com.flatfisk.gnomp.math.Spatial local = Pools.obtainSpatial();
        public com.flatfisk.gnomp.math.Spatial world = Pools.obtainSpatial();
        public SpatialInheritType inheritFromParentType = SpatialInheritType.POSITION_ANGLE;

        public Node() {
            super(Node.class);
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

        public Node addCopy(GnompEngine gnompEngine,Entity entity){
            Node relative = (Node) super.addCopy(gnompEngine,entity);
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

    }
}
