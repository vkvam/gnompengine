package com.flatfisk.gnomp.tests;

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
import com.flatfisk.gnomp.tests.addremove.AddRemoveInputSystem;
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
        createSystems(new Vector2(0, -1000f * PhysicsConstants.METERS_PER_PIXEL), true, true, false);

        engine.getSystem(CameraSystem.class).getWorldCamera().zoom = 1f;

        AddRemoveInputSystem inputSystem = new AddRemoveInputSystem(0, engine.getSystem(PhysicsSystem.class).getBox2DWorld());
        engine.addSystem(inputSystem);
        engine.addEntityListener(inputSystem.getFamily(),0,inputSystem);

        engine.addSystem(new CameraTrackerSystem(1, engine.getSystem(CameraSystem.class).getWorldCamera(),true,true));
        createGame(new Transform(0, -120, 0));
    }

    private void createGame(Transform position){

        Entity platform = createPlatform(position,90,Color.GREEN,false);
        engine.addComponent(Spatial.class,platform);

        Entity platform2 = createPlatform(new Transform(-1000,-200,0),3000,Color.RED,false);
        engine.addComponent(Spatial.class,platform2);
        
        engine.constructEntity(platform2);

        int i=1;
        for(;i<10;i++) {
            platform2 = createPlatform(new Transform(-150*i, -100+i*15, -i*4-90), 40,Color.ORANGE,true);
            engine.addComponent(Spatial.class,platform2);
            
            engine.constructEntity(platform2);
        }

        platform2 = createPlatform(new Transform(-150*(i-1)-90, -100+i*10+100, 0), 40,Color.RED,false);
        engine.addComponent(Spatial.class,platform2);
        engine.addComponent(EndPoint.class,platform2);
        
        engine.constructEntity(platform2);


        Entity character = createCharacter(new Transform(0,150,0),new Transform(0,0,0));
        platform.getComponent(Spatial.Node.class).addChild(character);

        Entity sensor = createSensor(new Transform(0,-10,0));
        character.getComponent(Spatial.Node.class).addChild(sensor);
        character.getComponent(Scenegraph.Node.class).addChild(sensor);


        Entity dot2;
        for(i=0;i<10;i++) {

            dot2 = createCharacterDot(new Transform(7+9*i, -i*i, i));
            character.getComponent(Spatial.Node.class).addChild(dot2);
            

            dot2 = createCharacterDot(new Transform(-7+-9*i, i*i, i));
            character.getComponent(Spatial.Node.class).addChild(dot2);
            
        }

        
        
        

        engine.constructEntity(platform);
    }

    protected Entity createSensor(Transform translation){


        Entity e = engine.addEntity();

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        //orientationRelative.relativeType = Relative.CHILD;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;


        Renderable.Node renderableRelative = engine.addComponent(Renderable.Node.class,e);


        PhysicsBody.Node physicsBodyRelative = engine.addComponent(PhysicsBody.Node.class,e);

        Shape<RectangularLine> structure = engine.addComponent(Shape.class,e);

        RectangularLine rectangularLineShape = structure.obtain(RectangularLine.class);
        rectangularLineShape.lineWidth = 1;
        rectangularLineShape.rectangleWidth = 4;
        rectangularLineShape.lineColor = Color.OLIVE;
        rectangularLineShape.fillColor = Color.BLUE;

        rectangularLineShape.from.set(-1.5f,0);
        rectangularLineShape.to.set(1.5f,0);
        rectangularLineShape.createPolygonVertices();


        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 1;
        physicalProperties.friction = 5f;
        physicalProperties.isSensor = true;
        physicalProperties.categoryBits = CATEGORY_SENSOR;
        physicalProperties.maskBits = CATEGORY_PLATFORM;


        Renderable renderableDef = engine.addComponent(Renderable.class,e);
        renderableDef.zIndex = -1;


        engine.addComponent(PlayerSensor.class,e);
        engine.addComponent(Scenegraph.Node.class,e);

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation=true;
        physicsBodyDef.bodyDef.angularDamping=.03f;

        return e;
    }

    protected Entity createCharacter(Transform translation, Transform velocity){

        Entity e = engine.addEntity();

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        //orientationRelative.relativeType = Relative.CHILD;

        Renderable.Node renderableRelative = engine.addComponent(Renderable.Node.class,e);

        Velocity velocityComponent = engine.addComponent(Velocity.class,e);
        velocityComponent.velocity = velocity;
        engine.addComponent(Player.class,e);

        Shape<Circle> structure = engine.addComponent(Shape.class,e);


        Circle rectangularLineShape = structure.obtain(Circle.class);
        rectangularLineShape.lineWidth = 1;
        rectangularLineShape.setRadius(11);
        rectangularLineShape.lineColor = Color.WHITE;
        rectangularLineShape.fillColor = Color.FIREBRICK;

        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 1;
        physicalProperties.friction = 5f;
        physicalProperties.categoryBits = CATEGORY_PLAYER;
        physicalProperties.maskBits = CATEGORY_PLATFORM;

        engine.addComponent(Renderable.class,e);

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation=false;
        physicsBodyDef.bodyDef.angularDamping=0.5f;


        engine.addComponent(Scenegraph.class,e);
        engine.addComponent(Scenegraph.Node.class,e);

        PhysicsBody.Node physicsBodyRelative = engine.addComponent(PhysicsBody.Node.class,e);

        return e;
    }

    protected Entity createCharacterDot(Transform translation){

        Entity e = engine.addEntity();

        engine.addComponent(Dot.class,e);

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;

        engine.addComponent(Renderable.Node.class,e);

        Shape<Circle> structure = engine.addComponent(Shape.class,e);
        Circle rectangularLineShape = structure.obtain(Circle.class);
        rectangularLineShape.lineWidth = 1;
        rectangularLineShape.setRadius(5);
        rectangularLineShape.lineColor  = Color.RED;
        rectangularLineShape.fillColor = Color.BLUE;

/*
        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = .01f;
        physicalProperties.friction = 5f;

        PhysicsBody.Node rel  = engine.addComponent(PhysicsBody.Node.class, e);

        if(Math.random()>0.9) {
            rel.intermediate = true;
        }
*/
        return e;
    }

    protected Entity createPlatform(Transform translation,float width,Color color, boolean hasVelocity){

        Entity e = engine.addEntity();

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;

        engine.addComponent(Renderable.Node.class,e);
        engine.addComponent(PhysicsBody.Node.class,e);

        Shape<RectangularLine> structure = engine.addComponent(Shape.class,e);
        RectangularLine rectangularLineShape = structure.obtain(RectangularLine.class);
        rectangularLineShape.lineWidth = 1;
        rectangularLineShape.rectangleWidth = 5;
        rectangularLineShape.lineColor = Color.WHITE;
        rectangularLineShape.fillColor = color;
        rectangularLineShape.from.set(-width/2,0);
        rectangularLineShape.to.set(width/2,0);
        rectangularLineShape.createPolygonVertices();


        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;
        physicalProperties.categoryBits = CATEGORY_PLATFORM;
        physicalProperties.maskBits = CATEGORY_PLAYER|CATEGORY_SENSOR;

        engine.addComponent(Renderable.class,e);

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class,e);
        if(hasVelocity) {
            Velocity velocity = engine.addComponent(Velocity.class,e);
            velocity.velocity = new Transform(0, 0, 6);
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.KinematicBody;
        }else{
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.StaticBody;
        }

        return e;
    }

}
