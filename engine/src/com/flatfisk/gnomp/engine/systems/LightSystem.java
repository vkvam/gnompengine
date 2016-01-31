package com.flatfisk.gnomp.engine.systems;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.Scenegraph;

;

/**
 * Created by Vemund Kvam on 31/01/16.
 */
public class LightSystem extends EntitySystem implements ApplicationListener {
    private RayHandler rayHandler;
    Matrix4 debugMatrix;
    ConeLight l;
    PointLight l2;
    boolean d = true;

    public LightSystem(int priority, World box2Dworld) {
        super(priority);
        rayHandler = new RayHandler(box2Dworld);
rayHandler.setBlur(true);

        l = new ConeLight(rayHandler, 150, new Color(1,1,1,1f), 4, 0,-1.5f,90,40);
        l.setIgnoreAttachedBody(true);

        l2 = new PointLight(rayHandler, 150, new Color(.6f,.6f,.6f,.69f), 2.2f,0,0);
        l2.setIgnoreAttachedBody(true);

        Filter f = new Filter();
        f.maskBits=31;
        f.categoryBits=31;
        rayHandler.setShadows(true);
        l.setContactFilter(f);

    }

    @Override
    public void update(float deltaTime) {
        Body b = getEngine().getEntitiesFor(Family.all(Scenegraph.Node.class).get()).get(0).getComponent(PhysicsBody.Container.class).body;

            l2.attachToBody(b,0,0.1f);



        if(b.getLinearVelocity().x>0.1f){
            l.attachToBody(b,0,.1f,0);
        }else if(b.getLinearVelocity().x<-0.1f){
            l.attachToBody(b,0,0.1f,180);
        }



        debugMatrix = new Matrix4(getEngine().getSystem(CameraSystem.class).getCamera().combined);
        debugMatrix.scale(PhysicsConstants.PIXELS_PER_METER, PhysicsConstants.PIXELS_PER_METER, 1);

        rayHandler.setCombinedMatrix(debugMatrix);
        rayHandler.updateAndRender();
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {
        rayHandler.useCustomViewport(0,0,width,height);
    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
