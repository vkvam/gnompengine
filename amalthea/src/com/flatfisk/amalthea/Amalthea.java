package com.flatfisk.amalthea;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.flatfisk.amalthea.components.*;
import com.flatfisk.amalthea.components.Enemy;
import com.flatfisk.amalthea.factories.SystemContructors;
import com.flatfisk.amalthea.factories.procedural.*;
import com.flatfisk.amalthea.systems.*;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.components.light.LightDef;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.engine.shape.Polygon;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.steering.behaviours.Arrive;
import com.flatfisk.gnomp.engine.steering.behaviours.AvoidCollision;
import com.flatfisk.gnomp.engine.steering.behaviours.Face;
import com.flatfisk.gnomp.engine.systems.*;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.utils.Pools;

import static com.flatfisk.gnomp.engine.CollisionCategories.*;

public class Amalthea extends SystemContructors {

    boolean running = true;

    public Amalthea(ShapeTextureFactory shapeTextureFactory) {
        this.shapeTextureFactory = shapeTextureFactory;
    }

    public void render() {
        if (running) {
            super.render();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            running = !running;
            Gdx.input.setCursorCatched(running);
        }
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_INFO);
        Gdx.input.setCursorCatched(true);
        Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();
        //Gdx.graphics.setFullscreenMode(displayModes[0]);

        Gdx.app.setLogLevel(Gdx.app.LOG_INFO);
        super.create();
        PhysicsConstants.setPixelsPerMeter(100);
        createSystems(new Vector2(0f, 0f), false, true);

        World w = engine.getSystem(PhysicsSystem.class).getBox2DWorld();


        engine.getSystem(CameraSystem.class).getWorldCamera().zoom = 1f;
        engine.getSystem(CameraSystem.class).setBackgroundProvider(
                new NoiseBackgroundProvider(1280, 720)
        );

        PlayerControlSystem inputSystem = new PlayerControlSystem(0, engine.getSystem(PhysicsSystem.class));
        engine.addSystem(inputSystem);

        PlayerLightSystem flickerSystem = new PlayerLightSystem(1, w);
        engine.addSystem(flickerSystem);

        engine.addEntityListener(inputSystem.getFamily(), 0, inputSystem);
        engine.addEntityListener(flickerSystem.getFamily(), 1, flickerSystem);

        PhysicsSteeringSystem sys = new PhysicsSteeringSystem(201, w, false);
        engine.addSystem(sys);

        DockSystem sys2 = new DockSystem(201);
        engine.addSystem(sys2);


        engine.addSystem(new EnemyDecisionSystem(1000000));
        engine.addSystem(new CameraTrackerSystem(1, engine.getSystem(CameraSystem.class).getWorldCamera(), true, true));
        engine.addSystem(new CameraMapTrackerSystem(2, engine.getSystem(CameraSystem.class).getMapCamera(), true, true));
        createBackGround();

        engine.addSystem(new PhysicsEventListener(w, engine.getSystem(CameraSystem.class), 200));

        SoundSystem s = new SoundSystem(210);
        engine.addSystem(s);
        engine.addEntityListener(Family.all(SoundComponent.class).get(), 2, s);

        engine.addSystem(new MouseSystem(701, engine.getSystem(CameraSystem.class)));


        engine.addSystem(new UISystem(700));

