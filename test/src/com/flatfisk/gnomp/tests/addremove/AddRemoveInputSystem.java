package com.flatfisk.gnomp.tests.addremove;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.shape.CircleShape;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.components.Dot;
import com.flatfisk.gnomp.tests.components.EndPoint;
import com.flatfisk.gnomp.tests.components.Player;
import com.flatfisk.gnomp.tests.components.PlayerSensor;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class AddRemoveInputSystem extends EntitySystem implements ContactListener, EntityListener{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private Entity player,sensor,endpoint;
    private Player playerComponent;
    private Family family;

    public AddRemoveInputSystem(int priority, World physicsWorld) {
        family = Family.one(Player.class, PlayerSensor.class, EndPoint.class).get();
        this.priority = priority;
        physicsWorld.setContactListener(this);
    }

    public Family getFamily() {
        return family;
    }

    boolean removed = false;
    int i=0;

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();




        if( entityA.equals(sensor) || entityB.equals(sensor)){
            playerComponent.touchedPlatformTimes++;
            LOG.info("TOUCH:"+playerComponent.touchedPlatformTimes);
                Array<Entity> children = player.getComponent(Spatial.Node.class).children;
                // TODO: This triggers two removes, need to have a trigger reconstruct flag.
                GnompEngine engine = (GnompEngine) getEngine();

                if (!removed) {
                    if (children.size > 1) {

                        Entity dot = null;

                        for(Entity e : children){

                            if(e.getComponent(Dot.class)!=null){
                                dot = e;
                                break;
                            }
                        }

                        if(dot!=null) {

                            i++;
                                Entity e = createCharacterDot(new Transform(20*i, 0,0));
                                Entity e2 = createCharacterDot(new Transform(-20*i, 0,0));
                                player.getComponent(Spatial.Node.class).addChild(e);
                                player.getComponent(Spatial.Node.class).addChild(e2);

                                engine.addEntity(e);
                                engine.addEntity(e2);
                                engine.removeEntity(dot);
                            if(Math.random()>0){
                                playerComponent.touchedPlatformTimes--;
                                engine.constructEntity(e);
                            }

                        }
                        removed = true;
                    }
                }
        }

        if(sensor!=null && player!=null && entityA.equals(endpoint) || entityB.equals(endpoint) ){
            if(entityA.equals(player) || entityB.equals(player)) {
                player.getComponent(Spatial.Node.class).world.vector.setZero();
                PhysicsBody.Container body = player.getComponent(PhysicsBody.Container.class);
                body.positionChanged = true;
            }
        }
    }

    protected Entity createCharacterDot(com.flatfisk.gnomp.math.Transform translation){

        GnompEngine world = ((GnompEngine) getEngine());

        Entity e = world.createEntity();

        world.addComponent(Dot.class,e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION_ANGLE;

        world.addComponent(Renderable.Node.class,e);

        Geometry structure = world.addComponent(Geometry.class,e);
        com.flatfisk.gnomp.engine.shape.CircleShape rectangularLineShape = new CircleShape(1,5, Color.GREEN,Color.BLACK);
        structure.shape = rectangularLineShape;

        PhysicalProperties physicalProperties = world.addComponent(PhysicalProperties.class,e);
        physicalProperties.density = .01f;
        physicalProperties.friction = 5f;

        PhysicsBody.Node rel = world.addComponent(PhysicsBody.Node.class, e);
        if(Math.random()>0.5) {
            rel.intermediate = true;
        }

        return e;
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
                    if (Gdx.input.isKeyPressed(Input.Keys.UP) && (playerComponent.touchedPlatformTimes > 0)) {
                        y = 400f* PhysicsConstants.METERS_PER_PIXEL;
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
