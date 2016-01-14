package com.github.vkvam.gnompengine.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Logger;
import com.github.vkvam.gnompengine.components.constructed.PhysicsBody;
import com.github.vkvam.gnompengine.components.relatives.OrientationRelative;
import com.github.vkvam.gnompengine.components.relatives.StructureRelative;
import com.github.vkvam.gnompengine.components.roots.PhysicsJointDef;
import com.github.vkvam.gnompengine.math.Translation;

public class PhysicsJointConstructionSystem implements EntityListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public ComponentMapper<PhysicsJointDef> jointMapper;
    public ComponentMapper<PhysicsBody> bodyMapper;
    public ComponentMapper<StructureRelative> structureNodeComponent;
    public ComponentMapper<OrientationRelative> orientationMapper;

    public World box2DWorld;

    public PhysicsJointConstructionSystem(World box2DWorld) {
        this.box2DWorld = box2DWorld;
        jointMapper = ComponentMapper.getFor(PhysicsJointDef.class);
        structureNodeComponent = ComponentMapper.getFor(StructureRelative.class);
        bodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        orientationMapper = ComponentMapper.getFor(OrientationRelative.class);
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

                OrientationRelative anchorStructureNode = orientationMapper.get(jointComponent.anchor);
                //Orientation anchorOrientation = anchorStructureNode;//orientationMapper.get(anchorStructureNode.scenegraphRoot);

                Translation anchor = anchorStructureNode.worldTranslation.getCopy();//anchorOrientation.localTranslation.getCopy();

                //anchor.position.add(anchorOrientation.worldTranslation.position);
                anchor.position.add(jointComponent.anchorOffset.position);

                anchor.toBox2D();

                def.initialize(bodyA.body,bodyB.body,anchor.position);
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
