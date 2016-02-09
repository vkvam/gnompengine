package com.flatfisk.gnomp.tests.catmull;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.components.Velocity;
import com.flatfisk.gnomp.engine.shape.CatmullPolygon;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.TestCatmullRender;
import com.flatfisk.gnomp.tests.components.Player;

import java.util.Random;

public class SnowballGrowSystem extends IntervalSystem {

    public SnowballGrowSystem(float interval, int priority) {
        super(interval, priority);
    }

    @Override
    protected void updateInterval() {

        Entity e = getEngine().getEntitiesFor(Family.all(Player.class).get()).get(0);
        CatmullPolygon p = (CatmullPolygon) e.getComponent(Shape.class).geometry;

        Renderable.Constructed n = e.getComponent(Renderable.Constructed.class);
        float w = n.texture.getWidth();

        Velocity v = e.getComponent(Velocity.class);
        float length = v.velocity.vector.len()*0.002f;

        if(Math.random()>0.94f) {
            Entity child = e.getComponent(Spatial.Node.class).children.get(0);
            e.getComponent(Spatial.Node.class).removeChild(child);
            getEngine().removeEntity(child);


            child = TestCatmullRender.createDot((GnompEngine) getEngine(), new Transform(0, 0, 0), w / 300);
            e.getComponent(Spatial.Node.class).addChild(child);
        }

        p.polygon.scale(.005f*Math.min(1,length));
        float[] vert = p.polygon.getVertices();
        vert[new Random().nextInt(vert.length)]+=(Math.random()*5-2.5)*length;
        ((GnompEngine) getEngine()).constructEntity(e);

    }
}