package com.flatfisk.gnomp.tests;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.shape.CatmullPolygon;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.catmull.SnowballGrowSystem;
import com.flatfisk.gnomp.tests.components.Player;
import com.flatfisk.gnomp.tests.systems.CameraTrackerSystem;

import java.util.Random;

public class TestCatmullRender extends Test {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public TestCatmullRender(ShapeTextureFactory shapeTextureFactory){
        this.shapeTextureFactory = shapeTextureFactory;
    }

    @Override
    public void create () {
        super.create();
        PhysicsConstants.setPixelsPerMeter(100);
        createSystems(new Vector2(0, -10), true);

        world.addSystem(new CameraTrackerSystem(1,world.getSystem(CameraSystem.class).getCamera(),true,true));
        world.addSystem(new SnowballGrowSystem(.05f,0));
        createGame();
    }

    private void createGame(){

        Entity oddBall = createOddBall(new Transform(0, -10, 0));
        world.addEntity(oddBall);
        world.constructEntity(oddBall);

        Entity platform = createPlatform(new Transform(50, -270, 10));
        world.addComponent(Spatial.class,platform);
        world.addEntity(platform);
        world.constructEntity(platform);

    }


    protected Entity createOddBall(Transform translation){

        Entity e = world.createEntity();
        world.addComponent(Spatial.class,e);

        world.addComponent(Renderable.class,e);
        world.addComponent(Velocity.class,e);
        world.addComponent(Renderable.Node.class,e);

        world.addComponent(Player.class,e);

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type= BodyDef.BodyType.DynamicBody;

        world.addComponent(PhysicsBody.Node.class,e);
        world.addComponent(PhysicalProperties.class,e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;

        Shape<CatmullPolygon> shape = world.addComponent(Shape.class,e);
        shape.geometry = new CatmullPolygon(5,Color.WHITE,Color.WHITE);
        shape.geometry.physicsResolution=5;
        shape.geometry.renderResolution=10;
        shape.geometry.setVertices(new float[]{
                100,0,
                75,75,
                0,100,
                -75,75,
                -100,0,
                -75,-75,
                0,-100,
                75,-75
        });

        shape.geometry.polygon.setScale(0.2f,0.2f);


        Entity e2 = createDot(world,new Transform(8,0,-90), .1f);
        orientationRelative.addChild(e2);
        world.addEntity(e2);


        return e;
    }

    protected Entity createPlatform(Transform translation){

        Entity e = world.createEntity();
        world.addComponent(Renderable.class,e);
        world.addComponent(Renderable.Node.class,e);
        world.addComponent(PhysicsBody.Node.class,e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;

        Shape<RectangularLine> shape = world.addComponent(Shape.class,e);
        shape.geometry = new RectangularLine(1,200,Color.WHITE,Color.WHITE);
        shape.geometry.from = new Vector2(500,0);
        shape.geometry.to = new Vector2(-15500,0);
        shape.geometry.createPolygonVertices();

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type= BodyDef.BodyType.StaticBody;

        return e;
    }

    public static Entity createDot(GnompEngine world, Transform translation, float scale){

        Entity e = world.createEntity();

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;

        world.addComponent(Renderable.Node.class,e);

        Shape<CatmullPolygon> shape = world.addComponent(Shape.class,e);
        Color c = new Color();
        c.r=0.9f;
        c.g=0.9f;
        c.b=0.9f;
        c.a=1;
        shape.geometry = new CatmullPolygon(5,null,c);
        shape.geometry.physicsResolution=5;
        shape.geometry.renderResolution=10;
        shape.geometry.setVertices(new float[]{
                50*new Random().nextFloat(),0,
                35*new Random().nextFloat(),75*new Random().nextFloat(),
                0,75*new Random().nextFloat(),
                -75*new Random().nextFloat(),75*new Random().nextFloat(),
                -75*new Random().nextFloat(),10*new Random().nextFloat(),
                -75*new Random().nextFloat(),-75*new Random().nextFloat(),
                0,-75*new Random().nextFloat(),
                75*new Random().nextFloat(),-75*new Random().nextFloat()
        });

        shape.geometry.polygon.setScale(scale,scale);

        return e;
    }


}
