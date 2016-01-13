package com.flatfisk.gnomp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.*;
import com.flatfisk.gnomp.exceptions.NodeConflictException;
import com.flatfisk.gnomp.factories.StructureFactory;
import com.flatfisk.gnomp.gdx.DefaultGnompEngineApplicationListener;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.shape.CircleShape;
import com.flatfisk.gnomp.shape.RectangularLineShape;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.systems.PhysicsConstructionSystem;
import com.flatfisk.gnomp.systems.PhysicsJointConstructionSystem;
import com.flatfisk.gnomp.systems.PhysicsSystem;

public class GnompEngineTestRenderPhysics extends DefaultGnompEngineApplicationListener {

    private EntityFactory entityFactory;
    private PhysicsJointConstructionSystem physicsJointSystem;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public GnompEngineTestRenderPhysics(ShapeTextureFactory shapeTextureFactory){
        this.shapeTextureFactory = shapeTextureFactory;
    }

    @Override
    public void create () {
        super.create();


        PhysicsConstructionSystem physicsConstructor = new PhysicsConstructionSystem(10);
        world.addSystem(physicsConstructor);
        world.addEntityListener(10,physicsConstructor);

        renderer.getCamera().zoom = 2f;

        World box2DWorld = new World(new Vector2(0, -9.8f), true);

        PhysicsSystem physics = new PhysicsSystem(box2DWorld,20);
        world.addSystem(physics);
        world.addEntityListener(20,physics);

        physicsJointSystem = new PhysicsJointConstructionSystem(box2DWorld,300);
        world.addSystem(physicsJointSystem);
        world.addEntityListener(300,physicsJointSystem);

        //world.addSystem(new TurnSystem(300));
        /*
        PhysicsDebugRenderer debugRenderer = new PhysicsDebugRenderer(renderer.getCamera(),physics.getBox2DWorld(),1002);
        debugRenderer.setClearScreen(false);
        world.addSystem(debugRenderer);
        */
        try {
            int dist = 100;
            for(int i=0;i<100;i++) {
                createCharacter(new Vector2(0, dist*i));
            }

            Entity floor = entityFactory.createFloorEntity();
            world.addEntity(floor);

        } catch (NodeConflictException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render () {
        world.update(Gdx.graphics.getDeltaTime());
    }

    public void createCharacter(Vector2 position) throws NodeConflictException {

        entityFactory = new EntityFactory(world);

        // Build all entities
        Entity character = entityFactory.createCharacterTorso(position);

        Entity arm0 = entityFactory.createCharacterLimb(new Translation(new Vector2(30, 0), 0),5);
        Entity arm1 = entityFactory.createCharacterLimb(new Translation(new Vector2(-30, 0), 0),5);

        Entity hand0 = entityFactory.createCharacterHand(new Translation(position.cpy().add(40, 0), 0),7);
        Entity hand1 = entityFactory.createCharacterHand(new Translation(position.cpy().add(-40,0), 0),7);

        Entity foot0 = entityFactory.createCharacterLimb(new Translation(new Vector2(-10, -35), 0),9);
        Entity foot1 = entityFactory.createCharacterLimb(new Translation(new Vector2(10, -35), 0),9);

        Entity head = entityFactory.createCharacterLimb(new Translation(new Vector2(0, 30), 0),30);

        Entity eye1 = entityFactory.createCharacterLimb(new Translation(new Vector2(-6, 6), 0),2.5f);
        Entity eye2 = entityFactory.createCharacterLimb(new Translation(new Vector2(6, 6), 0),2.5f);

        Entity joint0 = entityFactory.createJoint(character,hand0,arm0,new Translation(new Vector2(2,0),0));
        Entity joint1 = entityFactory.createJoint(character,hand1,arm1,new Translation(new Vector2(-2,0),0));

        // Create all relations
        StructureNode characterStructureNode = character.getComponent(StructureNode.class);
        characterStructureNode.addChild(arm0);
        characterStructureNode.addChild(arm1);

        characterStructureNode.addChild(head);
        characterStructureNode.addChild(foot0);
        characterStructureNode.addChild(foot1);

        StructureNode headStructureNode = head.getComponent(StructureNode.class);

        headStructureNode.addChild(eye1);
        headStructureNode.addChild(eye2);

        // Add all entities to world.
        world.addEntity(arm0);
        world.addEntity(arm1);

        world.addEntity(character);

        // Needs to be added before joints, otherwise joints won't have a hand-body to attach to.
        world.addEntity(hand1);
        world.addEntity(hand0);

        world.addEntity(joint0);
        world.addEntity(joint1);

        LOG.info("# entities:" + world.getEntities().size());
    }

    public class EntityFactory extends StructureFactory {

        public EntityFactory(PooledEngine world) {
            super(world);
        }

        public Entity createFloorEntity() throws NodeConflictException {
            Entity e = super.createParentNode();

            PhysicsConstruction physicalComponent = world.createComponent(PhysicsConstruction.class);
            physicalComponent.bodyType = BodyDef.BodyType.StaticBody;

            RectangularLineShape shape = new RectangularLineShape(2, 5, Color.WHITE, Color.BLUE);
            shape.from=new Vector2(-300,-200);
            shape.to = new Vector2(300,-200);
            shape.createPolygonVertices();

            StructureNode node = createNode(StructureNode.class, e);
            node.localTranslation = new Translation(0,0,0);
            node.worldTranslation = new Translation(0,0,0);
            node.shape = shape;

            addRenderableComponent(e);

            Root floorRootNode = world.createComponent(Root.class);
            ScenegraphNode floorNode = world.createComponent(ScenegraphNode.class);
            floorNode.localTranslation = new Translation(0,-30,0);

            e.add(floorRootNode);
            e.add(floorNode);
            e.add(node);
            e.add(physicalComponent);

            return e;
        }

        public Entity createCharacterTorso(Vector2 position) throws NodeConflictException {
            Entity e = super.createParentNode();

            PhysicsConstruction physics = world.createComponent(PhysicsConstruction.class);
            physics.bodyType = BodyDef.BodyType.DynamicBody;

            RectangularLineShape shape = new RectangularLineShape(2, 15, Color.WHITE, Color.BLUE);
            shape.from=new Vector2(0,-15);
            shape.to = new Vector2(0,15);
            shape.createPolygonVertices();

            StructureNode node = createNode(StructureNode.class, e);
            node.localTranslation = new Translation(0,0,0);
            node.worldTranslation = new Translation(0,0,0);
            node.shape = shape;
            node.density = 10;

            ScenegraphNode characterNode = world.createComponent(ScenegraphNode.class);
            characterNode.localTranslation = new Translation(position,0);
            characterNode.worldTranslation = new Translation(0,0,0);
            characterNode.velocity = new Translation(0,0,0);
            characterNode.owner = e;
            e.add(characterNode);

            addRenderableComponent(e);
            e.add(node);
            e.add(physics);
            return e;
        }

        public Entity createCharacterLimb(Translation translation,float size) throws NodeConflictException {
            Entity e = createChildNode();

            StructureNode node = e.getComponent(StructureNode.class);
            node.density = 10;
            node.worldTranslation = new Translation();
            node.shape = new CircleShape(1, size, Color.WHITE, Color.FIREBRICK);
            node.localTranslation = translation;
            e.add(node);

            PhysicsConstruction physics = world.createComponent(PhysicsConstruction.class);
            physics.bodyType = BodyDef.BodyType.DynamicBody;
            e.add(physics);
            addRenderableComponent(e);

            return e;
        }

        public Entity createJointAnchor(Translation translation) throws NodeConflictException {
            Entity e = createChildNode();

            StructureNode node = e.getComponent(StructureNode.class);
            node.density = 10;
            node.worldTranslation = new Translation();
            node.localTranslation=translation;

            e.add(node);
            return e;
        }

        public Entity createCharacterHand(Translation translation,float size) throws NodeConflictException {
            Entity e = createChildNode();

            StructureNode node = e.getComponent(StructureNode.class);
            node.density = 10;
            node.worldTranslation = new Translation();
            node.shape = new CircleShape(1, size, Color.WHITE, Color.FIREBRICK);
            node.localTranslation=new Translation(new Vector2(0,0),0);

            Root scenegraphRootComponent = world.createComponent(Root.class);

            ScenegraphNode scenegraphNodeComponent = world.createComponent(ScenegraphNode.class);
            scenegraphNodeComponent.localTranslation = translation;
            scenegraphNodeComponent.velocity = new Translation(0,0,0);
            scenegraphNodeComponent.owner = e;

            PhysicsConstruction physicsConstructionComponent = world.createComponent(PhysicsConstruction.class);
            physicsConstructionComponent.bodyType = BodyDef.BodyType.DynamicBody;
            e.add(physicsConstructionComponent);

            e.add(scenegraphRootComponent);
            e.add(scenegraphNodeComponent);

            e.add(node);
            addRenderableComponent(e);
            return e;
        }

        public Entity createJoint(Entity bodyA, Entity bodyB, Entity anchor, Translation anchorOffset) throws NodeConflictException {

            PhysicsJointConstruction jointComponent = new PhysicsJointConstruction();
            jointComponent.jointType = JointDef.JointType.RevoluteJoint;
            jointComponent.a = bodyA;
            jointComponent.b = bodyB;
            jointComponent.anchor = anchor;
            jointComponent.anchorOffset = anchorOffset;

            Entity jointEntity = world.createEntity();
            jointEntity.add(jointComponent);
            return jointEntity;
        }

        public RenderConstruction addRenderableComponent(Entity e) {
            RenderConstruction s = world.createComponent(RenderConstruction.class);
            e.add(s);
            return s;
        }

    }

}
