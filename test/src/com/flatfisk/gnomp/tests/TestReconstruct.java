package com.flatfisk.gnomp.tests;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.*;
import com.flatfisk.gnomp.components.abstracts.IRelative;
import com.flatfisk.gnomp.shape.CircleShape;
import com.flatfisk.gnomp.shape.RectangularLineShape;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.systems.CameraTrackerSystem;
import com.flatfisk.gnomp.systems.InputSystem;
import com.flatfisk.gnomp.systems.PhysicsSystem;
import com.flatfisk.gnomp.systems.RenderSystem;

public class TestReconstruct extends Test {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public TestReconstruct(ShapeTextureFactory shapeTextureFactory){
        this.shapeTextureFactory = shapeTextureFactory;
    }

    private final short CATEGORY_PLATFORM =     0x0001;  // 0000000000000001 in binary
    private final short CATEGORY_PLAYER =       0x0002; // 0000000000000100 in binary
    private final short CATEGORY_SENSOR =       0x0004; // 0000000000000010 in binary

    @Override
    public void create () {
        super.create();
        createSystems(new Vector2(0, -9.8f));

        world.getSystem(RenderSystem.class).getCamera().zoom = 1f;

        InputSystem inputSystem = new InputSystem(0,world.getSystem(PhysicsSystem.class).getBox2DWorld());
        world.addSystem(inputSystem);
        world.addEntityListener(inputSystem.getFamily(),0,inputSystem);

        world.addSystem(new CameraTrackerSystem(1,world.getSystem(RenderSystem.class).getCamera(),true,true));
        createGame(new com.flatfisk.gnomp.math.Spatial(0, -120, 0));
    }

    /*
            c0
        b0
    a
        b1
            c1

    c1 is removed and constructor is a, every node needs to be reconstructed.
    */

    private void createGame(com.flatfisk.gnomp.math.Spatial position){

        Entity platform = createPlatform(position,90,Color.GREEN,false);
        world.addComponent(Constructable.class,platform);

        int i;

        Entity character = createCharacter(new com.flatfisk.gnomp.math.Spatial(0,150,0),new com.flatfisk.gnomp.math.Spatial(0,0,0));
        platform.getComponent(Constructable.Node.class).addChild(character);

        Entity sensor = createSensor(new com.flatfisk.gnomp.math.Spatial(0,-10,0));
        character.getComponent(Constructable.Node.class).addChild(sensor);
        character.getComponent(Scenegraph.Node.class).addChild(sensor);

        Entity dot = character,dot2;
        for(i=0;i<5;i++) {

                dot2 = createCharacterDot(new com.flatfisk.gnomp.math.Spatial(17, 0, 0));
                dot.getComponent(Constructable.Node.class).addChild(dot2);
                world.addEntity(dot2);
                dot = dot2;
        }

        dot = character;
        for(i=0;i<5;i++) {

            dot2 = createCharacterDot(new com.flatfisk.gnomp.math.Spatial(-17, 0, 0));
            dot.getComponent(Constructable.Node.class).addChild(dot2);
            world.addEntity(dot2);
            dot = dot2;
        }

        world.addEntity(sensor);
        world.addEntity(character);
        world.addEntity(platform);
        world.constructEntity(platform);
    }

