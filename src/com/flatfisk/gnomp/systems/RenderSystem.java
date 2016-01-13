package com.flatfisk.gnomp.systems;


import com.badlogic.ashley.core.*;
<<<<<<< HEAD
import com.badlogic.gdx.ApplicationListener;
=======
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
<<<<<<< HEAD
import com.flatfisk.gnomp.components.constructed.Renderable;
import com.flatfisk.gnomp.components.relatives.OrientationRelative;
import com.flatfisk.gnomp.math.Translation;

import java.util.Comparator;

public class RenderSystem extends EntitySystem implements ApplicationListener, EntityListener {
    private static float ROOT2 = (float) Math.sqrt(2);


    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
=======
import com.flatfisk.gnomp.components.Renderable;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.gdx.GdxSystem;
import com.flatfisk.gnomp.math.Translation;

public class RenderSystem extends FamilySystem implements GdxSystem, EntityListener {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public ComponentMapper<Renderable> renderableMapper;
    public ComponentMapper<ScenegraphNode> scenegraphNodeMapper;
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    private SpriteBatch batch;
    private OrthographicCamera orthographicCamera;
    private Array<Entity> renderQueue = new Array<Entity>();

<<<<<<< HEAD
    public ComponentMapper<Renderable> renderableMapper;
    public ComponentMapper<OrientationRelative> orientationMapper;
    private Comparator comperator;
    private Family family;

    public RenderSystem(int priority) {
        family = Family.all(Renderable.class).get();
        this.priority = priority;
        batch = new SpriteBatch();
        orthographicCamera = new OrthographicCamera(640, 480);

        comperator = new Comparator<Entity>() {
            @Override
            public int compare(Entity spriteA, Entity spriteB){
                Renderable renderableA = renderableMapper.get(spriteA);
                Renderable renderableB = renderableMapper.get(spriteB);
                return (renderableA.zIndex - renderableB.zIndex) > 0 ? 1 : -1;
            }
        };
    }

    public Family getFamily() {
        return family;
=======
    public RenderSystem(int priority) {
        super(Family.all(Renderable.class, ScenegraphNode.class).get(),priority);
        batch = new SpriteBatch();
        orthographicCamera = new OrthographicCamera(640, 480);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

    @Override
    public void addedToEngine(Engine engine) {
        renderableMapper = ComponentMapper.getFor(Renderable.class);
<<<<<<< HEAD
        orientationMapper = ComponentMapper.getFor(OrientationRelative.class);
=======
        scenegraphNodeMapper = ComponentMapper.getFor(ScenegraphNode.class);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

    public OrthographicCamera getCamera() {
        return orthographicCamera;
    }

    @Override
    public void update(float f) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

<<<<<<< HEAD
        orthographicCamera.update(true);
=======
        orthographicCamera.update();
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
        batch.setProjectionMatrix(orthographicCamera.combined);
        batch.begin();

        for (Entity e : renderQueue) {
            Renderable renderable = renderableMapper.get(e);
<<<<<<< HEAD
            OrientationRelative orientation = orientationMapper.get(e);

            Texture texture = renderable.texture;
            Vector2 offset = renderable.offset;
            Translation translation = orientation.worldTranslation;
            float x = translation.position.x, y=translation.position.y;
            int tW = texture.getWidth();
            int tH = texture.getHeight();
            float tWDiv2 = ((float) tW) / 2 - offset.x;
            float tHDiv2 = ((float) tH) / 2 - offset.y;
            float xCenter = x-tWDiv2, yCenter=y-tHDiv2;

            // The idea for this check is that a texture's rotated bounding-box never will be larger than root(2) of half of longest side.
            float longestSide = Math.max(tW,tH)*0.5f*ROOT2;

            if(getCamera().frustum.boundsInFrustum(xCenter+tWDiv2, yCenter+tHDiv2,0, longestSide,longestSide, 0)) {
                batch.draw(
                        texture,                            // Texture texture
                        xCenter,                            // float x
                        yCenter,                            // float y
=======
            ScenegraphNode scenegraphNode = scenegraphNodeMapper.get(e);

                Texture texture = renderable.texture;
                Vector2 offset = renderable.offset;
                Translation translation = scenegraphNode.worldTranslation;

                int tW = texture.getWidth();
                int tH = texture.getHeight();
                float tWDiv2 = tW / 2 - offset.x;
                float tHDiv2 = tH / 2 - offset.y;

                batch.draw(
                        texture,                            // Texture texture
                        translation.position.x - tWDiv2,      // float x
                        translation.position.y - tHDiv2,      // float y
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
                        tWDiv2,                             // float originX
                        tHDiv2,                             // float originY
                        tW,                                 // float width
                        tH,                                 // float height
                        1,                                  // float scaleX
                        1,                                  // float scaleY
                        translation.angle,                  // float rotation
                        0,                                  // int srcX
                        0,                                  // int srcY
<<<<<<< HEAD
                        tW,                                 // int srcHeight
                        tH,                                 // int srcWidth
                        false,                              // boolean flipX
                        true                                // boolean flipY
                );
            }
        }
=======
                        tW,                                 // int srcWidth
                        tH,                                 // int srcHeight
                        false,                              // boolean flipX
                        true                                // boolean flipY
                );
        }


>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
        batch.end();
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

        Gdx.graphics.setDisplayMode(w, h, false);
        orthographicCamera.update();
        orthographicCamera.viewportWidth = w;
        orthographicCamera.viewportHeight = h;
    }

    @Override
    public void entityAdded(Entity entity) {
<<<<<<< HEAD
        renderQueue.add(entity);
        renderQueue.sort(comperator);
=======
        if(getFamily().matches(entity)) {
            renderQueue.add(entity);
        }
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

    @Override
    public void entityRemoved(Entity entity) {
<<<<<<< HEAD
        renderQueue.removeValue(entity,false);
=======
        if(getFamily().matches(entity)) {
            renderQueue.removeValue(entity,false);
        }
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

}
