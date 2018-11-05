package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.GnompMappers;
import com.flatfisk.gnomp.engine.components.DebugTracer;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.shape.texture.TextureCoordinates;
import com.flatfisk.gnomp.math.Transform;

import java.util.Comparator;

import static com.flatfisk.gnomp.engine.GnompMappers.renderableMap;
import static com.flatfisk.gnomp.engine.GnompMappers.spatialNodeMap;

public class RenderSystem extends SortedIteratingSystem implements ApplicationListener {
    protected final Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);

    private static float ROOT2 = (float) Math.sqrt(2);

    private final SpriteBatch batch;

    private int renderedObjects = 0;

    private Camera camera;
    private StatsSystem statsSystem;

    public RenderSystem(int priority, Camera camera) {
        super(Family.all(Renderable.Container.class).get(), new RenderComparator(), priority);

        this.camera = camera;

        batch = new SpriteBatch();
    }

    public void processEntity(Entity e, float f) {

        Renderable.Container renderable = GnompMappers.renderableMap.get(e);
        Renderable rend = GnompMappers.renderableConstructorMap.get(e);

        if(rend==null){
            return;
        }
        TextureCoordinates.BoundingRectangle r = rend.boundingRectangle;

        Spatial.Node orientation = spatialNodeMap.get(e);

        Texture texture = renderable.texture;
        Vector2 offset = renderable.offset;
        Transform transform = orientation.world;
        float x = transform.vector.x, y = transform.vector.y;
        if (texture != null) {
            int tW = texture.getWidth();
            int tH = texture.getHeight();
            //float tWDiv2 = ((float) tW) / 2 - offset.x;
            //float tHDiv2 = ((float) tH) / 2 - offset.y;
            float tWDiv2 = ((float) tW) / 2 - offset.x;
            float tHDiv2 = ((float) tH) / 2 - offset.y;
            float left = x - tWDiv2;
            float bottom = y - tHDiv2;

            // Assume texture is a square for culling, ROOT2 adds the max change to bounding box when rotated
            //float longestSide = Math.max(tWDiv2, tHDiv2) * ROOT2;
            //if (camera.frustum.boundsInFrustum(x,y, 0, longestSide, longestSide, 0)) {

            if (camera.frustum.boundsInFrustum(r.offsetX+x, r.offsetY+y, 0, r.width/2, r.height/2, 0)){
                renderedObjects++;
                batch.draw(
                        texture,                            // Texture texture
                        left,                          // float x
                        bottom,                         // float y
                        tWDiv2,                             // float originX
                        tHDiv2,                             // float originY
                        tW,                                 // float width
                        tH,                                 // float height
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

        } else {
            Gdx.app.debug(getClass().getName(), "Texture is null");
        }
    }

    @Override
    public void update(float f) {

        renderedObjects = 0;


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
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
        Gdx.app.debug(getClass().getName(), "Disposing batch");
        batch.dispose();
    }


    @Override
    public void resize(final int w, final int h) {
        Gdx.app.debug(getClass().getName(), "Resizing render system: w=" + w + "h=" + h);

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
