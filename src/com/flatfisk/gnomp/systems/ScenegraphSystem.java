package com.flatfisk.gnomp.systems;

<<<<<<< HEAD
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
=======

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.math.Translation;

public class ScenegraphSystem extends IteratingNodeSystem<ScenegraphSystem.EmptyDTO, Root, ScenegraphNode> {


    public ScenegraphSystem(int priority) {
        super(Family.all(Root.class).get(), Root.class, ScenegraphNode.class,priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    @Override
    public EmptyDTO processRoot(Entity e) {
        ScenegraphNode a = nodeMapper.get(e);
        Translation local = a.localTranslation;
        Translation world = a.worldTranslation;
        world.setCopy(local);
        return null;
    }

    @Override
    public EmptyDTO processChild(Entity eChild, EmptyDTO dto) {
        ScenegraphNode a = nodeMapper.get(eChild);
        Translation lTranslation = a.localTranslation;
        Translation wTranslation = a.worldTranslation;
        Translation parentWorldTranslation;
        parentWorldTranslation = nodeMapper.get(a.parent).worldTranslation;
        wTranslation.setCopy(parentWorldTranslation);
        wTranslation.position.add(lTranslation.position.cpy().rotate(parentWorldTranslation.angle));
        wTranslation.angle += lTranslation.angle;
        return null;
    }

    @Override
    protected EmptyDTO insertedChild(Entity e, EmptyDTO dto) {
        return null;
    }

    @Override
    protected void finalInsertedRoot(Entity e, EmptyDTO dto) {

    }

    @Override
    protected void removedChild(Entity e) {
    }

    public class EmptyDTO implements NodeSystem.IterateDTO {
    }

>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
}
