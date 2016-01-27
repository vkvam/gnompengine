package com.flatfisk.gnomp.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.components.abstracts.AbstractNode;
import com.flatfisk.gnomp.components.abstracts.ISerializable;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.components.abstracts.ISpatialController;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class Scenegraph implements ISerializable<Scenegraph>,Pool.Poolable {

    @Override
    public void reset() {

    }

    @Override
    public Scenegraph addCopy(GnompEngine gnompEngine, Entity entity) {
        return gnompEngine.addComponent(getClass(),entity);
    }

    /**
     * Created by Vemund Kvam on 22/12/15.
     */
    public static class Node extends AbstractNode implements ISpatialController, ISerializable<Node> {

        protected Node() {
            super();
        }

        @Override
        public Node addCopy(GnompEngine gnompEngine, Entity entity) {
            Node node = ((Node) super.addCopy(gnompEngine,entity));
            return node;
        }
    }
}
