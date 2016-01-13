package com.flatfisk.gnomp.systems;


<<<<<<< HEAD
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.OrientationRelative;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
import com.flatfisk.gnomp.components.roots.PhysicsJointDef;
import com.flatfisk.gnomp.math.Translation;

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
=======
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.PhysicsBody;
import com.flatfisk.gnomp.components.PhysicsJointConstruction;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.components.StructureNode;
import com.flatfisk.gnomp.math.Translation;

public class PhysicsJointConstructionSystem extends FamilySystem implements EntityListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public ComponentMapper<PhysicsJointConstruction> jointMapper;
    public ComponentMapper<PhysicsBody> bodyMapper;
    public ComponentMapper<StructureNode> structureNodeComponent;
    public ComponentMapper<ScenegraphNode> nodeMapper;

    public World box2DWorld;

    public PhysicsJointConstructionSystem(World box2DWorld, int priority) {
        super(Family.all(PhysicsJointConstruction.class).get(),priority);
        this.box2DWorld = box2DWorld;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        jointMapper = ComponentMapper.getFor(PhysicsJointConstruction.class);
        structureNodeComponent = ComponentMapper.getFor(StructureNode.class);
        bodyMapper = ComponentMapper.getFor(PhysicsBody.class);
        nodeMapper = ComponentMapper.getFor(ScenegraphNode.class);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
    }

    @Override
    public void entityAdded(Entity entity) {
<<<<<<< HEAD
        if(jointMapper.has(entity)){
            PhysicsJointDef jointComponent = jointMapper.get(entity);
=======
        if(getFamily().matches(entity)){
            PhysicsJointConstruction jointComponent = jointMapper.get(entity);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
            PhysicsBody bodyA = bodyMapper.get(jointComponent.a);
            PhysicsBody bodyB = bodyMapper.get(jointComponent.b);

            if(bodyA!=null && bodyB!=null) {
                RevoluteJointDef def = new RevoluteJointDef();
                def.type = jointComponent.jointType;

<<<<<<< HEAD
                OrientationRelative anchorStructureNode = orientationMapper.get(jointComponent.anchor);
                //Orientation anchorOrientation = anchorStructureNode;//orientationMapper.get(anchorStructureNode.scenegraphRoot);

                Translation anchor = anchorStructureNode.worldTranslation.getCopy();//anchorOrientation.localTranslation.getCopy();

                //anchor.position.add(anchorOrientation.worldTranslation.position);
=======
                StructureNode anchorStructureNode = structureNodeComponent.get(jointComponent.anchor);
                ScenegraphNode anchorScenegraphRoot = nodeMapper.get(anchorStructureNode.scenegraphRoot);

                Translation anchor = anchorScenegraphRoot.localTranslation.getCopy();
                anchor.position.add(anchorStructureNode.worldTranslation.position);
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
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
<<<<<<< HEAD
        if(jointMapper.has(entity)){
=======
        if(getFamily().matches(entity)){
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
            box2DWorld.destroyJoint(jointMapper.get(entity).joint);
        }
    }


}
