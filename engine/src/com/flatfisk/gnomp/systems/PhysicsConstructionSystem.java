package com.flatfisk.gnomp.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.PhysicsBody;
import com.flatfisk.gnomp.components.PhysicsConstruction;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.components.StructureNode;

public class PhysicsConstructionSystem extends NodeSystem<PhysicsBody, StructureNode>{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    public ComponentMapper<PhysicsConstruction> structurePhysicalMapper;

    public PhysicsConstructionSystem(int priority) {
        super(Family.all(Root.class, PhysicsConstruction.class).get(), StructureNode.class, priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        structurePhysicalMapper = ComponentMapper.getFor(PhysicsConstruction.class);
    }

    @Override
    protected PhysicsBody insertedRoot(Entity entity) {
        LOG.info("Inserting root");
        PhysicsBody bodyContainer = ((PooledEngine ) getEngine()).createComponent(PhysicsBody.class);
        bodyContainer.addBodyDef(structurePhysicalMapper.get(entity));
        bodyContainer.addFixtures(nodeMapper.get(entity));

        return bodyContainer;
    }

    @Override
    protected PhysicsBody insertedChild(Entity e, PhysicsBody dto) {
        Gdx.app.log(getClass().getName(), "Inserting child");
        if (structurePhysicalMapper.has(e)) {
            dto.addFixtures(nodeMapper.get(e));
        }
        return dto;
    }

    @Override
    protected void finalInsertedRoot(Entity e, PhysicsBody dto) {
        e.add(dto);
    }

    @Override
    protected void removedParent(Entity e) {
    }

    @Override
    protected void removedChild(Entity e) {

    }

}
