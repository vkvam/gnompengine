package com.flatfisk.gnomp.tests.platformer;

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
import com.flatfisk.gnomp.systems.PhysicsSystem;
import com.flatfisk.gnomp.systems.RenderSystem;
import com.flatfisk.gnomp.tests.Test;
import com.flatfisk.gnomp.tests.components.EndPoint;
import com.flatfisk.gnomp.tests.components.Player;
import com.flatfisk.gnomp.tests.components.PlayerSensor;
import com.flatfisk.gnomp.tests.systems.CameraTrackerSystem;

public class TestPlatformer extends Test {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public TestPlatformer(ShapeTextureFactory shapeTextureFactory){
        this.shapeTextureFactory = shapeTextureFactory;
    }

    private final short CATEGORY_PLATFORM =     0x0001;  // 0000000000000001 in binary
    private final short CATEGORY_PLAYER =       0x0002; //  0000000000000010 in binary
    private final short CATEGORY_SENSOR =       0x0004; //  0000000000000100 in binary
    private final short CATEGORY_ENEMY  =       0x0008; //  0000000000001000 in binary

    @Override
    public void create () {
        super.create();
        PhysicsConstants.setPixelsPerMeter(100);
        createSystems(new Vector2(0, -1000f * PhysicsConstants.METERS_PER_PIXEL));

        world.getSystem(RenderSystem.class).getCamera().zoom = 1f;

        PlatformerInputSystem inputSystem = new PlatformerInputSystem(0,world.getSystem(PhysicsSystem.class).getBox2DWorld());
        world.addSystem(inputSystem);
        world.addEntityListener(inputSystem.getFamily(),0,inputSystem);

        world.addSystem(new EnemyMoverSystem(0));
        world.addSystem(new CameraTrackerSystem(1,world.getSystem(RenderSystem.class).getCamera(),true,true));
        createGame(new Spatial(0, -120, 0));
    }

    private void createGame(Spatial position){

        Entity platform = createPlatform(position,90,Color.GREEN,false);
        world.addComponent(Constructable.class,platform);

        Entity platform2 = createPlatform(new Spatial(-1000,-200,0),3000,Color.RED,false);
        world.addComponent(Constructable.class,platform2);
        world.addEntity(platform2);
        world.constructEntity(platform2);

        int i=1;
        for(;i<10;i++) {
            platform2 = createPlatform(new Spatial(-150*i, -100+i*15, -i*4-90), 40,Color.ORANGE,true);
            world.addComponent(Constructable.class,platform2);
            world.addEntity(platform2);
            world.constructEntity(platform2);
        }

        i=0;
        for(;i<10;i++){
            Entity enemy = createEnemy(new Spatial(-250-i*250,250,0));
            world.addEntity(enemy);
            world.constructEntity(enemy);
        }

        platform2 = createPlatform(new Spatial(-150*(i-1)-90, -100+i*10+100, 0), 40,Color.RED,false);
        world.addComponent(Constructable.class,platform2);
        world.addComponent(EndPoint.class,platform2);
        world.addEntity(platform2);
        world.constructEntity(platform2);


        Entity character = createCharacter(new Spatial(0,150,0),new Spatial(0,0,0));
        platform.getComponent(Constructable.Node.class).addChild(character);

        Entity sensor = createSensor(new Spatial(0,-10,0));
        character.getComponent(Constructable.Node.class).addChild(sensor);
        character.getComponent(Scenegraph.Node.class).addChild(sensor);

        world.addEntity(sensor);
        world.addEntity(character);
        world.addEntity(platform);

        world.constructEntity(platform);
    }

    protected Entity createSensor(Spatial translation){


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

        RectangularLineShape rectangularLineShape = new RectangularLineShape(1,6,Color.OLIVE,Color.BLUE);
        rectangularLineShape.from = new Vector2(-7.5f,0);
        rectangularLineShape.to = new Vector2(7.5f,0);
        rectangularLineShape.createPolygonVertices();
        structure.shape = rectangularLineShape;
        structure.relativeType = IRelative.Relative.PARENT;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 1;
        physicalProperties.friction = 5f;
        physicalProperties.isSensor = true;
        physicalProperties.categoryBits = CATEGORY_SENSOR;
        physicalProperties.maskBits = CATEGORY_PLATFORM|CATEGORY_ENEMY;



        Renderable renderableDef = world.addComponent(Renderable.class,e);
        renderableDef.zIndex = -1;


        world.addComponent(PlayerSensor.class,e);
        world.addComponent(Scenegraph.Node.class,e);
        world.addComponent(Structure.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation=true;
        physicsBodyDef.bodyDef.angularDamping=.03f;
        physicsBodyDef.bodyDef.bullet=true;

        return e;
    }

    protected Entity createEnemy(Spatial translation){
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
        RectangularLineShape rectangularLineShape = new RectangularLineShape(1,(float) 5,Color.WHITE,Color.RED);
        rectangularLineShape.from = new Vector2(0,-10);
        rectangularLineShape.to = new Vector2(0,10);
        rectangularLineShape.createPolygonVertices();

        structure.shape = rectangularLineShape;
        structure.relativeType = IRelative.Relative.PARENT;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;
        physicalProperties.categoryBits = CATEGORY_ENEMY;
        physicalProperties.maskBits = CATEGORY_PLATFORM|CATEGORY_PLAYER|CATEGORY_ENEMY|CATEGORY_SENSOR;

        world.addComponent(Renderable.class,e);
        world.addComponent(Structure.class,e);
        world.addComponent(Enemy.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;

        return e;
    }

    protected Entity createCharacter(Spatial translation, Spatial velocity){

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
        CircleShape rectangularLineShape = new CircleShape(1,11,Color.WHITE,Color.FIREBRICK);
        structure.shape = rectangularLineShape;



        structure.relativeType = IRelative.Relative.PARENT;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 1;
        physicalProperties.friction = 5f;
        physicalProperties.categoryBits = CATEGORY_PLAYER;
        physicalProperties.maskBits = CATEGORY_PLATFORM|CATEGORY_ENEMY;

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


    protected Entity createPlatform(Spatial translation,float width,Color color, boolean hasVelocity){

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
        structure.relativeType = IRelative.Relative.PARENT;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;
        physicalProperties.categoryBits = CATEGORY_PLATFORM;
        physicalProperties.maskBits = CATEGORY_PLAYER|CATEGORY_SENSOR|CATEGORY_ENEMY;

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
