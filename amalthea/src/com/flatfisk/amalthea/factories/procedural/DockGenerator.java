package com.flatfisk.amalthea.factories.procedural;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.amalthea.components.Dock;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.components.light.LightDef;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

import java.util.ArrayList;
import java.util.List;

import static com.flatfisk.gnomp.engine.CollisionCategories.*;

public class DockGenerator {


    public static Entity buildDock(GnompEngine engine, Transform t, float width, float height){
        Entity dock = engine.addEntity();
        Spatial.Node spatial = engine.createComponent(Spatial.Node.class, dock);
        spatial.local.set(t);

        Renderable.Node renderableNode = engine.createComponent(Renderable.Node.class, dock);
        renderableNode.intermediate = true;

        PhysicsBody.Node physicsNode = engine.createComponent(PhysicsBody.Node.class, dock);
        physicsNode.intermediate = true;

        dock.add(spatial);
        dock.add(renderableNode);
        dock.add(physicsNode);



        Dock dockC = engine.createComponent(Dock.class, dock);
        dock.add(dockC);
        spatial.addChild(buildDockSideWalls(engine, width, height, -1));
        spatial.addChild(buildDockSideWalls(engine, width, height, 1));

        Entity dockMain = buildDockMain(engine, width, height, dockC);
        spatial.addChild(dockMain);


        // Add lights
        if(t.vector.x%1>0.5f) {
            lightNode(engine, dock, 50, Color.GREEN, new Vector2(0, -70));
        }else{
            lightNode(engine, dock, 50, Color.RED, new Vector2(0, -70));
        }

        Color c = Color.WHITE.cpy();
        c.a=0.5f;
        lightNode(engine, dock, 200, c, new Vector2(0, -50));

        return dock;
    }

    public static Entity buildDockMain(GnompEngine engine, float width, float height, Dock dockC){
        Entity dockMain = engine.addEntity();
        Renderable.Node renderableNode = engine.createComponent(Renderable.Node.class, dockMain);
        dockMain.add(renderableNode);

        Spatial.Node spatial = engine.createComponent(Spatial.Node.class, dockMain);
        dockMain.add(spatial);

        dockMain.add(engine.createComponent(Scenegraph.class, dockMain));

        RectangularLine dockShape = new RectangularLine(0, width, Color.DARK_GRAY, Color.GRAY);
        dockShape.from.set(0,height);
        dockShape.to.set(0,-height);
        dockShape.createPolygonVertices();

        Shape<RectangularLine> shape = engine.createComponent(Shape.class, dockMain);
        shape.geometry = dockShape;
        dockMain.add(shape);



        // TODO: Combine into two physical entities that can be moved independently.
        Entity leftDoor = dockDoor(engine, height-1, 18, width/2, width*0.5f, Color.DARK_GRAY, true);
        leftDoor.getComponent(Spatial.Node.class).addChild(
                dockDoor(engine, height+18, 3, width/3, 0, Color.WHITE,false)
        );

        Entity rightDoor = dockDoor(engine, height-1, 19,width/2, -width*0.5f, Color.DARK_GRAY, true);
        rightDoor.getComponent(Spatial.Node.class).addChild(
                dockDoor(engine, height+18, 3, width/3, 0, Color.WHITE,false)
        );

        Scenegraph.Node n = engine.createComponent(Scenegraph.Node.class, dockMain);
        n.addChild(leftDoor);
        n.addChild(rightDoor);

        dockMain.add(n);

        spatial.addChild(leftDoor);
        spatial.addChild(rightDoor);

        dockC.leftDoor = leftDoor;
        dockC.rightDoor = rightDoor;
        dockC.doorWidth = (float) width/2;
        //spatial.addChild(dockDoor(engine, height+8, 3, width/3, -width, Color.WHITE));
        //spatial.addChild(dockDoor(engine, height+8, 3,width/3, width, Color.WHITE));
        return dockMain;
    }

    public static Entity buildDockSideWalls(GnompEngine engine, float width, float height, float sideDirection){
        Entity dockPhysics = engine.addEntity();

        PhysicsBody.Node physicsNode = engine.createComponent(PhysicsBody.Node.class, dockPhysics);
        dockPhysics.add(physicsNode);

        Renderable.Node renderableNode = engine.createComponent(Renderable.Node.class, dockPhysics);
        dockPhysics.add(renderableNode);

        Spatial.Node spatial = engine.createComponent(Spatial.Node.class, dockPhysics);
        dockPhysics.add(spatial);

        RectangularLine dockShape = new RectangularLine(0, 5, Color.BROWN, Color.BROWN);
        dockShape.from.set(width*sideDirection,height);
        dockShape.to.set(width*sideDirection,-height);
        dockShape.createPolygonVertices();

        Shape<RectangularLine> shape = engine.createComponent(Shape.class, dockPhysics);
        shape.geometry = dockShape;
        dockPhysics.add(shape);

        PhysicalProperties physicalProperties = engine.createComponent(PhysicalProperties.class,dockPhysics);
        physicalProperties.density = .01f;
        physicalProperties.friction = 5f;
        physicalProperties.categoryBits = CATEGORY_PLATFORM;
        physicalProperties.maskBits = CATEGORY_PLAYER | CATEGORY_SENSOR | CATEGORY_ENEMY | CATEGORY_LIGHT;


        dockPhysics.add(physicalProperties);

        return dockPhysics;
    }


