package com.flatfisk.gnomp.tests.platformer;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.tests.components.EndPoint;
import com.flatfisk.gnomp.tests.components.Player;
import com.flatfisk.gnomp.tests.components.PlayerSensor;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class PlatformerInputSystem extends EntitySystem implements ContactListener, EntityListener{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private Entity player,sensor,endpoint;
    private Player playerComponent;
    private Family family;

    ComponentMapper<Enemy> enemyComponentMapper = ComponentMapper.getFor(Enemy.class);

    public PlatformerInputSystem(int priority, World physicsWorld) {
        family = Family.one(Player.class, PlayerSensor.class, EndPoint.class).get();
        this.priority = priority;
        physicsWorld.setContactListener(this);
    }

    public Family getFamily() {
        return family;
    }

    int enemiesKilled = 0;

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();


        if((entityA.equals(player) && enemyComponentMapper.has(entityB) || entityB.equals(player) && enemyComponentMapper.has(entityA) )){
            if(playerComponent.touchedPlatformTimes>0) {
                player.getComponent(Spatial.Node.class).world.vector.setZero();
                PhysicsBody.Container body = player.getComponent(PhysicsBody.Container.class);
                body.positionChanged = true;
            }
        }

        if( entityA.equals(sensor) || entityB.equals(sensor) ){

            if(enemyComponentMapper.has(entityA)){
                getEngine().removeEntity(entityA);
                enemiesKilled++;
            }else if(enemyComponentMapper.has(entityB)){
                getEngine().removeEntity(entityB);
                enemiesKilled++;
            }else{
                playerComponent.touchedPlatformTimes++;
            }
            LOG.info("TOUCH:"+playerComponent.touchedPlatformTimes);
        }

        if(sensor !=null && player!=null && entityA.equals(endpoint) || entityB.equals(endpoint) ){
            if(entityA.equals(player) || entityB.equals(player)) {
                player.getComponent(Spatial.Node.class).world.vector.setZero();
                PhysicsBody.Container body = player.getComponent(PhysicsBody.Container.class);
                body.positionChanged = true;
            }
        }
    }


    @Override
    public void endContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if( (entityA!=null&&entityA.equals(sensor) || entityB!=null&&entityB.equals(sensor) )){
            playerComponent.touchedPlatformTimes--;
            LOG.info("UNTOUCH:"+playerComponent.touchedPlatformTimes);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    float speed = 0, timer=0;

    public void update (float deltaTime) {
        if(player!=null) {
            PhysicsBody.Container physicsBody = player.getComponent(PhysicsBody.Container.class);
            if(physicsBody!=null) {
                timer += deltaTime;

                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    speed = -150f;
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    speed = 150f;
                } else {
                    speed += speed > 0 ? -deltaTime * 400 : deltaTime * 400;
                    if (Math.abs(speed) < 150f) {
                        speed = 0;
                    }
                }

                float y;
                Body b = physicsBody.body;
                if(b!=null) {
                    if(enemiesKilled >0){
                        playerComponent.touchedPlatformTimes += enemiesKilled;
                        LOG.info("TOUCH:"+playerComponent.touchedPlatformTimes);
                    }
                    if(enemiesKilled >0 && Gdx.input.isKeyPressed(Input.Keys.UP)){
                        y = 600f* PhysicsConstants.METERS_PER_PIXEL;
                        enemiesKilled =0;
                    } else if (enemiesKilled >0 || Gdx.input.isKeyPressed(Input.Keys.UP) && (playerComponent.touchedPlatformTimes > 0) ) {
                        y = 400f* PhysicsConstants.METERS_PER_PIXEL;
                        enemiesKilled =0;
                    } else {
                        y = b.getLinearVelocity().y;
                    }

                    b.setLinearVelocity(speed* PhysicsConstants.METERS_PER_PIXEL, y);
                }
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        LOG.info("GET SOME");
        if(entity.getComponent(Player.class)!=null){
            player = entity;
            playerComponent = entity.getComponent(Player.class);
        }else if(entity.getComponent(PlayerSensor.class)!=null){
            sensor = entity;
        }else if(entity.getComponent(EndPoint.class)!=null){
            endpoint = entity;
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
