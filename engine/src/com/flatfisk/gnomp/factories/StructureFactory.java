package com.flatfisk.gnomp.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.components.StructureNode;
import com.flatfisk.gnomp.exceptions.NodeConflictException;

public class StructureFactory extends NodeFactory {

    public StructureFactory(PooledEngine world) {
        super(world, StructureNode.class);
    }

    public Entity createParentNode() throws NodeConflictException {
        Entity e = world.createEntity();
        addParentComponent(e);
        return e;
    }

    public void addParentComponent(Entity e) throws NodeConflictException {
        Root root = createRoot(Root.class, e);
        e.add(root);
    }

    public Entity createChildNode() throws NodeConflictException {
        Entity e = world.createEntity();
        addChildComponent(e);
        return e;
    }

    public void addChildComponent(Entity e) throws NodeConflictException {
        StructureNode child = createNode(StructureNode.class, e);
        e.add(child);
    }
}
