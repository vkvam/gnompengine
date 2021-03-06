package com.flatfisk.gnomp.tests;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.components.light.LightDef;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.LightSystem;
import com.flatfisk.gnomp.engine.systems.PhysicsSystem;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.components.*;
import com.flatfisk.gnomp.tests.platformer.Enemy;
import com.flatfisk.gnomp.tests.platformer.EnemyMoverSystem;
import com.flatfisk.gnomp.tests.platformer.PlatformerInputSystem;
import com.flatfisk.gnomp.tests.systems.CameraTrackerSystem;
import com.flatfisk.gnomp.tests.systems.PhysicsEventListener;
import com.sun.org.apache.regexp.internal.RE;

public class TestPlatformer extends Test {

    private Logger LOG = new Logger(this.getClass().getName(), Logger.DEBUG);

    public TestPlatformer(ShapeTextureFactory shapeTextureFactory) {
        this.shapeTextureFactory = shapeTextureFactory;
    }

    private final static short CATEGORY_PLATFORM = 0x0001;  // 0000000000000001 in binary
    private final static short CATEGORY_PLAYER = 0x0002; //  0000000000000010 in binary
    private final static short CATEGORY_SENSOR = 0x0004; //  0000000000000100 in binary
    private final static short CATEGORY_ENEMY = 0x0008; //  0000000000001000 in binary
    private final static short CATEGORY_LIGHT = 0x0010; //  0000000000010000 in binary

    @Override
    public void create() {
        super.create();
        PhysicsConstants.setPixelsPerMeter(100);
        createSystems(new Vector2(0, -1000f * PhysicsConstants.METERS_PER_PIXEL), false, true, true);

        World w = engine.getSystem(PhysicsSystem.class).getBox2DWorld();

        engine.getSystem(CameraSystem.class).getWorldCamera().zoom = 1f;

        PlatformerInputSystem inputSystem = new PlatformerInputSystem(0, w);
        engine.addSystem(inputSystem);
        engine.addEntityListener(inputSystem.getFamily(), 0, inputSystem);

        engine.addSystem(new EnemyMoverSystem(0));
        engine.addSystem(new CameraTrackerSystem(1, engine.getSystem(CameraSystem.class).getWorldCamera(), true, true));
        createGame(new Transform(0, -120, 0));

        engine.addSystem(new PhysicsEventListener(w, engine.getSystem(CameraSystem.class), 200));
    }

    private void createGame(Transform position) {

        Entity platform = createPlatform(position, 90, Color.GREEN, false);
        engine.addComponent(Spatial.class, platform);

        Entity platform2 = createPlatform(new Transform(-1000, -200, 0), 3000, Color.RED, false);
        engine.addComponent(Spatial.class, platform2);

        engine.constructEntity(platform2);

        int i = 1;

        for (; i < 10; i++) {
            platform2 = createPlatform(new Transform(-150 * i, -100 + i * 15, -i * 4 - 90), 40, Color.ORANGE, true);
            engine.addComponent(Spatial.class, platform2);

            engine.constructEntity(platform2);
        }

        platform2 = createPlatform(new Transform(-150 * (i - 1) - 90, -100 + i * 10 + 100, 0), 40, Color.RED, false);
        engine.addComponent(Spatial.class, platform2);
        engine.addComponent(EndPoint.class, platform2);

        engine.constructEntity(platform2);

        i = 0;
        for (; i < 10; i++) {
            Entity enemy = createEnemy(engine, new Transform(-250 - i * 250, 250, 0));

            engine.constructEntity(enemy);
        }


        Entity character = createCharacter(new Transform(0, 150, 0), new Transform(0, 0, 0));
        platform.getComponent(Spatial.Node.class).addChild(character);


        Entity sensor = createSensor(new Transform(0, -10, 0));
        character.getComponent(Spatial.Node.class).addChild(sensor);
        character.getComponent(Scenegraph.Node.class).addChild(sensor);

        Entity flashLight = createLight(engine, new Transform(0, 10, 0), false);
        engine.addComponent(PlayerLight.class, flashLight);
        character.getComponent(Spatial.Node.class).addChild(flashLight);
        character.getComponent(Scenegraph.Node.class).addChild(flashLight);


        Entity gun = createGun(new Transform(50, 0, 0));
        character.getComponent(Spatial.Node.class).addChild(gun);
        character.getComponent(Scenegraph.Node.class).addChild(gun);


        Entity ambientCharacterLight = createLight(engine, new Transform(0, 0, 0), true);
        character.getComponent(Spatial.Node.class).addChild(ambientCharacterLight);
        character.getComponent(Scenegraph.Node.class).addChild(ambientCharacterLight);


        engine.constructEntity(platform);
    }


