package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphNode;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphRoot;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.utils.Pools;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class ScenegraphSystem extends IteratingSystem {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ComponentMapper<ScenegraphNode> scenegraphNodeComponentMapper;
    private ComponentMapper<SpatialRelative> orientationRelativeComponentMapper;

    public ScenegraphSystem(int priority) {
        super(Family.all(ScenegraphRoot.class).get(), priority);
        scenegraphNodeComponentMapper = ComponentMapper.getFor(ScenegraphNode.class);
        orientationRelativeComponentMapper = ComponentMapper.getFor(SpatialRelative.class);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ScenegraphNode scenegraphNode = scenegraphNodeComponentMapper.get(entity);
        SpatialRelative parentOrientation = orientationRelativeComponentMapper.get(entity);
        for(Entity child : scenegraphNode.children){
            processChild(child,parentOrientation.worldSpatial);
        }
    }

    private void processChild(Entity entity,  Spatial parentWorld){
        ScenegraphNode scenegraphNode = scenegraphNodeComponentMapper.get(entity);
        SpatialRelative orientationRelative = orientationRelativeComponentMapper.get(entity);

        boolean transferAngle = orientationRelative.inheritFromParentType.equals(SpatialRelative.SpatialInheritType.POSITION_ANGLE);

        SpatialRelative childOrientation = orientationRelativeComponentMapper.get(entity);
        Spatial childLocal = childOrientation.localSpatial;
        Spatial childWorld = childOrientation.worldSpatial;

        childWorld.set(Pools.obtainVector2FromCopy(parentWorld.vector), transferAngle ? parentWorld.rotation : 0);
        childWorld.vector.add(Pools.obtainVector2FromCopy(childLocal.vector).rotate(childWorld.rotation));
        childWorld.rotation += childLocal.rotation;

        for(Entity child : scenegraphNode.children){
            processChild(child, childWorld);
        }
    }
}
