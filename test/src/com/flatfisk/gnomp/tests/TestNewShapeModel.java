package com.flatfisk.gnomp.tests;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.components.Shape;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.PhysicsSystem;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.platformer.PlatformerInputSystem;
import com.flatfisk.gnomp.tests.systems.CameraTrackerSystem;
import com.flatfisk.gnomp.tests.systems.PhysicsEventListener;
import com.flatfisk.gnomp.tests.systems.RemoveEntitySystem;

public class TestNewShapeModel extends Test {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public TestNewShapeModel(ShapeTextureFactory shapeTextureFactory){
        this.shapeTextureFactory = shapeTextureFactory;
    }

    private final static short CATEGORY_PLATFORM =     0x0001;  // 0000000000000001 in binary
    private final static short CATEGORY_PLAYER =       0x0002; //  0000000000000010 in binary
    private final static short CATEGORY_SENSOR =       0x0004; //  0000000000000100 in binary
    private final static short CATEGORY_ENEMY  =       0x0008; //  0000000000001000 in binary
    private final static short CATEGORY_LIGHT =        0x0010; //  0000000000010000 in binary

    @Override
    public void create () {
        super.create();
        PhysicsConstants.setPixelsPerMeter(100);
        createSystems(new Vector2(0, -1000f * PhysicsConstants.METERS_PER_PIXEL), false, true);

        World w = engine.getSystem(PhysicsSystem.class).getBox2DWorld();

        engine.getSystem(CameraSystem.class).getWorldCamera().zoom = 1f;

        PlatformerInputSystem inputSystem = new PlatformerInputSystem(0,w);
        engine.addSystem(inputSystem);
        engine.addEntityListener(inputSystem.getFamily(),0,inputSystem);

        engine.addSystem(new CameraTrackerSystem(1, engine.getSystem(CameraSystem.class).getWorldCamera(),true,true));
        Entity e = createGame(new Transform(0, -120, 0));

        engine.addSystem(new PhysicsEventListener(w,engine.getSystem(CameraSystem.class),200));
        engine.addSystem(new RemoveEntitySystem());


    }

    private Entity createGame(Transform position){

        Entity platform = createPlatform(position,90,Color.GREEN,false);
        engine.addComponent(Spatial.class,platform);
        engine.constructEntity(platform);
        return platform;
    }



    private Entity createPlatform(Transform translation, float width, Color color, boolean hasVelocity){

        Entity entity = engine.addEntity();
        engine.addComponent(Renderable.class,entity);
        engine.addComponent(Renderable.Node.class,entity);
        engine.addComponent(PhysicsBody.Node.class,entity);

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class,entity);
        orientationRelative.local = translation;
        orientationRelative.world = translation;


        Shape<RectangularLine> shape = engine.addComponent(Shape.class, entity);
        RectangularLine rl = shape.obtain(RectangularLine.class);
        rl.from.set(-width/3,0);
        rl.to.set(width/2,0);
        rl.createPolygonVertices();

        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class,entity);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;
        physicalProperties.categoryBits = CATEGORY_PLATFORM;
        physicalProperties.maskBits = CATEGORY_PLAYER|CATEGORY_SENSOR|CATEGORY_ENEMY|CATEGORY_LIGHT;

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class,entity);
        if(hasVelocity) {
            Velocity velocity = engine.addComponent(Velocity.class,entity);
            velocity.velocity = new Transform(0, 0, 6);
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.KinematicBody;
        }else{
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.StaticBody;
        }

        return entity;
    }

}
