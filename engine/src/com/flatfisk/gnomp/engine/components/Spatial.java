package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.components.abstracts.AbstractNode;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.components.abstracts.ISpatialController;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Root node for entity-construction hierarchies
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
     * Transform node for constructed entities
     */
    public static class Node extends AbstractNode implements ISerializable<Node> {
        public Transform local = Pools.obtainTransform();
        public Transform world = Pools.obtainTransform();
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
