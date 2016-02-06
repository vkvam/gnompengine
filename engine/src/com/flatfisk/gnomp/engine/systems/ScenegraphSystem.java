package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Scenegraph;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class ScenegraphSystem extends IteratingSystem {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ComponentMapper<Scenegraph.Node> scenegraphNodeComponentMapper;
    private ComponentMapper<Spatial.Node> orientationRelativeComponentMapper;

    public ScenegraphSystem(int priority) {
        super(Family.all(Scenegraph.class).get(), priority);
        scenegraphNodeComponentMapper = ComponentMapper.getFor(Scenegraph.Node.class);
        orientationRelativeComponentMapper = ComponentMapper.getFor(Spatial.Node.class);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Scenegraph.Node scenegraphNode = scenegraphNodeComponentMapper.get(entity);
        Spatial.Node parentOrientation = orientationRelativeComponentMapper.get(entity);
        for(Entity child : scenegraphNode.children){
            processChild(child,parentOrientation.world);
        }
    }

    private void processChild(Entity entity,  Transform parentWorld){
        if(entity!=null) {
            Scenegraph.Node scenegraphNode = scenegraphNodeComponentMapper.get(entity);
            Spatial.Node orientationRelative = orientationRelativeComponentMapper.get(entity);
            if(orientationRelative!=null) {
                boolean transferAngle = orientationRelative.inheritFromParentType.equals(Spatial.Node.SpatialInheritType.POSITION_ANGLE);

                Spatial.Node childOrientation = orientationRelativeComponentMapper.get(entity);
                Transform childLocal = childOrientation.local;
                Transform childWorld = childOrientation.world;

                childWorld.set(parentWorld.vector, transferAngle ? parentWorld.rotation : 0);

                Vector2 localVectorWorldRotated = com.badlogic.gdx.utils.Pools.obtain(Vector2.class);
                localVectorWorldRotated.set(childLocal.vector);
                localVectorWorldRotated.rotate(childWorld.rotation);
                childWorld.vector.add(localVectorWorldRotated);
                childWorld.rotation += childLocal.rotation;

                for (Entity child : scenegraphNode.children) {
                    processChild(child, childWorld);
                }
            }
        }else{
            LOG.info("Entity is null!");
        }
    }
}
