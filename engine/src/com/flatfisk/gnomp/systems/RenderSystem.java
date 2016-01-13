package com.flatfisk.gnomp.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Renderable;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.gdx.GdxSystem;
import com.flatfisk.gnomp.math.Translation;

public class RenderSystem extends FamilySystem implements GdxSystem, EntityListener {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public ComponentMapper<Renderable> renderableMapper;
    public ComponentMapper<ScenegraphNode> scenegraphNodeMapper;
    private SpriteBatch batch;
    private OrthographicCamera orthographicCamera;
    private Array<Entity> renderQueue = new Array<Entity>();

    public RenderSystem(int priority) {
        super(Family.all(Renderable.class, ScenegraphNode.class).get(),priority);
        batch = new SpriteBatch();
        orthographicCamera = new OrthographicCamera(640, 480);
    }

    @Override
    public void addedToEngine(Engine engine) {
        renderableMapper = ComponentMapper.getFor(Renderable.class);
        scenegraphNodeMapper = ComponentMapper.getFor(ScenegraphNode.class);
    }

    public OrthographicCamera getCamera() {
        return orthographicCamera;
    }

    @Override
    public void update(float f) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        orthographicCamera.update();
        batch.setProjectionMatrix(orthographicCamera.combined);
        batch.begin();

        for (Entity e : renderQueue) {
            Renderable renderable = renderableMapper.get(e);
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
                        tWDiv2,                             // float originX
                        tHDiv2,                             // float originY
                        tW,                                 // float width
                        tH,                                 // float height
                        1,                                  // float scaleX
                        1,                                  // float scaleY
                        translation.angle,                  // float rotation
                        0,                                  // int srcX
                        0,                                  // int srcY
                        tW,                                 // int srcWidth
                        tH,                                 // int srcHeight
                        false,                              // boolean flipX
                        true                                // boolean flipY
                );
        }


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
        if(getFamily().matches(entity)) {
            renderQueue.add(entity);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if(getFamily().matches(entity)) {
            renderQueue.removeValue(entity,false);
        }
    }

}
