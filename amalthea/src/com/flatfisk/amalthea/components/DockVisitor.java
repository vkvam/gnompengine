package com.flatfisk.amalthea.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

public class DockVisitor implements Component, Pool.Poolable{
    public boolean approachingDock = false;
    public float timeUsedToVisitDock = 0;
    public float timeUsedToVisitDockLimit = 120;
    public Entity dockToVisit = null;
    public Entity previousVisitedDock = null;

    @Override
    public void reset() {
        approachingDock=false;
        timeUsedToVisitDock=0;
        dockToVisit=null;
    }
}
