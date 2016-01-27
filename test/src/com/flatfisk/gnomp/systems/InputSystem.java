package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.components.*;
import com.flatfisk.gnomp.components.PhysicsBody;
import com.flatfisk.gnomp.components.Renderable;
import com.flatfisk.gnomp.components.Structure;
import com.flatfisk.gnomp.components.abstracts.IRelative;
import com.flatfisk.gnomp.math.Spatial;

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
    int i=0;

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();




        if( entityA.equals(sensor) || entityB.equals(sensor)){
            playerComponent.touchedPlatformTimes++;
            LOG.info("TOUCH:"+playerComponent.touchedPlatformTimes);
                Array<Entity> children = player.getComponent(Constructor.Node.class).children;
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
                                Entity e = createCharacterDot(new Spatial(20*i, 0,0));
                                Entity e2 = createCharacterDot(new Spatial(-20*i, 0,0));
                                player.getComponent(Constructor.Node.class).addChild(e);
                                player.getComponent(Constructor.Node.class).addChild(e2);

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
                player.getComponent(Constructor.Node.class).world.vector.setZero();
                PhysicsBody.Container body = player.getComponent(PhysicsBody.Container.class);
                body.positionChanged = true;
            }
        }
    }

    protected Entity createCharacterDot(Spatial translation){

        GnompEngine world = ((GnompEngine) getEngine());

        Entity e = world.createEntity();

        world.addComponent(Dot.class,e);

        Constructor.Node orientationRelative = world.addComponent(Constructor.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.relativeType = IRelative.Relative.CHILD;
        orientationRelative.inheritFromParentType = Constructor.Node.SpatialInheritType.POSITION_ANGLE;

        Renderable.Node renderableRelative = world.addComponent(Renderable.Node.class,e);
        renderableRelative.relativeType = IRelative.Relative.CHILD;

        Structure.Node structure = world.addComponent(Structure.Node.class,e);
        com.flatfisk.gnomp.shape.CircleShape rectangularLineShape = new com.flatfisk.gnomp.shape.CircleShape(1,5, Color.GREEN,Color.BLACK);
        structure.shape = rectangularLineShape;
        structure.density = .01f;
        structure.friction = 5f;
        structure.relativeType = IRelative.Relative.CHILD;

        PhysicsBody.Node rel = world.addComponent(PhysicsBody.Node.class, e);
        if(Math.random()>0.5) {
            rel.relativeType = IRelative.Relative.INTERMEDIATE;
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
                    speed += speed > 0 ? -deltaTime * 100 : deltaTime * 100;
                    if (Math.abs(speed) < 5f) {
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
