package com.flatfisk.gnomp.components.scenegraph;

import com.flatfisk.gnomp.components.Node;
import com.flatfisk.gnomp.math.Spatial;

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
        public Spatial worldChanges;
        public boolean sideStepped;
    }


}
