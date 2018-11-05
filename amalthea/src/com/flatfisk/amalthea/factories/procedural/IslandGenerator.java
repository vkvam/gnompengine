package com.flatfisk.amalthea.factories.procedural;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.flatfisk.amalthea.factories.color.Colors;
import com.flatfisk.amalthea.test.procedural.TestProcedural;
import com.flatfisk.amalthea.test.procedural.noise.SimplexNoise;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.shape.CatmullPolygon;
import com.flatfisk.gnomp.engine.shape.Polygon;

import java.util.*;

public class IslandGenerator {
    public static Colors colors = new Colors(Color.FOREST, Color.DARK_GRAY);

    private static boolean allAreTrue(boolean[][] pic, int x, int y, int sizeX, int sizeY) {

        boolean xMin = x == 0;
        boolean xMax = x == sizeX - 1;
        boolean yMin = y == 0;
        boolean yMax = y == sizeY - 1;


        return (xMin || yMax || pic[x - 1][y + 1]) &&
                (xMin || yMin || pic[x - 1][y - 1]) &&
                (xMax || yMax || pic[x + 1][y + 1]) &&
                (xMax || yMin || pic[x + 1][y - 1]) &&
                (xMin || pic[x - 1][y]) &&
                (yMin || pic[x][y - 1]) &&
                (yMax || pic[x][y + 1]) &&
                (xMax || pic[x + 1][y]);
    }

    private static boolean closeTo(TestProcedural.Point p0, TestProcedural.Point p1, int space) {
        int x0 = space, y0 = space;
        for (int i = -x0; i <= x0; i++) {
            for (int j = -y0; j <= y0; j++) {
                if (p0.x + i == p1.x && p0.y + j == p1.y) {
                    return true;
                }
            }
        }
        return false;
    }

    private static TestProcedural.Point findNear(HashSet<TestProcedural.Point> points, TestProcedural.Point point, HashSet<TestProcedural.Point> touched) {
        int x0 = 1, y0 = 1;
        TestProcedural.Point pToFind = new TestProcedural.Point(0, 0);
        for (int i = -x0; i <= x0; i++) {
            for (int j = -y0; j <= y0; j++) {
                pToFind.x = point.x + i;
                pToFind.y = point.y + j;
                if (points.contains(pToFind) && !touched.contains(pToFind)) {
                    return pToFind;
                }
            }
        }
        return null;
    }


    public static class Islands {
        public List<PolyPoint> islands;
        public WayPoints wayPoints = new WayPoints();
    }


    public static class WayPoints {
        public boolean[][] booleanMap;
        public int scale;
        public int width;
        public int height;
    }


    public static class PolyPoint {
        public Vector2 pos;
        public Shape<CatmullPolygon> poly;

        PolyPoint(Vector2 pos, Shape<CatmullPolygon> poly) {
            this.pos = pos;
            this.poly = poly;
        }
    }

    private static boolean pointInPolygons(Vector2 v, List<Shape<CatmullPolygon>> polygons) {
        for (Shape<CatmullPolygon> p : polygons) {
            if (p.getGeometry().polygon.contains(v)) {
                return true;
            }
        }
        return false;
    }

    private static boolean[][] removeEdges(boolean[][] waypointbool, int X, int Y){
        boolean[][] waypointbool2 = new boolean[X][Y];


        for (int i = 0; i < waypointbool.length; i++)
            for (int j = 0; j < waypointbool[i].length; j++)
                waypointbool2[i][j] = waypointbool[i][j];

        for (int x = 0; x < X; x ++) {
            for (int y = 0; y < Y; y ++) {
                if (!allAreTrue(waypointbool2, x, y, X, Y)) {
                    waypointbool[x][y] = false;
                }
            }
        }

        return waypointbool;
    }

    private static boolean[][] getWayPoints(int X, int Y, int skip, int SCALE, boolean[][] shape, List<Shape<CatmullPolygon>> islandPolys){
        boolean[][] waypointbool = new boolean[X / skip][Y / skip];

        Vector2 testPoint = new Vector2();
        for (int x = 0; x < X; x ++) {
            for (int y = 0; y < Y; y ++) {
                if (x % skip == 0 && y % skip == 0) {
                    testPoint.set(x - X / 2, y - Y / 2).scl(SCALE);
                    if (!shape[x][y] || !pointInPolygons(testPoint, islandPolys)) {
                        waypointbool[x / skip][y / skip] = true;
                    }
                }
            }
        }
        return waypointbool;
    }

