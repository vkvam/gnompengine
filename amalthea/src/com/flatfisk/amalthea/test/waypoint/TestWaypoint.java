package com.flatfisk.amalthea.test.waypoint;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.factories.procedural.DockGenerator;
import com.flatfisk.amalthea.factories.procedural.IslandGenerator;
import com.flatfisk.amalthea.factories.procedural.IslandPopulator;
import com.flatfisk.amalthea.path.*;
import com.flatfisk.gnomp.utils.Pools;

import java.util.*;

public class TestWaypoint implements ApplicationListener {

    private Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);

    ShapeRenderer shapeRenderer;
    Sprite sprite;
    private Texture tex;
    IslandGenerator.Islands ps;
    float scale = 0.025f;

    public TestWaypoint() {

    }

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        sprite = new Sprite();
        ps = IslandGenerator.getIslands(600,600,21332123, 0.7f, 40, null);
        //ps2 = IslandGenerator.getIslands(300,300,21332123, 0.65f);

        for(IslandGenerator.PolyPoint p:ps.islands) {
            //p.poly.shiftCenterToCentroid();
            p.poly.getGeometry().polygon.setScale(scale,scale);
        }
    }


    public void draw_noise(IslandGenerator.Islands islands, boolean drawAll, Color color) {

        List<IslandGenerator.PolyPoint> ps = islands.islands;
        for(IslandGenerator.PolyPoint p: ps){

            float[] verts = p.poly.getGeometry().getRenderPolygon().getTransformedVertices();

            float xS,yS;
            float x1 = xS = verts[0]+p.pos.x*scale+320;
            float y1 = yS = verts[1]+p.pos.y*scale+240;
            float x2=0,y2=0;

            shapeRenderer.setColor(color);
            for(int i=2;i<verts.length;i+=2) {
                x2=verts[i]+p.pos.x*scale+320;
                y2=verts[i+1]+p.pos.y*scale+240;
                shapeRenderer.line(x1,y1,x2,y2);
                x1=x2;
                y1=y2;
            }
            shapeRenderer.line(x2,y2, xS, yS);

            DockGenerator.DockPositions dps = IslandPopulator.getDockPositions(p, 100, 100);
            if (dps != null) {
                Vector2 direction = dps.direction.cpy().rotate90(-1);
                for (Vector2 p2 : dps.dockPositions) {

                    Vector2 dockWaypoint = new Vector2(p2.x + p.pos.x + direction.x * dps.height * 2, p2.y + p.pos.y + direction.y * dps.height * 2);

                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.circle((p2.x + p.pos.x) * scale + 320, (p2.y + p.pos.y) * scale + 240, 10 * scale);
                    shapeRenderer.setColor(Color.WHITE);
                    shapeRenderer.line((p2.x + p.pos.x) * scale + 320, (p2.y + p.pos.y) * scale + 240,
                            dockWaypoint.x * scale + 320, dockWaypoint.y * scale + 240);
                    shapeRenderer.setColor(Color.ORANGE);
                    shapeRenderer.circle(dockWaypoint.x * scale + 320, dockWaypoint.y * scale + 240, 50 * scale);
                }
            }
        }


        IslandGenerator.WayPoints wayPoints = islands.wayPoints;
        int width = wayPoints.width;
        int height = wayPoints.height;
        int waypointScale = wayPoints.scale;
        boolean[][] booleanMap = wayPoints.booleanMap;

        Vector2 firstNode = null;

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                if (booleanMap[x][y]) {
                    Vector2 v = Pools.obtainVector().set(x * waypointScale - waypointScale * width / 2, y * waypointScale - waypointScale * height / 2);
                    //wayPoints.add(v);
                    shapeRenderer.setColor(Color.ORANGE);
                    shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.circle(v.x * scale + 320, v.y * scale + 240, 50*scale);

                    if(firstNode == null){
                        firstNode = new Vector2(v.x, v.y);
                    }

                }
            }
        }


        FlatTiledGraph tiledGraph = new FlatTiledGraph(width, height, waypointScale, waypointScale);
        tiledGraph.diagonal = true;
        tiledGraph.init(booleanMap);

        //FlatTiledNode a = tiledGraph.getNode(20,20);
        FlatTiledNode a = tiledGraph.getClosestNode(new Vector2(-8000,8000));
        FlatTiledNode b = tiledGraph.getClosestNode(new Vector2(8000,-8000));

        tiledGraph.startNode = a;

        TiledSmoothableGraphPath<FlatTiledNode> path = new TiledSmoothableGraphPath<FlatTiledNode>();
        IndexedAStarPathFinder<FlatTiledNode> c = new IndexedAStarPathFinder<FlatTiledNode>(tiledGraph);
        c.searchNodePath(a,b, new ManhattanDistance<FlatTiledNode>(), path);

        //PathSmoother pathSmoother = new PathSmoother<FlatTiledNode, Vector2>(new TiledRaycastCollisionDetector<FlatTiledNode>(tiledGraph));
        //pathSmoother.smoothPath(path);

        shapeRenderer.setColor(Color.GREEN);
        for(FlatTiledNode node: path.nodes){
            Vector2 v = Pools.obtainVector().set(node.x * waypointScale - waypointScale * width / 2, node.y * waypointScale - waypointScale * height / 2);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(v.x* scale + 320, v.y* scale + 240, 100*scale);
        }

        Gdx.app.log(getClass().getName(), "Graph init");
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, 640, 480);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        draw_noise(ps, true, Color.GREEN);
        //draw_noise(ps2, false, Color.BLUE);
        shapeRenderer.end();
        /*
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
