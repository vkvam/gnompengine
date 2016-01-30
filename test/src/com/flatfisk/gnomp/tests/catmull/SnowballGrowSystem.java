package com.flatfisk.gnomp.tests.catmull;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.shape.CatmullPolygon;
import com.flatfisk.gnomp.tests.components.Player;

public class SnowballGrowSystem extends IntervalSystem {

    public SnowballGrowSystem(float interval, int priority) {
        super(interval, priority);
    }

    @Override
    protected void updateInterval() {
        Entity e = getEngine().getEntitiesFor(Family.all(Player.class).get()).get(0);
        CatmullPolygon p = (CatmullPolygon) e.getComponent(Shape.class).geometry;
        p.polygon.scale(.005f);
        float[] vert = p.polygon.getVertices();
        vert[2]+=Math.random()*5-2.5;
        ((GnompEngine) getEngine()).constructEntity(e);
    }
}