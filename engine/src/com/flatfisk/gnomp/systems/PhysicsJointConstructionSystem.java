package com.flatfisk.gnomp.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.components.roots.PhysicsJointDef;
import com.flatfisk.gnomp.math.Spatial;

public class PhysicsJointConstructionSystem implements EntityListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public ComponentMapper<PhysicsJointDef> jointMapper;
    public ComponentMapper<PhysicsBody> bodyMapper;
    public ComponentMapper<StructureRelative> structureNodeComponent;
    public ComponentMapper<SpatialRelative> orientationMapper;

    public World box2DWorld;

    public PhysicsJointConstructionSystem(World box2DWorld) {
        this.box2DWorld = box2DWorld;
        jointMapper = ComponentMapper.getFor(PhysicsJointDef.class);
        structureNodeComponent = ComponentMapper.getFor(StructureRelative.class);
        bodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        orientationMapper = ComponentMapper.getFor(SpatialRelative.class);
    }

    @Override
    public void entityAdded(Entity entity) {
        if(jointMapper.has(entity)){
            PhysicsJointDef jointComponent = jointMapper.get(entity);
            PhysicsBody bodyA = bodyMapper.get(jointComponent.a);
            PhysicsBody bodyB = bodyMapper.get(jointComponent.b);

            if(bodyA!=null && bodyB!=null) {
                RevoluteJointDef def = new RevoluteJointDef();
                def.type = jointComponent.jointType;

                SpatialRelative anchorStructureNode = orientationMapper.get(jointComponent.anchor);
                //Orientation anchorOrientation = anchorStructureNode;//spatialRelativeComponentMapper.get(anchorStructureNode.scenegraphRoot);

                Spatial anchor = anchorStructureNode.world.getCopy();//anchorOrientation.localTranslation.getCopy();

                //anchor.vector.add(anchorOrientation.worldTranslation.vector);
                anchor.vector.add(jointComponent.anchorOffset.vector);

                anchor.toBox2D();

                def.initialize(bodyA.body,bodyB.body,anchor.vector);
                def.collideConnected = false;
                jointComponent.joint = box2DWorld.createJoint(def);
            }
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if(jointMapper.has(entity)){
            box2DWorld.destroyJoint(jointMapper.get(entity).joint);
        }
    }


}