    protected Entity createSensor(com.flatfisk.gnomp.math.Spatial translation){


        Entity e = world.createEntity();

        Constructable.Node orientationRelative = world.addComponent(Constructable.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.relativeType = IRelative.Relative.CHILD;
        orientationRelative.inheritFromParentType = Constructable.Node.SpatialInheritType.POSITION;


        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);
        renderableRelative.relativeType = IRelative.Relative.PARENT;


        PhysicsBody.Node physicsBodyRelative = world.addComponent(PhysicsBody.Node.class,e);
        physicsBodyRelative.relativeType = IRelative.Relative.PARENT;

        Structure.Node structure = world.addComponent(Structure.Node.class,e);

        RectangularLineShape rectangularLineShape = new RectangularLineShape(1,4,Color.OLIVE,Color.BLUE);
        rectangularLineShape.from = new Vector2(-1.5f,0);
        rectangularLineShape.to = new Vector2(1.5f,0);
        rectangularLineShape.createPolygonVertices();

        structure.shape = rectangularLineShape;
        structure.density = 1;
        structure.friction = 5f;
        structure.relativeType = IRelative.Relative.PARENT;
        structure.isSensor = true;
        structure.categoryBits = CATEGORY_SENSOR;
        structure.maskBits = CATEGORY_PLATFORM;


        Renderable renderableDef = world.addComponent(Renderable.class,e);
        renderableDef.zIndex = -1;


        world.addComponent(PlayerSensor.class,e);
        world.addComponent(Scenegraph.Node.class,e);
        world.addComponent(Structure.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation=true;
        physicsBodyDef.bodyDef.angularDamping=.03f;

        return e;
    }

    protected Entity createCharacter(com.flatfisk.gnomp.math.Spatial translation, com.flatfisk.gnomp.math.Spatial velocity){

        Entity e = world.createEntity();

        Constructable.Node orientationRelative = world.addComponent(Constructable.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.relativeType = IRelative.Relative.CHILD;

        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);
        renderableRelative.relativeType = IRelative.Relative.PARENT;

        Velocity velocityComponent = world.addComponent(Velocity.class,e);
        velocityComponent.velocity = velocity;
        world.addComponent(Player.class,e);

        Structure.Node structure = world.addComponent(Structure.Node.class,e);
        CircleShape rectangularLineShape = new CircleShape(1,10,Color.WHITE,Color.FIREBRICK);
        structure.shape = rectangularLineShape;
        structure.density = 1;
        structure.friction = 5f;
        structure.relativeType = IRelative.Relative.PARENT;
        structure.categoryBits = CATEGORY_PLAYER;
        structure.maskBits = CATEGORY_PLATFORM;

        world.addComponent(Renderable.class,e);
        world.addComponent(Structure.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation=false;
        physicsBodyDef.bodyDef.angularDamping=0.5f;


        world.addComponent(Scenegraph.class,e);
        world.addComponent(Scenegraph.Node.class,e);

        PhysicsBody.Node physicsBodyRelative = world.addComponent(PhysicsBody.Node.class,e);
        physicsBodyRelative.relativeType = IRelative.Relative.PARENT;

        return e;
    }

    protected Entity createCharacterDot(com.flatfisk.gnomp.math.Spatial translation){

        Entity e = world.createEntity();

        world.addComponent(Dot.class,e);

        Constructable.Node orientationRelative = world.addComponent(Constructable.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.relativeType = IRelative.Relative.CHILD;
        orientationRelative.inheritFromParentType = Constructable.Node.SpatialInheritType.POSITION_ANGLE;

        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);
        renderableRelative.relativeType = IRelative.Relative.CHILD;

        Structure.Node structure = world.addComponent(Structure.Node.class,e);
        CircleShape rectangularLineShape = new CircleShape(1,3,Color.RED,Color.RED);
        structure.shape = rectangularLineShape;
        structure.density = 1;
        structure.friction = 5f;
        structure.relativeType = IRelative.Relative.CHILD;

        return e;
    }

    protected Entity createPlatform(com.flatfisk.gnomp.math.Spatial translation,float width,Color color, boolean hasVelocity){

        Entity e = world.createEntity();

        Constructable.Node orientationRelative = world.addComponent(Constructable.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;
        orientationRelative.relativeType = IRelative.Relative.PARENT;

        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);
        renderableRelative.relativeType = IRelative.Relative.PARENT;

        PhysicsBody.Node physicsBodyRelative = world.addComponent(PhysicsBody.Node.class,e);
        physicsBodyRelative.relativeType = IRelative.Relative.PARENT;

        Structure.Node structure = world.addComponent(Structure.Node.class,e);
        RectangularLineShape rectangularLineShape = new RectangularLineShape(1,(float) 5,Color.WHITE,color);
        rectangularLineShape.from = new Vector2(-width/2,0);
        rectangularLineShape.to = new Vector2(width/2,0);
        rectangularLineShape.createPolygonVertices();

        structure.shape = rectangularLineShape;
        structure.density = 0;
        structure.friction = 1;
        structure.categoryBits = CATEGORY_PLATFORM;
        structure.maskBits = CATEGORY_PLAYER|CATEGORY_SENSOR;
        structure.relativeType = IRelative.Relative.PARENT;

        world.addComponent(Renderable.class,e);
        world.addComponent(Structure.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        if(hasVelocity) {
            Velocity velocity = world.addComponent(Velocity.class,e);
            velocity.velocity = new com.flatfisk.gnomp.math.Spatial(0, 0, 6);
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.KinematicBody;
        }else{
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.StaticBody;
        }

        return e;
    }

}
