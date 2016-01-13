package com.flatfisk.gnomp.systems;


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

}
