package com.flatfisk.gnomp.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;

public class PhysicsInputSystem extends EntitySystem {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private World box2DWorld;
    private StatsSystem statsSystem;
    protected final Vector3 press = new Vector3();

    private QueryCallback queryCallback;

    public PhysicsInputSystem(World box2DWorld, int priority) {
        super(priority);
        this.box2DWorld = box2DWorld;
        box2DWorld.setContinuousPhysics(true);
        final PhysicsInputSystem that = this;

        queryCallback = new MouseHooverCallBack(that);
    }

    public void setStatsSystem(StatsSystem statsSystem){
        this.statsSystem = statsSystem;
    }

    @Override
    public void update(float f) {
        checkPress();
        addStats();
    }

    public void checkPress(){
        CameraSystem s = getEngine().getSystem(CameraSystem.class);
        Rectangle vp = s.viewport;
        press.set(Gdx.input.getX(),Gdx.input.getY(),0);
        s.getWorldCamera().unproject(press, vp.x, vp.y, vp.width, vp.getHeight());
        press.scl(PhysicsConstants.METERS_PER_PIXEL);
        float delta = PhysicsConstants.METERS_PER_PIXEL*1f; // Equivalent of one pixel.
        box2DWorld.QueryAABB(this.queryCallback, press.x - delta, press.y - delta, press.x + delta, press.y + delta);
    }

    private void addStats(){
        if(statsSystem!=null){
            statsSystem.addStat("Physics input");
            statsSystem.addLine();
        }
    }

    private class MouseHooverCallBack implements QueryCallback {
        private PhysicsInputSystem physicsInputSystem;

        public MouseHooverCallBack(PhysicsInputSystem that) {
            physicsInputSystem = that;
        }

        @Override
        public boolean reportFixture(Fixture fixture) {
            if(fixture.testPoint(physicsInputSystem.press.x,physicsInputSystem.press.y)) {
                Entity e = (Entity) fixture.getBody().getUserData();
                if(e!=null) {
                    getEngine().removeEntity(e);

                }
            }
            return false;
        }
    }
}
