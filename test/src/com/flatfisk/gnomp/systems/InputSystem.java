package com.flatfisk.gnomp.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.*;
import com.flatfisk.gnomp.components.constructed.PhysicsBody;
import com.flatfisk.gnomp.components.relatives.PhysicsBodyRelative;
import com.flatfisk.gnomp.components.relatives.RenderableRelative;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.components.relatives.StructureRelative;
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
                Array<Entity> children = player.getComponent(SpatialRelative.class).children;
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
                                player.getComponent(SpatialRelative.class).addChild(e);
                                player.getComponent(SpatialRelative.class).addChild(e2);

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
                player.getComponent(SpatialRelative.class).world.vector.setZero();
                PhysicsBody body = player.getComponent(PhysicsBody.class);
                body.positionChanged = true;
            }
        }
    }

    protected Entity createCharacterDot(Spatial translation){

        GnompEngine world = ((GnompEngine) getEngine());

        Entity e = world.createEntity();

        world.addComponent(Dot.class,e);

        SpatialRelative orientationRelative = world.addComponent(SpatialRelative.class,e);
        orientationRelative.local = translation;
        orientationRelative.relativeType = Relative.CHILD;
        orientationRelative.inheritFromParentType = SpatialRelative.SpatialInheritType.POSITION_ANGLE;

        RenderableRelative renderableRelative = world.addComponent(RenderableRelative.class,e);
        renderableRelative.relativeType = Relative.CHILD;

        StructureRelative structure = world.addComponent(StructureRelative.class,e);
        com.flatfisk.gnomp.shape.CircleShape rectangularLineShape = new com.flatfisk.gnomp.shape.CircleShape(1,5, Color.GREEN,Color.BLACK);
        structure.shape = rectangularLineShape;
        structure.density = .1f;
        structure.friction = 5f;
        structure.relativeType = Relative.CHILD;

        PhysicsBodyRelative rel = world.addComponent(PhysicsBodyRelative.class, e);
        if(Math.random()>0.5) {
            rel.relativeType = Relative.INTERMEDIATE;
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
