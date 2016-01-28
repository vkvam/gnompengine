package com.flatfisk.gnomp.tests.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.Spatial;
import com.flatfisk.gnomp.tests.components.Player;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class CameraTrackerSystem extends IteratingSystem{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    //private Entity tracked;
    private Camera camera;
    private ComponentMapper<Spatial.Node> orientationRelativeComponentMapper;
    private boolean trackX,trackY;

    public CameraTrackerSystem(int priority, Camera camera, boolean trackX, boolean trackY) {
        super(Family.all(Player.class).get(),priority);
        orientationRelativeComponentMapper = ComponentMapper.getFor(Spatial.Node.class);
        //this.tracked = tracker;
        this.camera = camera;
        this.trackX = trackX;
        this.trackY = trackY;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector2 t = orientationRelativeComponentMapper.get(entity).world.vector;
        if(trackX){
            camera.position.x = t.x;
        }
        if(trackY){
            camera.position.y = t.y;
        }
    }
}
