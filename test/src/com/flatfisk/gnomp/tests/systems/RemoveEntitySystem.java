package com.flatfisk.gnomp.tests.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.message.Message;

import static com.flatfisk.gnomp.engine.GnompMappers.physicsBodyMap;
import static com.flatfisk.gnomp.engine.GnompMappers.scenegraphNodeMap;
import static com.flatfisk.gnomp.engine.GnompMappers.spatialNodeMap;

/**
* Created by Vemund Kvam on 20/02/16.
*/
public class RemoveEntitySystem extends IteratingSystem {
    private float c = 0;
    public RemoveEntitySystem() {
        super(Family.all(PhysicsBody.Node.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {

        c+=v;
        Spatial.Node b = spatialNodeMap.get(entity);
        if(b!=null){
            b.local.rotation=c/10f;
        }

    }
}
