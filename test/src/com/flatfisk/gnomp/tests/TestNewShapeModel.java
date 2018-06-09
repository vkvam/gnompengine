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
import com.flatfisk.gnomp.engine.shape.CatmullPolygon;
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

    @Override
    public void create () {
        super.create();
        PhysicsConstants.setPixelsPerMeter(100);
        createSystems(new Vector2(0, -1000f * PhysicsConstants.METERS_PER_PIXEL), false, true, false);

        World w = engine.getSystem(PhysicsSystem.class).getBox2DWorld();

        engine.getSystem(CameraSystem.class).getWorldCamera().zoom = 1f;

        PlatformerInputSystem inputSystem = new PlatformerInputSystem(0,w);
        engine.addSystem(inputSystem);
        engine.addEntityListener(inputSystem.getFamily(),0,inputSystem);

        engine.addSystem(new CameraTrackerSystem(1, engine.getSystem(CameraSystem.class).getWorldCamera(),true,true));
        for(int i=0;i<=50;i++) {
            for(int j=0;j<=50;j++) {
                createGame(new Transform((i-25)*10, (j-25)*10, i+j));
            }
        }

        engine.addSystem(new PhysicsEventListener(w,engine.getSystem(CameraSystem.class),200));
        engine.addSystem(new RemoveEntitySystem());


    }

    private Entity createGame(Transform position){

        Entity platform = createPlatform(position);
        engine.addComponent(Spatial.class,platform);
        engine.constructEntity(platform);
        return platform;
    }



    private Entity createPlatform(Transform translation){

        Entity entity = engine.addEntity();
        engine.addComponent(Renderable.class,entity);
        engine.addComponent(Renderable.Node.class,entity);
        engine.addComponent(PhysicsBody.Node.class,entity);

        Spatial.Node orientationRelative = engine.addComponent(Spatial.Node.class,entity);
        orientationRelative.local = translation;
        orientationRelative.world = translation;


        Shape<CatmullPolygon> shape = engine.addComponent(Shape.class, entity);
        CatmullPolygon rl = shape.obtain(CatmullPolygon.class);
        rl.fillColor = Color.RED;
        rl.lineColor = Color.WHITE;
        rl.setVertices(new float[]{
                100,0,
                75,75,
                0,100,
                -5,75,
                -160,0,
                -75,-7,
                0,-10,
                75,-175
        });
        rl.polygon.setScale(0.2f, 0.2f);
        shape.getGeometry().renderResolution=30;


        return entity;
    }

}
