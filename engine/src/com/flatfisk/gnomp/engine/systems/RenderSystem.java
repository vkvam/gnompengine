package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Renderable;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;

import java.util.Comparator;

public class RenderSystem extends IteratingSystem implements ApplicationListener {
    private static float ROOT2 = (float) Math.sqrt(2);

    public SpriteBatch batch;

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private Array<Entity> renderQueue = new Array<Entity>();

    public ComponentMapper<Renderable.Constructed> renderableMapper;
    public ComponentMapper<Spatial.Node> orientationMapper;
    private Comparator comperator;
    private CameraSystem cameraSystem;

    public RenderSystem(int priority, CameraSystem cameraSystem) {
        super(Family.all(Renderable.Constructed.class).get(),priority);

        this.cameraSystem = cameraSystem;

        batch = new SpriteBatch();

        comperator = new Comparator<Entity>() {
            @Override
            public int compare(Entity spriteA, Entity spriteB){
                Renderable.Constructed renderableA = renderableMapper.get(spriteA);
                Renderable.Constructed renderableB = renderableMapper.get(spriteB);
                return (renderableA.zIndex - renderableB.zIndex) > 0 ? 1 : -1;
            }
        };
        renderableMapper = ComponentMapper.getFor(Renderable.Constructed.class);
        orientationMapper = ComponentMapper.getFor(Spatial.Node.class);
    }

    public void setCameraSystem(CameraSystem cameraSystem){
        this.cameraSystem = cameraSystem;
    }

    public void processEntity(Entity e,float f){
        renderQueue.add(e);
    }

    @Override
    public void update(float f) {
        super.update(f);
        OrthographicCamera orthographicCamera = cameraSystem.getCamera();

        batch.setProjectionMatrix(orthographicCamera.combined);

        batch.begin();
        renderQueue.sort(comperator);

        for (Entity e : renderQueue) {
            Renderable.Constructed renderable = renderableMapper.get(e);
            Spatial.Node orientation = orientationMapper.get(e);

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
                if (orthographicCamera.frustum.boundsInFrustum(xCenter + tWDiv2, yCenter + tHDiv2, 0, longestSide, longestSide, 0)) {
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
        batch.end();
        renderQueue.clear();
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
        batch.dispose();
    }


    @Override
    public void resize(final int w, final int h) {
        LOG.info("Resizing render system: w="+w+"h="+h);

    }

}