    public static Islands getIslands(int width, int height, int seed, float limit, float SCALE, GnompEngine engine) {

        //int X=1080,Y=1080;
        int X = width, Y = height;
        int colorIndex = 0;

        SimplexNoise f = new SimplexNoise(2, 1f, 0.04f, seed);
        //Pixmap p = new Pixmap(640, 480, Pixmap.Format.RGBA8888);
        boolean[][] shape = new boolean[X][Y];
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                double v = f.getNoise((double) x, (double) y);
                shape[x][y] = v > limit;
            }
        }


        HashSet<TestProcedural.Point> points = new HashSet<TestProcedural.Point>();
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                if (shape[x][y] && !allAreTrue(shape, x, y, X, Y)) {
                    points.add(new TestProcedural.Point(x - X / 2, y - Y / 2));
                }
            }
        }

        List<List<TestProcedural.Point>> shapes = new ArrayList<List<TestProcedural.Point>>();
        HashSet<TestProcedural.Point> usedPoints = new HashSet<TestProcedural.Point>();

        List<TestProcedural.Point> nextPolygon = new ArrayList<TestProcedural.Point>();
        for (TestProcedural.Point p2 : points) {

            TestProcedural.Point near = findNear(points, p2, usedPoints);


            while (near != null) {
                nextPolygon.add(near);
                usedPoints.add(near);
                near = findNear(points, near, usedPoints);

                if (near != null && nextPolygon.size() > 8) {
                    // If we loop back, we exit
                    for (int i = 0; i < nextPolygon.size() - 8; i++) {
                        if (closeTo(nextPolygon.get(i), near, 1)) {
                            near = null;
                            break;
                        }
                    }
                }
            }

            boolean clear = true;
            if (nextPolygon.size() > 10) {
                TestProcedural.Point first = nextPolygon.get(0);
                TestProcedural.Point last = nextPolygon.get(nextPolygon.size() - 1);
                if (closeTo(first, last, 3)) {
                    clear = false;
                    shapes.add(nextPolygon);
                }
            }

            if (clear) {
                Iterator<TestProcedural.Point> iter = nextPolygon.iterator();
                while (iter.hasNext()) {
                    usedPoints.remove(iter.next());
                    iter.remove();
                }
            }
            nextPolygon = new ArrayList<TestProcedural.Point>();
        }

        List<PolyPoint> islands = new ArrayList<PolyPoint>();
        List<Shape<CatmullPolygon>> islandPolys = new ArrayList<Shape<CatmullPolygon>>();

        for (List<TestProcedural.Point> pols : shapes) {
            Random rand = new Random();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            Array<Vector2> pls = new Array<Vector2>();
            int i = 0;
            int c = 0;
            for (TestProcedural.Point p3 : pols) {
                if (c++ % 8 == 0) {
                    pls.add(new Vector2(p3.x * SCALE, p3.y * SCALE));
                }
            }

            if (pls.size < 6) {
                continue;
            }

            colorIndex += 20;
            if (colorIndex > 100) {
                colorIndex = 0;
            }

            Color col = colors.gradientColor((float) colorIndex / (float) 100);

            // TODO: Create shape and obtain this.

            Shape<CatmullPolygon> pol = engine.createComponent(Shape.class, null);
            CatmullPolygon polly = pol.obtain(CatmullPolygon.class);
            polly.lineWidth = 1;
            polly.lineColor=col;
            polly.fillColor = col;
            polly.physicsResolution = 1;
            polly.renderResolution = 1;
            polly.setVertices(pls);

            islandPolys.add(pol);


        }
        int skip = 4;

        boolean[][] waypointbool = getWayPoints(X,Y, skip, (int) SCALE, shape, islandPolys);

        removeEdges(waypointbool, X/skip, Y/skip);

        for (Shape<CatmullPolygon> p2 : islandPolys) {
            Vector2 vec = p2.getGeometry().shiftCenterToCentroid();
            islands.add(new PolyPoint(vec, p2));
        }

        IslandGenerator.Islands islands1 = new IslandGenerator.Islands();
        islands1.islands = islands;
        islands1.wayPoints.booleanMap = waypointbool;
        islands1.wayPoints.scale = (int) SCALE*skip;
        islands1.wayPoints.width = X/skip;
        islands1.wayPoints.height = Y/skip;

        return islands1;
    }
}
