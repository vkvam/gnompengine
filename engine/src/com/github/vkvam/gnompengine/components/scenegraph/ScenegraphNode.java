package com.github.vkvam.gnompengine.components.scenegraph;

import com.github.vkvam.gnompengine.components.Node;
import com.github.vkvam.gnompengine.math.Translation;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class ScenegraphNode extends Node {
    public SideStepper sideStepper = null;

    protected ScenegraphNode() {
        super(ScenegraphNode.class);
    }

    public boolean hasChildren(){
        return children.length>0;
    }

    @Override
    public void reset() {
        sideStepper = null;
    }

    public static class SideStepper{
        public Translation worldChanges;
        public boolean sideStepped;
    }


}
