package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.*;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class InputSystem extends EntitySystem implements ContactListener, EntityListener{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private Entity player,sensor,endpoint;
    private Player playerComponent;
    private Family family;

    public InputSystem(int priority,World physicsWorld) {
        family = Family.one(Player.class, PlayerSensor.class, EndPoint.class).get();
        this.priority = priority;
        physicsWorld.setContactListener(this);
    }

    public Family getFamily() {
        return family;
    }

    boolean removed = false;

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();
        int i=0;



        if( entityA.equals(sensor) || entityB.equals(sensor)){
            playerComponent.touchedPlatformTimes++;
            LOG.info("TOUCH:"+playerComponent.touchedPlatformTimes);
                Array<Node.EntityWrapper> children = player.getComponent(SpatialRelative.class).children;
                // TODO: This triggers two removes, need to have a trigger reconstruct flag.
                GnompEngine engine = (GnompEngine) getEngine();

                if (!removed) {
                    if (children.size > 1) {

                        Entity dot = null;

                        for(Node.EntityWrapper e : children){

                            if(e.getEntity(engine).getComponent(Dot.class)!=null){
                                i++;
                                dot = e.getEntity(engine);
                            }
                        }

                        if(dot!=null) {
                            while (dot.getComponent(SpatialRelative.class).children.size > 0) {
                                if (dot.getComponent(Dot.class) != null) {
                                    i++;
                                    dot = dot.getComponent(SpatialRelative.class).children.get(0).getEntity((GnompEngine) getEngine());
                                }
                            }
                            playerComponent.touchedPlatformTimes--;
                            engine.removeEntity(dot);
                        }
                        LOG.info("CHILD:"+i);
                        removed = true;
                    }
                }
        }

        if(sensor!=null && player!=null && entityA.equals(endpoint) || entityB.equals(endpoint) ){
            if(entityA.equals(player) || entityB.equals(player)) {
                player.getComponent(SpatialRelative.class).world.vector.setZero();
                PhysicsBody body = player.getComponent(PhysicsBody.class);
                body.positionChanged = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if( player!=null&&entityA!=null&&entityB!=null&&(entityA.equals(sensor) || entityB.equals(sensor) )){
            playerComponent.touchedPlatformTimes--;
            LOG.info("UNTOUCH:"+playerComponent.touchedPlatformTimes);
            removed=false;
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
            PhysicsBody physicsBody = player.getComponent(PhysicsBody.class);
            if(physicsBody!=null) {
                timer += deltaTime;
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    speed = -1.5f;
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    speed = 1.5f;
                } else {
                    speed += speed > 0 ? -deltaTime * 10 : deltaTime * 10;
                    if (Math.abs(speed) < 0.5f) {
                        speed = 0;
                    }
                }

                float y;
                Body b = physicsBody.body;
                if(b!=null) {
                    if (Gdx.input.isKeyPressed(Input.Keys.UP) && (playerComponent.touchedPlatformTimes > 0)) {
                        y = 4f;
                    } else {
                        y = b.getLinearVelocity().y;
                    }

                    b.setLinearVelocity(speed, y);
                }
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        LOG.info("GET SOME");
        if(entity.getComponent(Player.class)!=null){
            LOG.info("PLAYER");
            player = entity;
            playerComponent = entity.getComponent(Player.class);
        }else if(entity.getComponent(PlayerSensor.class)!=null){
            LOG.info("PLAYERSENSOR");
            sensor = entity;
        }else if(entity.getComponent(EndPoint.class)!=null){
            LOG.info("ENDPOINT");
            endpoint = entity;
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
