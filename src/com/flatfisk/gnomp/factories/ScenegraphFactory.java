package com.flatfisk.gnomp.factories;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.exceptions.NodeConflictException;

public class ScenegraphFactory extends NodeFactory {

    public ScenegraphFactory(PooledEngine world) {
        super(world, ScenegraphNode.class);
    }

    public Entity createParentNode() throws NodeConflictException {
        Entity e = world.createEntity();
        addParentComponent(e);
        return e;
    }

    public void addParentComponent(Entity e) throws NodeConflictException {
        Root root = world.createComponent(Root.class);
        e.add(root);
    }

    public Entity createNode(float x, float y, float angle) throws NodeConflictException {
        Entity e = world.createEntity();
        addChildComponent(e, x, y, angle);
        return e;
    }

    public void addChildComponent(Entity e, float x, float y, float angle) throws NodeConflictException {
        ScenegraphNode child = createNode(ScenegraphNode.class, e);
        child.localTranslation = new Translation(x, y, angle);
        e.add(child);
    }
}
