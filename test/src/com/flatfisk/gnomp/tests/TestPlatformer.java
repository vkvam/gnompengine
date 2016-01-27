package com.flatfisk.gnomp.tests;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.components.*;
import com.flatfisk.gnomp.components.abstracts.IRelative;
import com.flatfisk.gnomp.math.Spatial;
import com.flatfisk.gnomp.shape.CircleShape;
import com.flatfisk.gnomp.shape.RectangularLineShape;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.systems.CameraTrackerSystem;
import com.flatfisk.gnomp.systems.InputSystem;
import com.flatfisk.gnomp.systems.PhysicsSystem;
import com.flatfisk.gnomp.systems.RenderSystem;

public class TestPlatformer extends Test {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public TestPlatformer(ShapeTextureFactory shapeTextureFactory){
        this.shapeTextureFactory = shapeTextureFactory;
    }

    private final short CATEGORY_PLATFORM =     0x0001;  // 0000000000000001 in binary
    private final short CATEGORY_PLAYER =       0x0002; // 0000000000000100 in binary
    private final short CATEGORY_SENSOR =       0x0004; // 0000000000000010 in binary

    @Override
    public void create () {
        super.create();
        PhysicsConstants.setPixelsPerMeter(100);
        createSystems(new Vector2(0, -1000f * PhysicsConstants.METERS_PER_PIXEL));

        world.getSystem(RenderSystem.class).getCamera().zoom = 1f;

        InputSystem inputSystem = new InputSystem(0,world.getSystem(PhysicsSystem.class).getBox2DWorld());
        world.addSystem(inputSystem);
        world.addEntityListener(inputSystem.getFamily(),0,inputSystem);

        world.addSystem(new CameraTrackerSystem(1,world.getSystem(RenderSystem.class).getCamera(),true,true));
        createGame(new Spatial(0, -120, 0));
    }

    private void createGame(Spatial position){

        Entity platform = createPlatform(position,90,Color.GREEN,false);
        world.addComponent(Constructor.class,platform);

        Entity platform2 = createPlatform(new Spatial(-1000,-200,0),3000,Color.RED,false);
        world.addComponent(Constructor.class,platform2);
        world.addEntity(platform2);
        world.constructEntity(platform2);

        int i=1;
        for(;i<10;i++) {
            platform2 = createPlatform(new Spatial(-150*i, -100+i*15, -i*4-90), 40,Color.ORANGE,true);
            world.addComponent(Constructor.class,platform2);
            world.addEntity(platform2);
            world.constructEntity(platform2);
        }

        platform2 = createPlatform(new Spatial(-150*(i-1)-90, -100+i*10+100, 0), 40,Color.RED,false);
        world.addComponent(Constructor.class,platform2);
        world.addComponent(EndPoint.class,platform2);
        world.addEntity(platform2);
        world.constructEntity(platform2);


        Entity character = createCharacter(new Spatial(0,150,0),new Spatial(0,0,0));
        platform.getComponent(Constructor.Node.class).addChild(character);

        Entity sensor = createSensor(new Spatial(0,-10,0));
        character.getComponent(Constructor.Node.class).addChild(sensor);
        character.getComponent(Scenegraph.Node.class).addChild(sensor);


        Entity dot2;
        for(i=0;i<10;i++) {

            dot2 = createCharacterDot(new Spatial(7+9*i, -i*i, i));
            character.getComponent(Constructor.Node.class).addChild(dot2);
            world.addEntity(dot2);

            dot2 = createCharacterDot(new Spatial(-7+-9*i, i*i, i));
            character.getComponent(Constructor.Node.class).addChild(dot2);
            world.addEntity(dot2);
        }

        world.addEntity(sensor);
        world.addEntity(character);
        world.addEntity(platform);

        world.constructEntity(platform);
    }

    protected Entity createSensor(Spatial translation){


        Entity e = world.createEntity();

        Constructor.Node orientationRelative = world.addComponent(Constructor.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.relativeType = IRelative.Relative.CHILD;
        orientationRelative.inheritFromParentType = Constructor.Node.SpatialInheritType.POSITION;


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

    protected Entity createCharacter(Spatial translation, Spatial velocity){

        Entity e = world.createEntity();

        Constructor.Node orientationRelative = world.addComponent(Constructor.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.relativeType = IRelative.Relative.CHILD;

        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);
        renderableRelative.relativeType = IRelative.Relative.PARENT;

        Velocity velocityComponent = world.addComponent(Velocity.class,e);
        velocityComponent.velocity = velocity;
        world.addComponent(Player.class,e);

        Structure.Node structure = world.addComponent(Structure.Node.class,e);
        CircleShape rectangularLineShape = new CircleShape(1,11,Color.WHITE,Color.FIREBRICK);
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

    protected Entity createCharacterDot(Spatial translation){

        Entity e = world.createEntity();

        world.addComponent(Dot.class,e);

        Constructor.Node orientationRelative = world.addComponent(Constructor.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.relativeType = IRelative.Relative.CHILD;
        orientationRelative.inheritFromParentType = Constructor.Node.SpatialInheritType.POSITION_ANGLE;

        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);
        renderableRelative.relativeType = IRelative.Relative.CHILD;

        Structure.Node structure = world.addComponent(Structure.Node.class,e);
        CircleShape rectangularLineShape = new CircleShape(1,5,Color.RED,Color.BLUE);
        structure.shape = rectangularLineShape;
        structure.density = .01f;
        structure.friction = 5f;
        structure.relativeType = IRelative.Relative.CHILD;

        PhysicsBody.Node rel  = world.addComponent(PhysicsBody.Node.class, e);

        if(Math.random()>0.9) {
            rel.relativeType = IRelative.Relative.INTERMEDIATE;
        }

        return e;
    }

    protected Entity createPlatform(Spatial translation,float width,Color color, boolean hasVelocity){

        Entity e = world.createEntity();

        Constructor.Node orientationRelative = world.addComponent(Constructor.Node.class,e);
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
            velocity.velocity = new Spatial(0, 0, 6);
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.KinematicBody;
        }else{
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.StaticBody;
        }

        return e;
    }

}
