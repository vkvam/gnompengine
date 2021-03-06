package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.components.abstracts.AbstractNode;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISpatialController;

/**
 * Root node for a scenegraph hierarchy
 */
public class Scenegraph implements ISerializable<Scenegraph>,Pool.Poolable {

    @Override
    public void reset() {

    }

    @Override
    public Scenegraph addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }

    /**
     * A scenegraph node
     */
    public static class Node extends AbstractNode implements ISpatialController, ISerializable<Node> {

        protected Node() {
            super();
        }

        @Override
        public Node addCopy(GnompEngine gnompEngine, Entity entity) {
            return null;
        }
    }
}
