package com.flatfisk.gnomp.tests.platformer;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.components.*;
import com.flatfisk.gnomp.tests.systems.CameraTrackerSystem;

import java.util.Iterator;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class PlatformerInputSystem extends EntitySystem implements ContactListener, EntityListener{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private Entity player,sensor,endpoint, playerLight,gun;
    private Player playerComponent;
    private Family family;

    private final ComponentMapper<Enemy> enemyComponentMapper = ComponentMapper.getFor(Enemy.class);
    private final ComponentMapper<Bullet> bulletComponentMapper = ComponentMapper.getFor(Bullet.class);

    private Queue<Entity> bullets;
    public PlatformerInputSystem(int priority, World physicsWorld) {
        family = Family.one(PlayerLight.class,Player.class, PlayerSensor.class, EndPoint.class, Gun.class).get();
        this.priority = priority;
        physicsWorld.setContactListener(this);
        bullets = new Queue<Entity>(10);
    }

    public Family getFamily() {
        return family;
    }


    int enemiesKilled = 0;

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if(entityA==null||entityB==null){
            return;
        }

        if((entityA.equals(player) && enemyComponentMapper.has(entityB) || entityB.equals(player) && enemyComponentMapper.has(entityA) )){
            if(playerComponent.touchedPlatformTimes>0) {

                PhysicsBody.Container body = player.getComponent(PhysicsBody.Container.class);
                body.setPosition(0,0);
                player.getComponent(Player.class).wasKilled = true;
            }
        }

        Entity enemyKilled = null;

        if( entityA.equals(sensor) || entityB.equals(sensor) ){

            if(enemyComponentMapper.has(entityA)){
                enemyKilled = entityA;
                enemiesKilled++;
            }else if(enemyComponentMapper.has(entityB)){
                enemyKilled = entityB;
                enemiesKilled++;
            }else{
                playerComponent.touchedPlatformTimes++;
                PhysicsBody.Container body = player.getComponent(PhysicsBody.Container.class);
                getEngine().getSystem(CameraTrackerSystem.class).shake(Math.abs(body.getLinearVelocity().y)*0.1f,body.getLinearVelocity().y);
            }
            LOG.info("TOUCH:"+playerComponent.touchedPlatformTimes);
        }

        if(enemyKilled==null && ( bulletComponentMapper.has(entityA) || bulletComponentMapper.has(entityB) )){
            Enemy e = null;
            if(enemyComponentMapper.has(entityA) && !bulletComponentMapper.get(entityB).inert){
                enemyKilled = entityA;
                e = enemyComponentMapper.get(entityA);
                entityB.remove(PhysicsBody.class);
                entityB.getComponent(Bullet.class).lifeTime=2f;
                ((GnompEngine) getEngine()).reConstructEntity(entityB);
                ++e.shotTimes;

            }else if(enemyComponentMapper.has(entityB) && !bulletComponentMapper.get(entityA).inert){
                enemyKilled = entityB;
                e = enemyComponentMapper.get(entityB);
                entityA.remove(PhysicsBody.class);
                entityA.getComponent(Bullet.class).lifeTime=2f;
                ((GnompEngine) getEngine()).reConstructEntity(entityA);
                ++e.shotTimes;
            }

            if(e!=null && e.shotTimes<3){
                enemyKilled = null;
            }
        }

        if(enemyKilled!=null){
            getEngine().removeEntity(enemyKilled);
        }

        if(sensor !=null && player!=null && entityA.equals(endpoint) || entityB.equals(endpoint) ){
            if(entityA.equals(player) || entityB.equals(player)) {
                PhysicsBody.Container body = player.getComponent(PhysicsBody.Container.class);
                body.setPosition(0,0);
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

    float speed = 0, timer=0, flickerTimer=0, shotInterval = 0.005f, shotCounter = 0f;

    private Vector2 lookAt = new Vector2(),
            output = new Vector2();

    public void update (float deltaTime) {



        Iterator<Entity> it=bullets.iterator();
        while(it.hasNext()){
            Entity e = it.next();
            if(e!=null){
                Bullet bullet = bulletComponentMapper.get(e);
                if (bullet != null && (bullet.lifeTime -= deltaTime) < 0) {
                    getEngine().removeEntity(e);
                    it.remove();
                }
            }else{
                it.remove();
            }
        }

        if(player!=null) {

            PhysicsBody.Container physicsBody = player.getComponent(PhysicsBody.Container.class);
            if(physicsBody!=null) {
                timer += deltaTime;

                Light.Container light = playerLight.getComponent(Light.Container.class);
                com.flatfisk.gnomp.math.Transform lightOffset = playerLight.getComponent(Spatial.Node.class).local;

                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    speed = -150f;
                    lookAt.x=-2.4f;
                    lookAt.y=-0.4f;
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    speed = 150f;
                    lookAt.x=2.4f;
                    lookAt.y=-0.4f;
                } else {
                    speed += speed > 0 ? -deltaTime * 400 : deltaTime * 400;
                    if (Math.abs(speed) < 150f) {
                        speed = 0;
                    }
                }

                float y;
                Body b = physicsBody.body;
                if(b!=null) {
                    if(!playerComponent.wasKilled) {
                        if (enemiesKilled > 0) {
                            playerComponent.touchedPlatformTimes += enemiesKilled;
                        }
                        if (enemiesKilled > 0 && Gdx.input.isKeyPressed(Input.Keys.UP)) {
                            y = 600f * PhysicsConstants.METERS_PER_PIXEL;
                            enemiesKilled = 0;
                        } else if (enemiesKilled > 0 || Gdx.input.isKeyPressed(Input.Keys.UP) && (playerComponent.touchedPlatformTimes > 0)) {
                            y = 400f * PhysicsConstants.METERS_PER_PIXEL;
                            enemiesKilled = 0;
                        } else {
                            y = b.getLinearVelocity().y;
                        }

                        b.setLinearVelocity(speed * PhysicsConstants.METERS_PER_PIXEL, y);
                    }else {
                        playerComponent.wasKilled = false;
                    }


                    output.x = lookAt.x+b.getLinearVelocity().x;
                    output.y = lookAt.y+b.getLinearVelocity().y;


                    Vector2 p = output.cpy().add(0,0.4f).nor();
                    shotCounter+=deltaTime;
                    if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shotCounter>shotInterval) {
                        Transform t = Pools.obtain(Transform.class);
                        t.set(player.getComponent(Spatial.Node.class).world);
                        shoot(t, p);
                        shotCounter = 0;
                    }

                    gun.getComponent(Spatial.Node.class).local.vector.set(p.scl(10));
                    gun.getComponent(Spatial.Node.class).local.rotation = p.angle();

                    output.y+=(float) Math.sin(timer*(.4f+Math.abs(b.getLinearVelocity().x*8)))*0.6;

                    interpolate(lightOffset, output, deltaTime * 5);

                    if(Math.random()>0.99f){
                        flickerTimer=(float) Math.random()*0.4f;
                    }

                    if(flickerTimer>0) {
                        flickerTimer-=deltaTime;
                        light.light.getColor().a = (float) Math.random()*0.2f+0.7f;
                        light.light.setColor(light.light.getColor());
                    }else{
                        flickerTimer=0;
                        light.light.getColor().a = 0.9f;
                        light.light.setColor(light.light.getColor());
                    }

                }
            }
        }
    }

    private void shoot(Transform t, Vector2 direction){
        t.vector.add(direction.cpy().scl(20));
        getEngine().getSystem(CameraTrackerSystem.class).shake(.05f,5);
        createBullet(t,direction);
    }

    protected Entity createBullet(Transform translation,Vector2 direction){
        GnompEngine world = (GnompEngine) getEngine();
        Entity e = world.addEntity();
        e.removeAll();


        world.addComponent(Bullet.class,e);
        world.addComponent(PhysicsBody.Node.class,e);

        PhysicsBody b = world.addComponent(PhysicsBody.class,e);
        b.bodyDef.type= BodyDef.BodyType.DynamicBody;
        b.bodyDef.bullet = true;
        b.bodyDef.gravityScale=0;


        PhysicalProperties p = world.addComponent(PhysicalProperties.class,e);
        p.categoryBits=127;
        p.maskBits=127;
        p.density=100;

        PhysicsBodyState v = world.addComponent(PhysicsBodyState.class,e);
        v.velocity.vector.set(direction).nor().scl(400);

        world.addComponent(Spatial.class,e);
        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class,e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;

        Effect effect = world.addComponent(Effect.class,e);
        effect.effectFileName = "data/test.p";
        effect.initialEmitters.addAll("simple");

        com.flatfisk.gnomp.engine.components.Shape structure = world.addComponent(com.flatfisk.gnomp.engine.components.Shape.class,e);
        Circle circle = new Circle(1,3, Color.WHITE,Color.DARK_GRAY);
        structure.geometry = circle;


        bullets.addFirst(e);

        world.constructEntity(e);

        return e;
    }

    public Transform interpolate(Transform in, Vector2 vector2,float amount){
        float xDiff = vector2.x-in.vector.x;
        float yDiff = vector2.y-in.vector.y;

        in.vector.x+=xDiff*amount;
        in.vector.y+=yDiff*amount;
        in.rotation=in.vector.angle();
        return in;
    }

    @Override
    public void entityAdded(Entity entity) {
        // TODO: Use groups?
        LOG.info("GET SOME");
        if(entity.getComponent(Player.class)!=null){
            player = entity;
            playerComponent = entity.getComponent(Player.class);
        }else if(entity.getComponent(PlayerSensor.class)!=null){
            sensor = entity;
        }else if(entity.getComponent(EndPoint.class)!=null){
            endpoint = entity;
        }else if(entity.getComponent(PlayerLight.class)!=null){
            playerLight = entity;
        }else if(entity.getComponent(Gun.class)!=null){
            gun = entity;
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