        createGame(new Transform(0, -120, 0));
    }

    private void createGame(Transform position) {

        MapColor mapColor;
        Entity island;
        Spatial spatial;


        IslandGenerator.Islands islands = IslandGenerator.getIslands(256, 256, 10, 0.75f, 40);

        engine.getSystem(EnemyDecisionSystem.class).initPathFinding(islands.wayPoints);

        // TODO: Replace with something that resemples floating debree
        for(int i =0;i<512;i+=32){
            for(int j =0;j<512;j+=32){
                float x = (i-512/2)*40;
                float y = (j-512/2)*40;
                Entity e = engine.addEntity();
                Spatial nr = engine.createComponent(Spatial.class, e);
                Spatial.Node n = engine.createComponent(Spatial.Node.class, e);
                n.local.vector.set(x,y);
                n.world.vector.set(x,y);
                e.add(n);
                e.add(nr);


                engine.addComponent(Renderable.class, e);
                engine.addComponent(Renderable.Node.class, e);

                Shape structure = engine.addComponent(Shape.class, e);
                structure.geometry = new Circle(1, 4, null, new Color(0.6f,0.6f,0.6f,1f));

                engine.constructEntity(e);

            }
        }

        for (IslandGenerator.PolyPoint polyPoint : islands.islands) {


            island = createIsland(polyPoint.poly, new Transform(polyPoint.pos, 0), Color.ORANGE);

            NameGen newName = new NameGen(3,10, (long) (polyPoint.pos.x+polyPoint.pos.y));

            island.getComponent(Renderable.class).zIndex = 10;
            //DockGenerator.DockPositions dps = IslandPopulator.getDockPositions(engine, island, polyPoint.pos, 100,100);
            DockGenerator.DockPositions dps = IslandPopulator.getDockPositions(polyPoint, 200, 80);
            spatial = engine.createComponent(Spatial.class, island);
            mapColor = engine.createComponent(MapColor.class, island);
            mapColor.color = Color.CLEAR;

            if (dps != null) {
                Label l = engine.createComponent(Label.class, island);
                l.scale = 2;
                l.text = newName.getName();
                island.add(l);

                HasDocks hasDocks = engine.createComponent(HasDocks.class, island);
                island.add(hasDocks);

                for (Vector2 middle : dps.dockPositions) {
                    Entity dock = DockGenerator.buildDock(engine, new Transform(middle, dps.direction.angle()), dps.width / 2, dps.height);
                    island.getComponent(Spatial.Node.class).addChild(dock);
                    Gdx.app.log(getClass().getName(), "Added dock");
                }
            }

            island.add(spatial);
            island.add(mapColor);

            engine.constructEntity(island);
            Gdx.app.log(getClass().getName(), "Constructed Island");
        }

        debugWayPoints(islands, engine);

        for (int i = -20; i < 0; i++) {
            for (int j = -20; j < 0; j++) {
                //int i = -1;
                //int j = 0;
                Entity enemy = createEnemy(engine, new Transform(i * 1500, j * 1500, 0));
                engine.constructEntity(enemy);
                Label l = engine.createComponent(Label.class, enemy);
                l.offset.y=-50;
                l.text = "Cruiser";
                enemy.add(l);

            }
        }


        Entity character = createCharacter(new Transform(-1000, 1050, 0), new Transform(0, 0, 0));

        Label l = engine.createComponent(Label.class, character);
        l.text = "100%";
        l.offset.y=-50;
        character.add(l);

        Entity flashLight = createLight(engine, new Transform(0, 10, 0), false, 70);
        engine.addComponent(PlayerLight.class, flashLight);
        character.getComponent(Spatial.Node.class).addChild(flashLight);
        character.getComponent(Scenegraph.Node.class).addChild(flashLight);

        Entity flashLight2 = createLight(engine, new Transform(0, -10, 0), false, 20);
        engine.addComponent(PlayerLight.class, flashLight2);
        character.getComponent(Spatial.Node.class).addChild(flashLight2);
        character.getComponent(Scenegraph.Node.class).addChild(flashLight2);

        Entity gun = createGun(engine, new Transform(0, 0, 0));
        engine.addComponent(Gun.class, gun);
        character.getComponent(Spatial.Node.class).addChild(gun);
        character.getComponent(Scenegraph.Node.class).addChild(gun);

        Entity ambientCharacterLight = createLight(engine, new Transform(0, 0, 0), true, 0);
        character.getComponent(Spatial.Node.class).addChild(ambientCharacterLight);
        character.getComponent(Scenegraph.Node.class).addChild(ambientCharacterLight);

        engine.constructEntity(character);

    }

    private Entity createBackGround() {
        Entity e = engine.addEntity();
        engine.addComponent(Renderable.Container.class, e);
        e.getComponent(Renderable.Container.class).texture = new Texture(Gdx.files.internal("data/maple.jpg"));
        e.getComponent(Renderable.Container.class).offset = new Vector2(0, 0);
        e.getComponent(Renderable.Container.class).flipped = false;
        e.getComponent(Renderable.Container.class).zIndex = -90;
        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class, e);
        orientationRelative.local.set(Vector2.Zero.cpy(), 0);
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;
        return e;
    }


    public static Entity createGun(GnompEngine engine, Transform translation) {
        Entity e = engine.addEntity();

        engine.addComponent(Renderable.class, e);
        e.getComponent(Renderable.class).zIndex = 0;
        engine.addComponent(Renderable.Node.class, e);
        engine.addComponent(Scenegraph.Node.class, e);

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;

        Shape structure = engine.addComponent(Shape.class, e);
        RectangularLine barrel = new RectangularLine(3, 3, Color.DARK_GRAY, Color.GRAY);
        barrel.from.set(-10, 0);
        barrel.to.set(30, 0);
        barrel.createPolygonVertices();
        structure.geometry = barrel;


        return e;
    }

    private static Entity createLight(GnompEngine world, Transform translation, boolean ambient, float coneAngle) {
        Entity e = world.addEntity();

        world.addComponent(Scenegraph.Node.class, e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        // TODO: Should be SpatialInheritType.POSITION_ANGLE, and PLAYER should not be a rotating ball...
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;


        LightDef light;

        if (ambient) {
            LightDef.Point pointDef = new LightDef.Point();
            pointDef.color = new Color(0.6f, 0.6f, 0.4f, 0.7f);
            pointDef.distance = 30;
            pointDef.staticLight = true;
            pointDef.group = 0;
            light = pointDef;
        } else {
            LightDef.Cone coneDef = new LightDef.Cone();
            coneDef.color = new Color(0.9f, 0.9f, .8f, 0.7f);
            coneDef.softShadowLength = 180;
            coneDef.coneAngle = coneAngle;
            coneDef.distance = (300 - coneAngle) * 5;
            coneDef.rayNum = 60;
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
        e.add(world.createComponent(Scenegraph.Node.class, e));
        e.add(world.createComponent(DockVisitor.class, e));

        world.addComponent(PhysicsBody.Node.class, e);
        world.addComponent(Renderable.class, e);
        world.addComponent(Renderable.Node.class, e);
        world.addComponent(Enemy.class, e);
        e.add(world.createComponent(PhysicsBodyState.class, e));
        MapColor mapColor = world.addComponent(MapColor.class, e);
        mapColor.color = Color.RED;

        // TODO: CONTAINS A BOATLOAD OF STUFF, need to refactor!
        PhysicsSteerable steerable = world.createComponent(PhysicsSteerable.class, e);
        steerable.active = "Arrive";
        steerable.maxLinearAcceleration = 4;
        steerable.maxLinearVelocity = 4;

        FixtureDef def = new FixtureDef();
        //def.isSensor = true;
        def.shape = new CircleShape();
        def.shape.setRadius(3);

        // TODO: This is the category of the sensor
        def.filter.categoryBits = CATEGORY_SENSOR;
        // TODO: This is what the sensor will crash with
        def.filter.maskBits = CATEGORY_PLATFORM | CATEGORY_PLAYER | CATEGORY_ENEMY;//SENSOR SHOULD NOT COLLIDE WITH SENSOR | CATEGORY_SENSOR;

        Arrive arrive = new Arrive();
        AvoidCollision avoid = new AvoidCollision(def, 3);
        Face face = new Face();

        steerable.behaviourSets.put("Arrive",
                new PhysicsSteerable.SteeringDefinition[]{
                        new PhysicsSteerable.SteeringDefinition(1,arrive),
                        new PhysicsSteerable.SteeringDefinition(10,avoid),
                        new PhysicsSteerable.SteeringDefinition(1,face)
                }
        );

        steerable.behaviourSets.put("Arrive2",
                new PhysicsSteerable.SteeringDefinition[]{
                        new PhysicsSteerable.SteeringDefinition( 1, arrive),
                        new PhysicsSteerable.SteeringDefinition(1, avoid),
                        new PhysicsSteerable.SteeringDefinition(1,face)
                }
        );

        steerable.behaviourSets.put("Arrive3",
                new PhysicsSteerable.SteeringDefinition[]{
                        new PhysicsSteerable.SteeringDefinition(1, arrive),
                        new PhysicsSteerable.SteeringDefinition( 0.5f, avoid),
                        new PhysicsSteerable.SteeringDefinition(1,face)
                }
        );
        e.add(steerable);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;

        Shape structure = world.addComponent(Shape.class, e);

        Polygon rectangularLineShape = new Polygon(1, Color.RED, Color.FIREBRICK);
        rectangularLineShape.setVertices(new float[]{
                0, 0,
                0, 40,
                10, 60,
                32, 60,
                40, 40,
                40, 0}
        );

        rectangularLineShape.shiftCenterToCentroid();

        structure.geometry = rectangularLineShape;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class, e);
        physicalProperties.density = 2f;
        physicalProperties.friction = 0.01f;
        physicalProperties.categoryBits = CATEGORY_ENEMY;
        // Just cheat ffs, NPC's can't crash into anything except the player and sensors
        // CATEGORY_PLATFORM | CATEGORY_ENEMY |
        physicalProperties.maskBits = CATEGORY_PLATFORM | CATEGORY_PLAYER | CATEGORY_SENSOR | CATEGORY_LIGHT;

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class, e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.angularDamping = 3;
        physicsBodyDef.bodyDef.linearDamping = 0.1f;


        Entity l = createLight(world, new Transform(0, 0, 0), true, 0);

        e.getComponent(Spatial.Node.class).addChild(l);
        e.getComponent(Scenegraph.Node.class).addChild(l);

        Entity gun = createGun(world, new Transform(0, 40, -90));
        e.getComponent(Spatial.Node.class).addChild(gun);
        gun.getComponent(Spatial.Node.class).inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION_ANGLE;
        e.getComponent(Scenegraph.Node.class).addChild(gun);

        SoundComponent s = world.createComponent(SoundComponent.class, e);
        s.soundfile = "data/laser.mp3";
        s.play = false;
        s.pitch = 6;
        s.volume = 0.5f;
        e.add(s);

        return e;
    }

    private Entity createCharacter(Transform translation, Transform velocity) {
        Entity e = engine.addEntity();

        engine.addComponent(Renderable.class, e);
        engine.addComponent(Renderable.Node.class, e);
        engine.addComponent(Scenegraph.class, e);
        engine.addComponent(Scenegraph.Node.class, e);
        engine.addComponent(PhysicsBody.Node.class, e);

        MapColor mapColor = engine.addComponent(MapColor.class, e);
        mapColor.color = Color.CYAN;


        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation.cpy();

        PhysicsBodyState physicsBodyStateComponent = engine.addComponent(PhysicsBodyState.class, e);
        physicsBodyStateComponent.velocity = velocity;
        engine.addComponent(Player.class, e);

        Shape structure = engine.addComponent(Shape.class, e);
        structure.geometry = new Circle(1, 13, Color.GOLDENROD, Color.GOLDENROD);
        ;

        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class, e);
        physicalProperties.density = 1;
        physicalProperties.friction = 0.5f;
        physicalProperties.categoryBits = CATEGORY_PLAYER;
        physicalProperties.maskBits = CATEGORY_PLATFORM | CATEGORY_ENEMY | CATEGORY_SENSOR;

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class, e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBodyDef.bodyDef.fixedRotation = false;
        physicsBodyDef.bodyDef.angularDamping = 0.5f;

        SoundComponent s = engine.createComponent(SoundComponent.class, e);
        s.soundfile = "data/laser.mp3";
        s.play = false;
        s.pitch = 4;
        s.volume = 1;
        e.add(s);

        return e;
    }


    private Entity createIsland(Polygon polygon, Transform translation, Color color) {

        Entity e = engine.addEntity();
        engine.addComponent(Renderable.class, e);
        engine.addComponent(Renderable.Node.class, e);
        engine.addComponent(PhysicsBody.Node.class, e);

        Spatial.Node orientationRelative = engine.createComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;
        e.add(orientationRelative);

        Shape<Polygon> shape = engine.createComponent(Shape.class, e);
        shape.geometry = polygon;
        e.add(shape);

        PhysicalProperties physicalProperties = engine.addComponent(PhysicalProperties.class, e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;
        physicalProperties.categoryBits = CATEGORY_PLATFORM;
        physicalProperties.maskBits = CATEGORY_PLAYER | CATEGORY_SENSOR | CATEGORY_ENEMY | CATEGORY_LIGHT;

        PhysicsBody physicsBodyDef = engine.addComponent(PhysicsBody.class, e);
        physicsBodyDef.bodyDef.type = BodyDef.BodyType.StaticBody;

        return e;
    }

    private void debugWayPoints(IslandGenerator.Islands islands, GnompEngine engine) {
        if (islands != null)
            return;
        IslandGenerator.WayPoints wp = islands.wayPoints;
        int width = wp.width;
        int height = wp.height;
        boolean booleanMap[][] = wp.booleanMap;
        int waypointScale = wp.scale;


        int divisions = 32;
        int partWidth = width / divisions;
        int partHeight = height / divisions;

        Array<Entity> kids = new Array<Entity>();
        Vector2 segmentAbsolute = Pools.obtainVector();
        for (int w = 0; w < divisions; w++) {
            for (int h = 0; h < divisions; h++) {

                segmentAbsolute.set(
                        w * width - (width * divisions) / 2, h * height - (height * divisions) / 2
                );
                segmentAbsolute.scl(waypointScale / divisions);

                kids.clear();
                for (int x = partWidth * w; x < partWidth * (w + 1); x++) {
                    for (int y = partHeight * h; y < partHeight * (h + 1); y++) {

                        if (booleanMap[x][y]) {
                            Vector2 absolutePosition = Pools.obtainVector().set(x * waypointScale - waypointScale * width / 2, y * waypointScale - waypointScale * height / 2);
                            absolutePosition.sub(segmentAbsolute);
                            Entity e2 = createWaypoint(new Transform(absolutePosition, 0));
                            kids.add(e2);
                        }

                    }
                }

                if (kids.size > 0) {
                    Entity e = engine.addEntity();
                    Renderable r = engine.createComponent(Renderable.class, e);
                    r.zIndex = 10;
                    if (kids.size == partWidth * partHeight) {
                        r.textureId = 1;
                    }
                    Renderable.Node r2 = engine.createComponent(Renderable.Node.class, e);
                    Spatial.Node n = engine.createComponent(Spatial.Node.class, e);
                    Spatial n2 = engine.createComponent(Spatial.class, e);

                    for (Entity e2 : kids) {
                        n.children.add(e2);
                    }

                    n.local.set(segmentAbsolute, 0);
                    n.world.set(segmentAbsolute, 0);

                    e.add(r);
                    e.add(r2);
                    e.add(n);
                    e.add(n2);
                    engine.constructEntity(e);
                }
            }
        }


    }

    private Entity createWaypoint(Transform translation) {
        Entity e = engine.addEntity();
        Renderable.Node renderNode = engine.createComponent(Renderable.Node.class, e);
        Spatial.Node orientationRelative = engine.createComponent(Spatial.Node.class, e);
        Shape<Circle> shape = engine.createComponent(Shape.class, e);

        orientationRelative.local = translation;
        shape.geometry = new Circle(1, (float) 20, null, Color.FIREBRICK);

        e.add(orientationRelative);
        e.add(renderNode);
        e.add(shape);
        return e;
    }

}