    public static Entity dockDoor(GnompEngine engine, float dockHeight, float doorDepth, float dockWidth, float offset, Color color, boolean scenegraphed){
        Entity dockDoor = engine.addEntity();

        Vector2 shapeOffset;

        RectangularLine dockShape = new RectangularLine(0, dockWidth, null, color);

        dockShape.from.set(0,-dockHeight);
        dockShape.to.set(0,-dockHeight-doorDepth);
        dockShape.createPolygonVertices();

        //shapeOffset = dockShape.shiftCenterToCentroid();
        if(scenegraphed) {
            dockDoor.add(engine.createComponent(Renderable.class, dockDoor));
            dockDoor.add(engine.createComponent(Scenegraph.Node.class, dockDoor));
        }
        else{
            //shapeOffset.scl(0.1f);
        }



        Spatial.Node orientationRelative = engine.createComponent(Spatial.Node.class, dockDoor);
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION_ANGLE;
        //orientationRelative.local.vector.set(shapeOffset);
        orientationRelative.local.vector.x=offset;
        dockDoor.add(orientationRelative);

        Renderable.Node renderableNode = engine.createComponent(Renderable.Node.class, dockDoor);
        dockDoor.add(renderableNode);

        Shape<RectangularLine> shape = engine.createComponent(Shape.class, dockDoor);
        shape.geometry = dockShape;
        dockDoor.add(shape);

        return dockDoor;
    }

    public static void lightNode(GnompEngine engine, Entity dock, float distance, Color color, Vector2 pos){
        Entity light = engine.addEntity();

        LightDef.Point pointDef = new LightDef.Point();
        pointDef.color = color;
        pointDef.distance = distance;
        pointDef.staticLight = true;
        pointDef.group = 0;

        Light l = engine.createComponent(Light.class, dock);
        l.lightDef = pointDef;
        light.add(l);

        Spatial.Node spat = engine.createComponent(Spatial.Node.class, light);
        spat.local.vector.set(pos);
        light.add(spat);

        dock.getComponent(Spatial.Node.class).children.add(light);
    }

    public static DockPositions getDockPositions(LongestStraight straight, float w, float h){
        DockPositions dockPositions = new DockPositions();
        dockPositions.dockPositions.clear();
        float d = w/6;
        int canFit =(int) ((straight.distance-2*d)/(w+d));
        float distFromEnd = straight.distance-(canFit*(w+d));
        // BEGIN
        float lerpPoint = (distFromEnd+d)/2+(w/2);
        dockPositions.dockPositions.add(straight.start.cpy().lerp(straight.end, lerpPoint/straight.distance));
        for(int i=1;i<canFit;i++){
            lerpPoint+=(w+d);
            dockPositions.dockPositions.add(straight.start.cpy().lerp(straight.end, lerpPoint/straight.distance));
        }

        dockPositions.direction = straight.end.sub(straight.start).nor();
        dockPositions.width = w;
        dockPositions.height = h;
        return dockPositions;
    }

    public static LongestStraight findLargestStraight(com.badlogic.gdx.math.Polygon renderPolygon) {
        Vector2 previous = Pools.obtainVector();
        Vector2 current = Pools.obtainVector();

        //Shape<Polygon> s = island.getComponent(Shape.class);
        //float[] verts = s.geometry.getRenderPolygon().getVertices();
        float[] verts = renderPolygon.getVertices();

        float longestDist2 = 0;
        int longestDistIndexStart = 0;

        for (int i = 2; i < verts.length; i += 2) {
            previous.x = verts[i - 2];
            previous.y = verts[i - 1];

            current.x = verts[i];
            current.y = verts[i + 1];

            float distSquared = previous.dst2(current);
            if(distSquared> longestDist2){
                longestDist2 = distSquared;
                longestDistIndexStart = i-2;
            }
        }
        previous.x = verts[longestDistIndexStart];
        previous.y = verts[longestDistIndexStart+1];
        current.x = verts[longestDistIndexStart+2];
        current.y = verts[longestDistIndexStart+3];
        return new LongestStraight(
                longestDistIndexStart,
                previous,
                current,
                (float) Math.sqrt(longestDist2)
        );
    }

    public static class LongestStraight{
        float startIndex;
        Vector2 start;
        Vector2 end;
        float distance;

        public LongestStraight(float startIndex, Vector2 start, Vector2 end, float distance) {
            this.startIndex = startIndex;
            this.start = start;
            this.end = end;
            this.distance = distance;
        }
    }

    public static class DockPositions{
        public Vector2 direction;
        public List<Vector2> dockPositions = new ArrayList<Vector2>();
        public float width;
        public float height;
    }

    public void buildDock(){

    }

}
