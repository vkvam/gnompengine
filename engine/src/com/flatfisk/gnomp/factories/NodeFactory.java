package com.flatfisk.gnomp.factories;


import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.core.Entity;
import com.flatfisk.gnomp.components.Node;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.exceptions.NodeConflictException;

public abstract class NodeFactory {
    protected Class<? extends Component> parent;
    protected Class<? extends Node> child;
    protected PooledEngine world;

    public NodeFactory(PooledEngine world, Class<? extends Node> child) {
        this.world = world;
        this.child = child;
        this.parent = Root.class;
    }

    public <T extends Node> T createNode(Class<T> type, Entity e) throws NodeConflictException {
        T child = world.createComponent(type);
        checkNodeIntegrity(e, child);
        addStandardProperties(child, e);
        return child;
    }

    public <T extends Component> T createRoot(Class<T> type, Entity e) throws NodeConflictException {
        T child = world.createComponent(type);
        checkNodeIntegrity(e, child);
        return child;
    }

    private void addStandardProperties(Node node, Entity e) {
        node.owner = e;
    }

    protected void checkNodeIntegrity(Entity entity, Component toAdd) throws NodeConflictException {
        if (entity.getComponent(toAdd.getClass()) != null) {
            throw new NodeConflictException("Entity already has node type:" + toAdd.getClass().getName());
        }
    }

}
