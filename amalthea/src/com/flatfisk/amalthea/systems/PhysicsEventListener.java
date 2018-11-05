package com.flatfisk.amalthea.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.message.HooverMessage;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.StatsSystem;
import com.flatfisk.gnomp.engine.message.Messenger;

public class PhysicsEventListener extends EntitySystem implements QueryCallback {
    private CameraSystem cameraSystem;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private World box2DWorld;
    private StatsSystem statsSystem;

    protected boolean isPressed = false;
    protected final Vector3 press = new Vector3();
    protected Messenger<HooverMessage> hooverMessenger;


    public PhysicsEventListener(World box2DWorld, CameraSystem cameraSystem, int priority) {
        super(priority);
        this.box2DWorld = box2DWorld;
        this.cameraSystem = cameraSystem;

    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        hooverMessenger = new Messenger<HooverMessage>(HooverMessage.class,(GnompEngine) engine);
    }

    @Override
    public void update(float f) {
        //findEvent();
        hooverMessenger.update();
        addStats();
    }

    /*
    private void findEvent(){
        press.set(Gdx.input.getX(),Gdx.input.getY(),0);
        isPressed = Gdx.input.isButtonPressed(0);
        // cameraSystem.getWorldCamera().unproject(press, vp.x, vp.y, vp.width, vp.getHeight());
        press.scl(PhysicsConstants.METERS_PER_PIXEL);
        float delta = PhysicsConstants.METERS_PER_PIXEL*0.5f; // The physicsConstructorMap equivalent of one pixel.
        box2DWorld.QueryAABB(this, press.x - delta, press.y - delta, press.x + delta, press.y + delta);
    }
    */


    private void addStats(){
        if(statsSystem!=null){
            statsSystem.addStat("Physics input");
            statsSystem.addLine();
        }
    }

    @Override
    public boolean reportFixture(Fixture fixture) {
        if(fixture.testPoint(press.x,press.y)) {
            Entity e = (Entity) fixture.getBody().getUserData();
            if(e!=null) {
                HooverMessage event = hooverMessenger.createMessage();
                event.entityHoovered = e;
                event.isPressed = isPressed;
                event.at.set(press.x,press.y);
                return false;
            }
        }
        return true;
    }


}
