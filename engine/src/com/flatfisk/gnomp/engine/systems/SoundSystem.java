package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.SoundComponent;
import com.flatfisk.gnomp.engine.components.Spatial;

import java.util.HashMap;
import java.util.Map;

/**
 * Controls the lifetime of entities.
 * <p>
 * When lifeTime reaches zero, it is removed from the engine.
 */
public class SoundSystem extends IteratingSystem implements EntityListener, ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(), Logger.ERROR);

    private final ComponentMapper<SoundComponent> soundComponentMapper = ComponentMapper.getFor(SoundComponent.class);
    private Map<String, SoundWithCounter> soundMap = new HashMap<String, SoundWithCounter>();

    public SoundSystem(int priority) {
        super(Family.all(SoundComponent.class).get(), priority);
        soundMap = new HashMap<String, SoundWithCounter>(8);
    }

    private Vector3 screenPosition = new Vector3(1,1,1);

    private float cappedX(Vector3 v ){
        float capX = 0.7f;
        if(v.x>capX){
            v.x = capX;
        }else if(v.x < -capX){
            v.x = -capX;
        }
        return v.x;
    }

    @Override
    public void processEntity(Entity entity, float f) {
        SoundComponent s = soundComponentMapper.get(entity);
        if (s.play) {
            Sound sound = soundMap.get(s.soundfile).b;
            Vector2 n = entity.getComponent(Spatial.Node.class).world.vector;

            screenPosition.x = n.x;
            screenPosition.y = n.y;

            screenPosition = getEngine().getSystem(CameraSystem.class).getWorldCamera().project(
                    screenPosition,-1,-1,2,2
            );

            float distance = (3-screenPosition.len())/3;
            if(distance>0) {
                float x = cappedX(screenPosition);
                sound.play(distance * s.volume, s.pitch, x);
                s.play = false;
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        SoundComponent s = soundComponentMapper.get(entity);

        if (s.soundfile.equals("")) {
            return;
        }
        if (!soundMap.containsKey(s.soundfile)) {
            soundMap.put(s.soundfile, new SoundWithCounter(0, Gdx.audio.newSound(
                    Gdx.files.internal(s.soundfile))
            ));
        } else {
            soundMap.get(s.soundfile).a += 1;
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        SoundComponent s = soundComponentMapper.get(entity);
        SoundWithCounter t = soundMap.get(s.soundfile);
        if (t.a == 0) {
            t.b.dispose();
            soundMap.remove(s.soundfile);
        } else {
            t.a -= 1;
        }
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

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

    }

    public static class SoundWithCounter {
        public Integer a;
        public Sound b;

        public SoundWithCounter(Integer a, Sound b) {
            this.a = a;
            this.b = b;
        }
    }
}


