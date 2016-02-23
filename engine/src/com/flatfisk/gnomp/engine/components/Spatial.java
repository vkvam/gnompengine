package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.AbstractNode;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.components.abstracts.ISpatialController;
import com.flatfisk.gnomp.math.Transform;

/**
 * Root node for entity-construction hierarchies
 */
public class Spatial implements ISpatialController, ISerializable<Spatial>, Pool.Poolable{

    @Override
    public void reset() {

    }

    @Override
    public Spatial addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }

    /**
     * Transform node for constructed entities
     */
    public static class Node extends AbstractNode implements ISerializable<Node> {
        public Transform local = new Transform();
        public Transform world = new Transform();

        private final Vector2 tempTransformVector = new Vector2();


        public SpatialInheritType inheritFromParentType = SpatialInheritType.POSITION_ANGLE;

        public Node() {
            super();
        }

        @Override
        public void reset() {
            super.reset();
            local.setZero();
            world.setZero();
        }

        public Node addCopy(GnompEngine gnompEngine,Entity entity){

            return null;
        }

        public enum SpatialInheritType {
            POSITION,
            POSITION_ANGLE
        }

        public void addParentTransform(Transform parentWorld){
            boolean transferAngle = inheritFromParentType.equals(Spatial.Node.SpatialInheritType.POSITION_ANGLE);

            if(transferAngle) {
                world.set(parentWorld);
                tempTransformVector.set(local.vector);
                tempTransformVector.rotate(parentWorld.rotation);
                world.add(tempTransformVector,local.rotation);
            }else{
                world.set(parentWorld.vector, 0);
                world.add(local);
            }

        }

    }
}
