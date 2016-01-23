package com.flatfisk.gnomp.components.scenegraph;

import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.components.ConstructorComponent;
import com.flatfisk.gnomp.components.Node;
import com.badlogic.ashley.core.GnompEngine;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class ScenegraphNode extends Node implements SpatialRelative.Controller, ConstructorComponent<ScenegraphNode>{

    protected ScenegraphNode() {
        super(ScenegraphNode.class);
    }

    @Override
    public ScenegraphNode addCopy(GnompEngine gnompEngine, Entity entity) {
        ScenegraphNode node = ((ScenegraphNode) super.addCopy(gnompEngine,entity));
        return node;
    }
}
