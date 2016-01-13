package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.components.StructureNode;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.shape.texture.TextureCoordinates;

/**
 * Calculates properties for StructureRoot such as worldTranslation and total area made by shapes.
 */
public class StructureSystem extends NodeSystem<StructureSystem.StructureDTO, StructureNode> {

    public ComponentMapper<ScenegraphNode> scenegraphNodeComponentMapper;
    public StructureSystem(int priority) {
        super(Family.all(Root.class,StructureNode.class/*, StructureRootComponent.class*/).get(), StructureNode.class, priority);
    }

    @Override
    public void addedToEngine(Engine engine){
        super.addedToEngine(engine);
        scenegraphNodeComponentMapper = ComponentMapper.getFor(ScenegraphNode.class);
    }

    @Override
    protected StructureDTO insertedRoot(Entity e) {
        System.out.println("Inserted root:" + e.hashCode());
        StructureNode structure = nodeMapper.get(e);
        Translation local = structure.localTranslation;
        Translation world = structure.worldTranslation;
        world.setCopy(local);

        StructureDTO dto = new StructureDTO();
        if (structure.shape != null) {
            dto.textureCoordinates = structure.shape.getTextureCoordinates(dto.textureCoordinates, world);
        }

        ScenegraphNode scenegraphNodeComponent = scenegraphNodeComponentMapper.get(e);
        if(scenegraphNodeComponent!=null){
            structure.scenegraphRoot = e;
            dto.sceneGraphRoot = e;
        }

        return dto;
    }

    @Override
    protected StructureDTO insertedChild(Entity e, StructureDTO dto) {
        System.out.println("Inserted child:"+e.hashCode());
        StructureNode structure = nodeMapper.get(e);
        Translation lTranslation = structure.localTranslation;
        Translation wTranslation = structure.worldTranslation;

        Entity parent = structure.parent;
        Translation parentWorldTranslation = nodeMapper.get(parent).worldTranslation;

        wTranslation.setCopy(parentWorldTranslation);
        wTranslation.position.add(lTranslation.position.cpy().rotate(parentWorldTranslation.angle));
        wTranslation.angle += lTranslation.angle;

        structure.scenegraphRoot = dto.sceneGraphRoot;

        if(structure.shape!=null) {
            dto.textureCoordinates = structure.shape.getTextureCoordinates(dto.textureCoordinates, wTranslation);
        }
        return dto;
    }

    @Override
    protected void finalInsertedRoot(Entity e, StructureDTO dto) {
        StructureNode structure = nodeMapper.get(e);
        structure.boundingRectangle = dto.textureCoordinates.getBoundingRectangle();
    }

    @Override
    protected void removedParent(Entity e) {
    }

    @Override
    protected void removedChild(Entity e) {
    }

    public class StructureDTO implements NodeSystem.IterateDTO {
        TextureCoordinates textureCoordinates = null;
        Entity sceneGraphRoot= null;
    }
}
