package com.flatfisk.gnomp.tests.addremove;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.PhysicsSystem;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.Test;
import com.flatfisk.gnomp.tests.components.Dot;
import com.flatfisk.gnomp.tests.components.EndPoint;
import com.flatfisk.gnomp.tests.components.Player;
import com.flatfisk.gnomp.tests.components.PlayerSensor;
import com.flatfisk.gnomp.tests.systems.CameraTrackerSystem;

public class TestAddRemove extends Test {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public TestAddRemove(ShapeTextureFactory shapeTextureFactory){
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

        world.getSystem(CameraSystem.class).getCamera().zoom = 1f;

        AddRemoveInputSystem inputSystem = new AddRemoveInputSystem(0,world.getSystem(PhysicsSystem.class).getBox2DWorld());
        world.addSystem(inputSystem);
        world.addEntityListener(inputSystem.getFamily(),0,inputSystem);

        world.addSystem(new CameraTrackerSystem(1,world.getSystem(CameraSystem.class).getCamera(),true,true));
        createGame(new Transform(0, -120, 0));
    }

    private void createGame(Transform position){

        Entity platform = createPlatform(position,90,Color.GREEN,false);
        world.addComponent(Spatial.class,platform);

        Entity platform2 = createPlatform(new Transform(-1000,-200,0),3000,Color.RED,false);
        world.addComponent(Spatial.class,platform2);
        world.addEntity(platform2);
        world.constructEntity(platform2);

        int i=1;
        for(;i<10;i++) {
            platform2 = createPlatform(new Transform(-150*i, -100+i*15, -i*4-90), 40,Color.ORANGE,true);
            world.addComponent(Spatial.class,platform2);
            world.addEntity(platform2);
            world.constructEntity(platform2);
        }

        platform2 = createPlatform(new Transform(-150*(i-1)-90, -100+i*10+100, 0), 40,Color.RED,false);
        world.addComponent(Spatial.class,platform2);
        world.addComponent(EndPoint.class,platform2);
        world.addEntity(platform2);
        world.constructEntity(platform2);


        Entity character = createCharacter(new Transform(0,150,0),new Transform(0,0,0));
        platform.getComponent(Spatial.Node.class).addChild(character);

        Entity sensor = createSensor(new Transform(0,-10,0));
        character.getComponent(Spatial.Node.class).addChild(sensor);
        character.getComponent(Scenegraph.Node.class).addChild(sensor);


        Entity dot2;
        for(i=0;i<10;i++) {

            dot2 = createCharacterDot(new Transform(7+9*i, -i*i, i));
            character.getComponent(Spatial.Node.class).addChild(dot2);
            world.addEntity(dot2);

            dot2 = createCharacterDot(new Transform(-7+-9*i, i*i, i));
            character.getComponent(Spatial.Node.class).addChild(dot2);
            world.addEntity(dot2);
        }

        world.addEntity(sensor);
        world.addEntity(character);
        world.addEntity(platform);

        world.constructEntity(platform);
    }

    protected Entity createSensor(Transform translation){


        Entity e = world.createEntity();

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        //orientationRelative.relativeType = Relative.CHILD;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;


        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);


        PhysicsBody.Node physicsBodyRelative = world.addComponent(PhysicsBody.Node.class,e);

        Shape structure = world.addComponent(Shape.class,e);

        RectangularLine rectangularLineShape = new RectangularLine(1,4,Color.OLIVE,Color.BLUE);
        rectangularLineShape.from = new Vector2(-1.5f,0);
        rectangularLineShape.to = new Vector2(1.5f,0);
        rectangularLineShape.createPolygonVertices();

        structure.geometry = rectangularLineShape;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 1;
        physicalProperties.friction = 5f;
        physicalProperties.isSensor = true;
        physicalProperties.categoryBits = CATEGORY_SENSOR;
        physicalProperties.maskBits = CATEGORY_PLATFORM;


        Renderable renderableDef = world.addComponent(Renderable.class,e);
        renderableDef.zIndex = -1;


        world.addComponent(PlayerSensor.class,e);
        world.addComponent(Scenegraph.Node.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation=true;
        physicsBodyDef.bodyDef.angularDamping=.03f;

        return e;
    }

    protected Entity createCharacter(Transform translation, Transform velocity){

        Entity e = world.createEntity();

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        //orientationRelative.relativeType = Relative.CHILD;

        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);

        Velocity velocityComponent = world.addComponent(Velocity.class,e);
        velocityComponent.velocity = velocity;
        world.addComponent(Player.class,e);

        Shape structure = world.addComponent(Shape.class,e);
        Circle rectangularLineShape = new Circle(1,11,Color.WHITE,Color.FIREBRICK);
        structure.geometry = rectangularLineShape;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 1;
        physicalProperties.friction = 5f;
        physicalProperties.categoryBits = CATEGORY_PLAYER;
        physicalProperties.maskBits = CATEGORY_PLATFORM;

        world.addComponent(Renderable.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation=false;
        physicsBodyDef.bodyDef.angularDamping=0.5f;


        world.addComponent(Scenegraph.class,e);
        world.addComponent(Scenegraph.Node.class,e);

        PhysicsBody.Node physicsBodyRelative = world.addComponent(PhysicsBody.Node.class,e);

        return e;
    }

    protected Entity createCharacterDot(Transform translation){

        Entity e = world.createEntity();

        world.addComponent(Dot.class,e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        //orientationRelative.relativeType = Relative.CHILD;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION_ANGLE;

        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);

        Shape structure = world.addComponent(Shape.class,e);
        Circle rectangularLineShape = new Circle(1,5,Color.RED,Color.BLUE);
        structure.geometry = rectangularLineShape;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = .01f;
        physicalProperties.friction = 5f;

        PhysicsBody.Node rel  = world.addComponent(PhysicsBody.Node.class, e);

        if(Math.random()>0.9) {
            rel.intermediate = true;
        }

        return e;
    }

    protected Entity createPlatform(Transform translation,float width,Color color, boolean hasVelocity){

        Entity e = world.createEntity();

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;

        world.addComponent(Renderable.Node.class,e);
        world.addComponent(PhysicsBody.Node.class,e);

        Shape structure = world.addComponent(Shape.class,e);
        RectangularLine rectangularLineShape = new RectangularLine(1,(float) 5,Color.WHITE,color);
        rectangularLineShape.from = new Vector2(-width/2,0);
        rectangularLineShape.to = new Vector2(width/2,0);
        rectangularLineShape.createPolygonVertices();

        structure.geometry = rectangularLineShape;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;
        physicalProperties.categoryBits = CATEGORY_PLATFORM;
        physicalProperties.maskBits = CATEGORY_PLAYER|CATEGORY_SENSOR;

        world.addComponent(Renderable.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        if(hasVelocity) {
            Velocity velocity = world.addComponent(Velocity.class,e);
            velocity.velocity = new Transform(0, 0, 6);
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.KinematicBody;
        }else{
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.StaticBody;
        }

        return e;
    }

}