    protected Entity createSensor(Transform translation) {
        Entity e = engine.addEntity();

        engine.addComponent(PlayerSensor.class, e);
        engine.addComponent(Scenegraph.Node.class, e);
        engine.addComponent(Renderable.Node.class, e);
        engine.addComponent(PhysicsBody.Node.class, e);


        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;

        Shape<RectangularLine> structure = engine.addComponent(Shape.class, e);
        RectangularLine rectangularLineShape = structure.obtain(RectangularLine.class);
        rectangularLineShape.lineWidth = 1;
        rectangularLineShape.rectangleWidth = 6;
        rectangularLineShape.from.set(-7.5f, 0);
        rectangularLineShape.to.set(7.5f, 0);
        rectangularLineShape.createPolygonVertices();

        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class, e);
        physicalProperties.isSensor = true;
        physicalProperties.categoryBits = CATEGORY_SENSOR;
        physicalProperties.maskBits = CATEGORY_PLATFORM | CATEGORY_ENEMY;

        Renderable renderableDef = engine.addComponent(Renderable.class, e);
        renderableDef.zIndex = -1;

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class, e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation = true;
        physicsBodyDef.bodyDef.angularDamping = .03f;
        physicsBodyDef.bodyDef.bullet = true;

        return e;
    }

    protected Entity createGun(Transform translation) {
        Entity e = engine.addEntity();

        engine.addComponent(Renderable.class, e);
        e.getComponent(Renderable.class).zIndex = 10;
        engine.addComponent(Renderable.Node.class, e);
        engine.addComponent(Scenegraph.Node.class, e);
        engine.addComponent(Gun.class, e);

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;

        Shape<RectangularLine> structure = engine.addComponent(Shape.class, e);
        RectangularLine barrel = structure.obtain(RectangularLine.class);
        barrel.rectangleWidth = 3;
        barrel.lineWidth = 3;
        barrel.fillColor = Color.DARK_GRAY;
        barrel.lineColor = Color.GRAY;
        barrel.from.set(-10, 0);
        barrel.to.set(30, 0);
        barrel.createPolygonVertices();

        return e;
    }

    protected static Entity createLight(GnompEngine world, Transform translation, boolean ambient) {
        Entity e = world.addEntity();

        world.addComponent(Scenegraph.Node.class, e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;

        LightDef light;

        if (ambient) {
            LightDef.Point pointDef = new LightDef.Point();
            pointDef.color = new Color(0.6f, 0.6f, 0.4f, 0.7f);
            pointDef.distance = 20;
            pointDef.staticLight = true;
            pointDef.group = 0;
            light = pointDef;
        } else {
            LightDef.Cone coneDef = new LightDef.Cone();
            coneDef.color = new Color(0.9f, 0.9f, .8f, 0.8f);
            coneDef.softShadowLength = 80;
            coneDef.coneAngle = 30;
            coneDef.distance = 400;
            coneDef.rayNum = 150;
            coneDef.group = 0;
            coneDef.categoryBits = CATEGORY_LIGHT;
            coneDef.maskBits = CATEGORY_ENEMY | CATEGORY_PLATFORM;
            light = coneDef;
        }

        Light l = world.addComponent(Light.class, e);
        l.lightDef = light;


        return e;
    }

    public static Entity createEnemy(GnompEngine world, Transform translation) {
        Entity e = world.addEntity();

        world.addComponent(Spatial.class, e);
        world.addComponent(Scenegraph.class, e);
        world.addComponent(Scenegraph.Node.class, e);

        world.addComponent(PhysicsBody.Node.class, e);
        world.addComponent(Renderable.class, e);
        world.addComponent(Renderable.Node.class, e);
        world.addComponent(Enemy.class, e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;

        Shape<RectangularLine> structure = world.addComponent(Shape.class, e);
        RectangularLine rectangularLineShape = structure.obtain(RectangularLine.class);
        rectangularLineShape.lineWidth = 1;
        rectangularLineShape.rectangleWidth = 5;
        rectangularLineShape.lineColor = Color.WHITE;
        rectangularLineShape.fillColor = Color.RED;

        rectangularLineShape.from.set(0, -10);
        rectangularLineShape.to.set(0, 10);
        rectangularLineShape.createPolygonVertices();


        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class, e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;
        physicalProperties.categoryBits = CATEGORY_ENEMY;
        physicalProperties.maskBits = CATEGORY_PLATFORM | CATEGORY_PLAYER | CATEGORY_ENEMY | CATEGORY_SENSOR | CATEGORY_LIGHT;

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class, e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;


        Entity l = createLight(world, new Transform(0, 0, 0), true);
        e.getComponent(Spatial.Node.class).addChild(l);
        e.getComponent(Scenegraph.Node.class).addChild(l);


        return e;
    }

    protected Entity createCharacter(Transform translation, Transform velocity) {
        Entity e = engine.addEntity();

        engine.addComponent(Renderable.class, e);
        engine.addComponent(Renderable.Node.class, e);
        engine.addComponent(Scenegraph.class, e);
        engine.addComponent(Scenegraph.Node.class, e);
        engine.addComponent(PhysicsBody.Node.class, e);


        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;

        Velocity velocityComponent = engine.addComponent(Velocity.class, e);
        velocityComponent.velocity = velocity;
        engine.addComponent(Player.class, e);

        Shape<Circle> structure = engine.addComponent(Shape.class, e);
        Circle circle = structure.obtain(Circle.class);
        circle.lineWidth = 1;
        circle.setRadius(11);
        circle.lineColor = Color.WHITE;
        circle.fillColor = Color.FIREBRICK;

        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class, e);
        physicalProperties.density = 1;
        physicalProperties.friction = 5f;
        physicalProperties.categoryBits = CATEGORY_PLAYER;
        physicalProperties.maskBits = CATEGORY_PLATFORM | CATEGORY_ENEMY;

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class, e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation = false;
        physicsBodyDef.bodyDef.angularDamping = 0.5f;

        return e;
    }


    private Entity createPlatform(Transform translation, float width, Color color, boolean hasVelocity) {

        Entity e = engine.addEntity();
        engine.addComponent(Renderable.class, e);
        engine.addComponent(Renderable.Node.class, e);
        engine.addComponent(PhysicsBody.Node.class, e);

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;


        Shape<RectangularLine> shape = engine.addComponent(Shape.class, e);
        RectangularLine rectangularLineShape = shape.obtain(RectangularLine.class);
        rectangularLineShape.lineWidth = 1;
        rectangularLineShape.rectangleWidth = 5;
        rectangularLineShape.lineColor = Color.WHITE;
        rectangularLineShape.fillColor = color;
        rectangularLineShape.from.set(-width / 2, 0);
        rectangularLineShape.to.set(width / 2, 0);
        rectangularLineShape.createPolygonVertices();

        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class, e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;
        physicalProperties.categoryBits = CATEGORY_PLATFORM;
        physicalProperties.maskBits = CATEGORY_PLAYER | CATEGORY_SENSOR | CATEGORY_ENEMY | CATEGORY_LIGHT;

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class, e);
        if (hasVelocity) {
            Velocity velocity = engine.addComponent(Velocity.class, e);
            velocity.velocity = new Transform(0, 0, 6);
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.KinematicBody;
        } else {
            physicsBodyDef.bodyDef.type = BodyDef.BodyType.StaticBody;
        }

        return e;
    }

}
