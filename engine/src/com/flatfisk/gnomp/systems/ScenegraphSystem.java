package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.utils.Pools;
import com.flatfisk.gnomp.components.relatives.OrientationRelative;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphNode;
import com.flatfisk.gnomp.components.scenegraph.ScenegraphRoot;
import com.flatfisk.gnomp.math.Translation;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class ScenegraphSystem extends IteratingSystem {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ComponentMapper<ScenegraphNode> scenegraphNodeComponentMapper;
    private ComponentMapper<OrientationRelative> orientationRelativeComponentMapper;

    public ScenegraphSystem(int priority) {
        super(Family.all(ScenegraphRoot.class).get(), priority);
        scenegraphNodeComponentMapper = ComponentMapper.getFor(ScenegraphNode.class);
        orientationRelativeComponentMapper = ComponentMapper.getFor(OrientationRelative.class);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ScenegraphNode scenegraphNode = scenegraphNodeComponentMapper.get(entity);
        OrientationRelative parentOrientation = orientationRelativeComponentMapper.get(entity);
        for(Entity child : scenegraphNode.children){
            processChild(child,parentOrientation.worldTranslation);
        }
    }

    // TODO: Reduce number of translation copies as well as complexity of this function, name things properly!
    private void processChild(Entity entity,  Translation parentWorld){
        ScenegraphNode scenegraphNode = scenegraphNodeComponentMapper.get(entity);
        OrientationRelative orientationRelative = orientationRelativeComponentMapper.get(entity);

        boolean transferAngle = orientationRelative.inheritFromParentType.equals(OrientationRelative.TranslationInheritType.POSITION_ANGLE);

        OrientationRelative childOrientation = orientationRelativeComponentMapper.get(entity);
        Translation childLocalTranslation = childOrientation.localTranslation;
        Translation childWorldTranslation = childOrientation.worldTranslation;

        childWorldTranslation.set(Pools.obtainFromCopy(parentWorld.position),transferAngle?parentWorld.angle:0);
        childWorldTranslation.position.add(Pools.obtainFromCopy(childLocalTranslation.position).rotate(childWorldTranslation.angle));
        childWorldTranslation.angle += childLocalTranslation.angle;

        for(Entity child : scenegraphNode.children){
            processChild(child,childWorldTranslation);
        }
    }
}
