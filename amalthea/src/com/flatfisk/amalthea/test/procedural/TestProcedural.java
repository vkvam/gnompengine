package com.flatfisk.amalthea.test.procedural;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.test.procedural.noise.SimplexNoise;
import com.flatfisk.gnomp.engine.shape.CatmullPolygon;

import java.util.*;

public class TestProcedural implements ApplicationListener {

    private Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);

    SpriteBatch batch;
    Sprite sprite;
    private Texture tex;

    public TestProcedural() {

    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        sprite = new Sprite();
        draw_noise();
    }

    int i = 1;

    public boolean anyAroundAreFalse(boolean[][] pic, int x, int y) {
        return !(pic[x - 1][y] &&
                pic[x + 1][y] &&
                pic[x][y - 1] &&
                pic[x][y + 1] &&
                pic[x + 1][y + 1] &&
                pic[x - 1][y - 1] &&
                pic[x - 1][y + 1] &&
                pic[x + 1][y - 1]);
    }

    public boolean closeTo(Point p0, Point p1, int space) {
        int x0 = space, y0=space;
        for(int i=-x0;i<=x0;i++){
            for(int j=-y0;j<=y0;j++){
                if (p0.x+i==p1.x && p0.y+j==p1.y) {
                    return true;
                }
            }
        }
        return false;
    }

    public Point findNear(HashSet<Point> points, Point point, HashSet<Point> touched){
        int x0 = 1, y0=1;
        Point pToFind = new Point(0,0);
        for(int i=-x0;i<=x0;i++){
            for(int j=-y0;j<=y0;j++){
                pToFind.x = point.x + i;
                pToFind.y = point.y + j;
                if (points.contains(pToFind) && !touched.contains(pToFind)) {
                    return pToFind;
                }
            }
        }
        return null;
    }

    public static class Point {
        public int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            return y == point.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

    public void draw_noise() {

        SimplexNoise f = new SimplexNoise(2, 1f, 0.02f, i);
        i += 1;
        Pixmap p = new Pixmap(640, 480, Pixmap.Format.RGBA8888);
        boolean[][] shape = new boolean[640][480];
        for (int x = 0; x < 640; x++) {
            for (int y = 0; y < 480; y++) {
                double v = f.getNoise((double) x, (double) y);
                if (v > 0.75f) {
                    v-=0.75f;
                    v/=0.25f;
                    float v2= (float) v;
                    //p.drawPixel(x, y, Color.rgba8888(new Color(v2,v2,v2, 1)));
                    //p.drawPixel(x, y, Color.rgba8888(new Color(1, 0, 0, 1)));

                    shape[x][y] = true;
                } else {
                    shape[x][y] = false;
                }
            }
        }

        //HashMap<Point, Integer> points = new HashMap<Point, Integer>();
        HashSet<Point> points = new HashSet<Point>();

        for (int x = 1; x < 640-1; x++) {
            for (int y = 1; y < 480-1; y++) {
                if (shape[x][y] && anyAroundAreFalse(shape, x, y)) {
                    points.add(new Point(x,y));
                    //p.drawPixel(x, y, Color.rgba8888(new Color(1, 0, 0, 1)));
                }
            }
        }

        List<List<Point>> shapes = new ArrayList<List<Point>>();
        HashSet<Point> usedPoints = new HashSet<Point>();

        List<Point> nextPolygon = new ArrayList<Point>();
        for(Point p2 : points){

            Point near = findNear(points, p2, usedPoints);


            while (near!=null){
                nextPolygon.add(near);
                usedPoints.add(near);
                near = findNear(points, near, usedPoints);

                if(near!=null && nextPolygon.size()>8) {
                    // If we loop back, we exit
                    for (int i=0;i<nextPolygon.size()-8;i++) {
                        if (closeTo(nextPolygon.get(i), near, 1)) {
                            near = null;
                            break;
                        }
                    }
                }
            }

            boolean clear = true;
            if(nextPolygon.size()>10) {
                Point first = nextPolygon.get(0);
                Point last = nextPolygon.get(nextPolygon.size()-1);
                if(closeTo(first,last,3)) {
                    clear=false;
                    shapes.add(nextPolygon);
                }
            }

            if(clear) {
                Iterator<Point> iter = nextPolygon.iterator();
                while(iter.hasNext()) {
                    usedPoints.remove(iter.next());
                    iter.remove();
                }
            }
            nextPolygon= new ArrayList<Point>();
        }


        for(List<Point> pols: shapes){
            Random rand = new Random();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            for(Point p3: pols){
                p.drawPixel(p3.x, p3.y, Color.rgba8888(new Color(r, g, b, 1)));
            }
        }

        for(List<Point> pols: shapes) {
            Random rand = new Random();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            float[] pls = new float[pols.size()*2];
            int i=0;
            for (Point p3 : pols) {
                pls[i++] = p3.x;
                pls[i++] = p3.y;
            }
            CatmullPolygon polly = new CatmullPolygon();
            polly.lineWidth = 1;
            polly.lineColor = Color.BLUE;
            polly.fillColor = Color.FIREBRICK;
            polly.setVertices(pls);
            polly.getRenderPolygon();

        }

        if (tex == null) {
            tex = new Texture(p);
        } else {
            tex.draw(p, 0, 0);
        }

        p.dispose();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        draw_noise();
        Gdx.gl.glViewport(0, 0, 640, 480);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(tex,0,0);
        //batch.draw(tex, 0, 0, 1280,960,0,0,640,480, false,true);
        batch.end();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
