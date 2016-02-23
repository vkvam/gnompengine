package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.GnompMappers;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;

import java.util.Comparator;

import static com.flatfisk.gnomp.engine.GnompMappers.spatialNodeMap;
import static com.flatfisk.gnomp.engine.GnompMappers.renderableMap;

public class RenderSystem extends SortedIteratingSystem implements ApplicationListener {
    private static float ROOT2 = (float) Math.sqrt(2);

    private final SpriteBatch batch;
    private final Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private CameraSystem cameraSystem;
    private StatsSystem statsSystem;

    public RenderSystem(int priority, CameraSystem cameraSystem) {
        super(Family.all(Renderable.Container.class).get(),new RenderComperator(),priority);

        this.cameraSystem = cameraSystem;

        batch = new SpriteBatch();
    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    public void processEntity(Entity e,float f){

        Renderable.Container renderable = GnompMappers.renderableMap.get(e);
        Spatial.Node orientation = spatialNodeMap.get(e);

        Texture texture = renderable.texture;
        Vector2 offset = renderable.offset;
        Transform transform = orientation.world;
        float x = transform.vector.x, y= transform.vector.y;
        if(texture!=null){
            int tW = texture.getWidth();
            int tH = texture.getHeight();
            float tWDiv2 = ((float) tW) / 2 - offset.x;
            float tHDiv2 = ((float) tH) / 2 - offset.y;
            float xCenter = x - tWDiv2, yCenter = y - tHDiv2;

            // TODO: Very large Rectangular lines, get cut off to early.

            // The idea for this check is that a texture's rotated bounding-box never will be larger than root(2) of half of longest side.
            float longestSide = Math.max(tW, tH) * 0.5f * ROOT2;
            if (cameraSystem.getWorldCamera().frustum.boundsInFrustum(xCenter + tWDiv2, yCenter + tHDiv2, 0, longestSide, longestSide, 0)) {
                batch.draw(
                        texture,                            // Texture texture
                        xCenter,                            // float x
                        yCenter,                            // float y
                        tWDiv2,                             // float originX
                        tHDiv2,                             // float originY
                        tW,                                 // float width
                        tH,                                 // float height
                        1,                                  // float scaleX
                        1,                                  // float scaleY
                        transform.rotation,                  // float rotation
                        0,                                  // int srcX
                        0,                                  // int srcY
                        tW,                                 // int srcHeight
                        tH,                                 // int srcWidth
                        false,                              // boolean flipX
                        true                                // boolean flipY
                );
            }
        }else{
            LOG.info("Texture is null");
        }
    }

    @Override
    public void update(float f) {

        batch.setProjectionMatrix(cameraSystem.getWorldCamera().combined);
        batch.begin();

        super.update(f);

        batch.end();
        if(statsSystem!=null){
            statsSystem.addStat("Rendering");
            statsSystem.addLine();
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
        LOG.info("Disposing batch");
        batch.dispose();
    }


    @Override
    public void resize(final int w, final int h) {
        LOG.info("Resizing render system: w="+w+"h="+h);

    }

    public void setStatsSystem(StatsSystem statsSystem) {
        this.statsSystem = statsSystem;
    }

    private static class RenderComperator implements Comparator<Entity>{

        @Override
        public int compare(Entity spriteA, Entity spriteB){
            Renderable.Container renderableA = renderableMap.get(spriteA);
            Renderable.Container renderableB = renderableMap.get(spriteB);
            return (renderableA.zIndex - renderableB.zIndex) > 0 ? 1 : -1;
        }
    };
}
