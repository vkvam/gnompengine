package com.flatfisk.amalthea.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.components.Player;

import java.util.Random;

import static com.flatfisk.gnomp.engine.GnompMappers.spatialNodeMap;


/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class CameraTrackerSystem extends IteratingSystem{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    //private Entity tracked;
    private Camera camera;
    private boolean trackX,trackY;
    private float shakeTime = 0, shakePower=0;
    private Random random = new Random();

    public CameraTrackerSystem(int priority, Camera camera, boolean trackX, boolean trackY) {
        super(Family.all(Player.class).get(),priority);
        //this.tracked = tracker;
        this.camera = camera;
        this.trackX = trackX;
        this.trackY = trackY;
    }

    public void shake(float time,float power){
        shakeTime = random.nextFloat()*0.5f*time+time*0.5f;
        shakePower = power;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector2 t = spatialNodeMap.get(entity).world.vector;

        if(shakeTime>0){
            shakeTime-=deltaTime;
        }
        if(trackX){
            camera.position.x = t.x;
            if(shakeTime>0){
                camera.position.x+=shakePower*(random.nextFloat()-0.5);
            }
        }
        if(trackY){
            camera.position.y = t.y;
            if(shakeTime>0){
                camera.position.y+=shakePower*(random.nextFloat()-0.5);
            }
        }
    }
}
