package com.flatfisk.amalthea.factories.procedural;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ArrayMap;
import com.flatfisk.amalthea.test.procedural.noise.SimplexNoise;
import com.flatfisk.gnomp.engine.systems.BackgroundProvider;

public class NoiseBackgroundProvider implements BackgroundProvider {

    private final int width;
    private final int height;
    Color color = new Color();



    Vector2 center = new Vector2();
    Vector2 left = new Vector2();
    Vector2 leftTop = new Vector2();
    Vector2 top  = new Vector2();

    Sprite sCenter;
    Sprite sLeft;
    Sprite sLeftTop;
    Sprite sTop;

    SimplexNoise f = new SimplexNoise(1, 3f, 0.0023f, 1);
    SimplexNoise f2 = new SimplexNoise(1, 2f, 0.0026f, 11);
    SimplexNoise f3 = new SimplexNoise(1, 1f, 0.0003f, 111);

    //ObjectMap<Vector2, Texture> backgroundCache = new ObjectMap<Vector2, Texture>(10);
    IntMap<Texture> backgroundCache = new IntMap<Texture>();


    public NoiseBackgroundProvider(int width, int height) {
        this.width = width;
        this.height = height;

        sCenter = new Sprite();
        sLeft = new Sprite();
        sLeftTop = new Sprite();
        sTop = new Sprite();
    }

    private Pixmap create(int xIndex, int yIndex){

        int scale = 4;
        int offsetX = xIndex*width;
        int offsetY = yIndex*height;
        Pixmap pm = new Pixmap(width/scale, height/scale, Pixmap.Format.RGBA8888);
        double noiseY=0, noiseX=0;
        float noise = 0;

        Gdx.app.error("Noise: ",offsetX+":"+offsetY);
        for (int x = 0; x < width/scale; x++) {
            for (int y = 0; y < height/scale; y++) {
                noiseX = (double) (x*scale+offsetX);
                noiseY = (double) (y*scale+offsetY);

                noise = (float) f.getNoise(noiseX, noiseY);
                pm.drawPixel(x, y, Color.rgba8888(noise/3,noise/2,noise/1.3f, 1));

            }
        }

        int starAmount = (int) ((noise+0.2f)*100f);

        for(int i=0;i<starAmount;i++){
            int starX = (int) (f.getNoise((noise+i)*31312f, (noise+i)*123f)*((float) (width/scale)));
            int starY = (int) (f.getNoise((noise+i)*13131f, (noise+i)*123567f)*((float) (width/scale)));
            pm.drawPixel(starX, starY, Color.rgba8888(Color.WHITE));
        }


        return pm;
    }

    @Override
    public Sprite[] getTexture(Vector2 cameraPosition) {


        float x = cameraPosition.x;
        float y = cameraPosition.y;

        x /= (float) width;
        y /= (float) height;


        x = (int) Math.ceil(x);
        y = (int) Math.floor(y);

        center.set(x,y);
        left.set(x-1,y);
        top.set(x,y+1);
        leftTop.set(x-1,y+1);

        Array<Vector2> quads = new Array<Vector2>();
        quads.add(center);
        quads.add(left);
        quads.add(top);
        quads.add(leftTop);

        for(Vector2 quad: quads) {
            if (backgroundCache.get((int) (quad.x+quad.y*2048)) == null) {
                Gdx.app.log("BUILD", quad.toString());
                Pixmap map = create((int) quad.x, (int) quad.y);
                backgroundCache.put((int) (quad.x+quad.y*2048),
                        new Texture(map)
                );
            }
        }

        sCenter.setTexture(backgroundCache.get((int) (center.x+center.y*2048)));
        sLeft.setTexture(backgroundCache.get((int) (left.x+left.y*2048)));
        sTop.setTexture(backgroundCache.get((int) (top.x+top.y*2048)));
        sLeftTop.setTexture(backgroundCache.get((int) (leftTop.x+leftTop.y*2048)));

        sLeft.setPosition(left.x*width-cameraPosition.x, left.y*height-cameraPosition.y);
        sCenter.setPosition(center.x*width-cameraPosition.x,center.y*height-cameraPosition.y);
        sTop.setPosition(top.x*width-cameraPosition.x,top.y*height-cameraPosition.y);
        sLeftTop.setPosition(leftTop.x*width-cameraPosition.x, leftTop.y*height-cameraPosition.y);

        return new Sprite[]{
                sLeftTop,
                sTop,
                sLeft,
                sCenter
        };
    }

}
