package com.flatfisk.gnomp.tests;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.shape.CatmullPolygon;
import com.flatfisk.gnomp.engine.shape.RectangularLine;
import com.flatfisk.gnomp.engine.shape.texture.ShapeTextureFactory;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.components.Player;
import com.flatfisk.gnomp.tests.systems.CameraTrackerSystem;

//import com.badlogic.gdx.math.Polygon;

public class TestCatmullRender extends Test {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public TestCatmullRender(ShapeTextureFactory shapeTextureFactory){
        this.shapeTextureFactory = shapeTextureFactory;
    }

    @Override
    public void create () {
        super.create();
        PhysicsConstants.setPixelsPerMeter(100);
        createSystems(new Vector2(0,-10), true);
        world.getSystem(CameraSystem.class).getCamera().zoom = 1f;
        world.addSystem(new CameraTrackerSystem(1,world.getSystem(CameraSystem.class).getCamera(),true,true));
        createGame();
    }

    private void createGame(){

        Entity oddBall = createOddBall(new Transform(0, -120, 0));
        world.addEntity(oddBall);
        world.constructEntity(oddBall);

        Entity platform = createPlatform(new Transform(0, -520, 0),Color.ORANGE);
        world.addComponent(Spatial.class,platform);
        world.addEntity(platform);
        world.constructEntity(platform);

    }


    protected Entity createOddBall(Transform translation){

        Entity e = world.createEntity();
        world.addComponent(Spatial.class,e);

        world.addComponent(Renderable.class,e);
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
        shape.geometry = new CatmullPolygon(1,Color.WHITE,Color.RED);
        shape.geometry.physicsResolution=5;
        shape.geometry.renderResolution=100;

        shape.geometry.setVertices(new float[]{
                100,0,
                175,75,
                0,100,
                -75,75,
                -100,0,
                -75,-75,
                0,-100,
                75,-75
        });

        return e;
    }

    protected Entity createPlatform(Transform translation,Color color){

        Entity e = world.createEntity();
        world.addComponent(Renderable.class,e);
        world.addComponent(Renderable.Node.class,e);
        world.addComponent(PhysicsBody.Node.class,e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;

        Shape<RectangularLine> shape = world.addComponent(Shape.class,e);
        shape.geometry = new RectangularLine(1,(float) 5,Color.WHITE,color);
        shape.geometry.from = new Vector2(-500,0);
        shape.geometry.to = new Vector2(500,0);
        shape.geometry.createPolygonVertices();

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = 0;
        physicalProperties.friction = 1;

        PhysicsBody physicsBodyDef = world.addComponent(PhysicsBody.class,e);
        physicsBodyDef.bodyDef.type= BodyDef.BodyType.StaticBody;

        return e;
    }

}
