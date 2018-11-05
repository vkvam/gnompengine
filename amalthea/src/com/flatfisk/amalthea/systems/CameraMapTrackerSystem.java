package com.flatfisk.amalthea.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.components.Player;

import java.util.Random;

import static com.flatfisk.gnomp.engine.GnompMappers.spatialNodeMap;


/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class CameraMapTrackerSystem extends IteratingSystem{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private Camera camera;
    private boolean trackX,trackY;

    public CameraMapTrackerSystem(int priority, Camera camera, boolean trackX, boolean trackY) {
        super(Family.all(Player.class).get(),priority);
        //this.tracked = tracker;
        this.camera = camera;
        this.trackX = trackX;
        this.trackY = trackY;
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector2 t = spatialNodeMap.get(entity).world.vector;
        if(trackX)
            camera.position.x = t.x;
        if(trackY)
            camera.position.y = t.y;
    }
}
