package com.flatfisk.amalthea.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.flatfisk.amalthea.components.MapColor;
import com.flatfisk.gnomp.engine.GnompMappers;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.StatsSystem;
import com.flatfisk.gnomp.math.Transform;

import java.util.Comparator;
import java.util.HashMap;

import static com.flatfisk.gnomp.engine.GnompMappers.renderableMap;
import static com.flatfisk.gnomp.engine.GnompMappers.spatialNodeMap;


public class MapRenderSystem extends SortedIteratingSystem implements ApplicationListener {
    protected final Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);
    public final static ComponentMapper<MapColor> mapColorComponentMapper = ComponentMapper.getFor(MapColor.class);
    private static float ROOT2 = (float) Math.sqrt(2);

    private final SpriteBatch batch;
    private int renderedObjects = 0;

    private OrthographicCamera camera;
    private CameraSystem cameraSystem;
    private StatsSystem statsSystem;
    private Matrix4 ONE_MATRIX = new Matrix4();



    private Texture mapBackgroundTexture;

    private java.util.Map<String, Texture> mapColorMap = new HashMap<String, Texture>();


    public MapRenderSystem(int priority, CameraSystem camera) {
        super(Family.all(Renderable.Container.class).get(), new RenderComparator(), priority);
        this.cameraSystem = camera;
        this.camera = camera.getMapCamera();

        batch = new SpriteBatch();
        this.createTextures();
        ONE_MATRIX.scale(0.01f, 0.01f, 1);
    }

    private void createTextures(){


        Pixmap pixMap = new Pixmap( 10,10, Pixmap.Format.RGBA8888 );
        pixMap.setColor(new Color(0,0,0.2f,0.25f));
        pixMap.fill();
        mapBackgroundTexture = new Texture(pixMap);
        pixMap.dispose();
    }

    private Texture createSimpleTexture(Color color){
        Pixmap pixMap = new Pixmap( 8,8, Pixmap.Format.RGBA8888 );
        pixMap.setColor( color);
        pixMap.fill();
        Texture t = new Texture(pixMap);
        pixMap.dispose();
        return t;
    }

    public void processEntity(Entity e, float f) {

        Renderable.Container renderable = GnompMappers.renderableMap.get(e);
        Spatial.Node orientation = spatialNodeMap.get(e);

        Texture t;

        if (mapColorComponentMapper.has(e)){

            MapColor c = mapColorComponentMapper.get(e);

            if (! mapColorMap.containsKey(c.color.toString())){
                mapColorMap.put(c.color.toString(), createSimpleTexture(c.color.cpy()));
            }

            t = mapColorMap.get(c.color.toString());

            Texture texture = renderable.texture;
            Vector2 offset = renderable.offset;
            Transform transform = orientation.world;
            float x = transform.vector.x, y = transform.vector.y;
            if (texture != null) {

                // TODO: WTF1: camera.zoom 4, wtf??
                int tW = Math.max(texture.getWidth(), ((int) camera.zoom*4) );
                int tH = Math.max(texture.getHeight(), ((int) camera.zoom*4) );
                float tWDiv2 = ((float) tW) / 2 - offset.x;
                float tHDiv2 = ((float) tH) / 2 - offset.y;
                float lowerLeft = x - tWDiv2;
                float lowerRight = y - tHDiv2;



                float mul = 1;
                // TODO: WTF2: Dependant on camera.zoom?? What am I trying to do?
                if(c.color != Color.CLEAR){//tW<camera.zoom*5 && tH<camera.zoom*5){
                    mul/=2;
                    texture=t;
                }

                // Assume texture is a square for culling.
                float longestSide = Math.max(tWDiv2,tHDiv2) * ROOT2;
                if (camera.frustum.boundsInFrustum(x,y, 0, longestSide, longestSide, 0)) {
                    renderedObjects++;
                    batch.draw(
                            texture,                            // Texture texture
                            lowerLeft,                          // float x
                            lowerRight,                         // float y
                            tWDiv2,                             // float originX
                            tHDiv2,                             // float originY
                            tW*mul,                                 // float width
                            tH*mul,                                 // float height
                            1,                                  // float scaleX
                            1,                                  // float scaleY
                            transform.rotation,                 // float rotation
                            0,                                  // int srcX
                            0,                                  // int srcY
                            tW,                                 // int srcHeight
                            tH,                                 // int srcWidth
                            false,                              // boolean flipX
                            renderable.flipped                                // boolean flipY
                    );
                }

            }

        }
        /*else {
            Gdx.app.debug(getClass().getName(),"Texture is null");
        }*/
    }

    private void drawBackground(){

        int tW = mapBackgroundTexture.getWidth();
        int tH = mapBackgroundTexture.getHeight();

        batch.draw(
                mapBackgroundTexture,                   // Texture texture
                -1/this.ONE_MATRIX.getScaleX(),                          // float x
                -1/this.ONE_MATRIX.getScaleY(),                         // float y
                0,                             // float originX
                0,                             // float originY
                2/this.ONE_MATRIX.getScaleX(),                                 // float width
                2/this.ONE_MATRIX.getScaleY(),                                 // float height
                1,                                  // float scaleX
                1,                                  // float scaleY
                0,                 // float rotation
                0,                                  // int srcX
                0,                                  // int srcY
                mapBackgroundTexture.getWidth(),                                 // int srcHeight
                mapBackgroundTexture.getHeight(),                                 // int srcWidth
                false,                              // boolean flipX
                false                                // boolean flipY
        );

    }

    @Override
    public void update(float f) {
        FitViewport vp = cameraSystem.mapViewPort;
        Gdx.gl.glViewport(vp.getScreenX(), vp.getScreenY(), vp.getScreenWidth(), vp.getScreenHeight());

        renderedObjects = 0;


        batch.setProjectionMatrix(ONE_MATRIX);
        batch.begin();
        drawBackground();
        batch.setProjectionMatrix(camera.combined);
        super.update(f);
        batch.end();


        if (statsSystem != null) {
            statsSystem.addStat("Rendering");
            statsSystem.addStat("# of objects:" + renderedObjects);
        }
    }

    @Override
    public void create() {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        Gdx.app.debug(getClass().getName(),"Disposing batch");
        batch.dispose();
        for(Texture t: mapColorMap.values()){
            t.dispose();
        }
        mapColorMap.clear();
    }


    @Override
    public void resize(final int w, final int h) {
        Gdx.app.debug(getClass().getName(),"Resizing render system: w=" + w + "h=" + h);

    }

    public void setStatsSystem(StatsSystem statsSystem) {
        this.statsSystem = statsSystem;
    }

    private static class RenderComparator implements Comparator<Entity> {

        @Override
        public int compare(Entity spriteA, Entity spriteB) {
            Renderable.Container renderableA = renderableMap.get(spriteA);
            Renderable.Container renderableB = renderableMap.get(spriteB);
            return (renderableA.zIndex - renderableB.zIndex) > 0 ? 1 : -1;
        }
    }

    ;
}
