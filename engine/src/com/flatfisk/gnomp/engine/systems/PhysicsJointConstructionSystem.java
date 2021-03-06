package com.flatfisk.gnomp.engine.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.components.PhysicsBody;

public class PhysicsJointConstructionSystem implements EntityListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    //public ComponentMapper<PhysicsJointDef> jointMapper;
    public ComponentMapper<PhysicsBody.Container> bodyMapper;
    public ComponentMapper<Shape> structureNodeComponent;
    public ComponentMapper<Spatial.Node> orientationMapper;

    public World box2DWorld;

    public PhysicsJointConstructionSystem(World box2DWorld) {
        this.box2DWorld = box2DWorld;
        //jointMapper = ComponentMapper.getFor(PhysicsJointDef.class);
        structureNodeComponent = ComponentMapper.getFor(Shape.class);
        bodyMapper = ComponentMapper.getFor(PhysicsBody.Container.class);
        orientationMapper = ComponentMapper.getFor(Spatial.Node.class);
    }

    @Override
    public void entityAdded(Entity entity) {
        /*
        if(jointMapper.has(entity)){
            PhysicsJointDef jointComponent = jointMapper.get(entity);
            PhysicsBodyContainer bodyA = bodyMapper.get(jointComponent.a);
            PhysicsBodyContainer bodyB = bodyMapper.get(jointComponent.b);

            if(bodyA!=null && bodyB!=null) {
                RevoluteJointDef def = new RevoluteJointDef();
                def.type = jointComponent.jointType;

                SpatialNode anchorStructureNode = orientationMapper.get(jointComponent.anchor);
                //Orientation anchorOrientation = anchorStructureNode;//spatialRelativeComponentMapper.get(anchorStructureNode.scenegraphRoot);

                Spatial anchor = anchorStructureNode.engine.getPooledCopy();//anchorOrientation.localTranslation.getPooledCopy();

                //anchor.vector.add(anchorOrientation.worldTranslation.vector);
                anchor.vector.add(jointComponent.anchorOffset.vector);

                anchor.toBox2D();

                def.initialize(bodyA.body,bodyB.body,anchor.vector);
                def.collideConnected = false;
                jointComponent.joint = box2DWorld.createJoint(def);
            }
        }
        */
    }

    @Override
    public void entityRemoved(Entity entity) {
        /*
        if(jointMapper.has(entity)){
            box2DWorld.destroyJoint(jointMapper.get(entity).joint);
        }
        */
    }


}
