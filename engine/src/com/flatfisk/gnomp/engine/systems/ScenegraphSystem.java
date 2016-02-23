package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Scenegraph;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;
import static com.flatfisk.gnomp.engine.GnompMappers.*;


/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class ScenegraphSystem extends IteratingSystem {
    private final Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);


    public ScenegraphSystem(int priority) {
        super(Family.all(Scenegraph.class).get(), priority);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Scenegraph.Node scenegraphNode = scenegraphNodeMap.get(entity);
        Spatial.Node parentOrientation = spatialNodeMap.get(entity);
        for(Entity child : scenegraphNode.children){
            processChild(child,parentOrientation.world);
        }
    }

    private void processChild(Entity entity,  Transform parentWorld){
        if(entity!=null) {
            Scenegraph.Node scenegraphNode = scenegraphNodeMap.get(entity);
            Spatial.Node childOrientation = spatialNodeMap.get(entity);
            childOrientation.addParentTransform(parentWorld);

            for (Entity child : scenegraphNode.children) {
                processChild(child, childOrientation.world);
            }

        }else{
            LOG.info("Entity is null!");
        }
    }
}
