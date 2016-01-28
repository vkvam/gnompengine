package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.abstracts.AbstractNode;
import com.flatfisk.gnomp.components.abstracts.ISerializable;
import com.flatfisk.gnomp.components.abstracts.ISpatialController;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Root node where construction of entities is started.
 */
public class Spatial implements ISpatialController, ISerializable<Spatial>, Pool.Poolable{

    @Override
    public void reset() {

    }

    @Override
    public Spatial addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(this.getClass(),entity);
    }

    /**
     * Defines the position of all entities constructed.
     */
    public static class Node extends AbstractNode implements ISerializable<Node> {
        public Transform local = Pools.obtainSpatial();
        public Transform world = Pools.obtainSpatial();
        public SpatialInheritType inheritFromParentType = SpatialInheritType.POSITION_ANGLE;

        public Node() {
            super();
        }

        @Override
        public void reset() {
            super.reset();
            local.vector.setZero();
            local.rotation = 0;
            world.vector.setZero();
            world.rotation = 0;
        }

        public Node addCopy(GnompEngine gnompEngine,Entity entity){
            Node relative = (Node) super.addCopy(gnompEngine,entity);
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
