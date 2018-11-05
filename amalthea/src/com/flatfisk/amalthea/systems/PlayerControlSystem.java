package com.flatfisk.amalthea.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.flatfisk.amalthea.ContactIdentity;
import com.flatfisk.amalthea.components.*;
import com.flatfisk.amalthea.factories.WeaponBuilder;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompContactListener;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.PhysicsSystem;
import com.flatfisk.gnomp.math.Transform;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class PlayerControlSystem extends EntitySystem implements GnompContactListener, EntityListener {
    private Logger LOG = new Logger(this.getClass().getName(), Logger.DEBUG);
    //private Entity player,gun;
    //playerLight
    //private Player playerComponent;
    private Family playerFamily;
    private Family gunFamily;

    private final ComponentMapper<LifeTime> lifeTimeComponentMapper = ComponentMapper.getFor(LifeTime.class);
    private final ComponentMapper<Enemy> enemyComponentMapper = ComponentMapper.getFor(Enemy.class);
    private final ComponentMapper<Bullet> bulletComponentMapper = ComponentMapper.getFor(Bullet.class);
    private final ComponentMapper<SoundComponent> soundComponentMapper = ComponentMapper.getFor(SoundComponent.class);

    // private Queue<Entity> bullets;
    // TODO: Booooooo!
    //  public Queue<Entity> explosionFragments;

    public PlayerControlSystem(int priority, PhysicsSystem system) {
        super(priority);
        playerFamily = Family.one(Player.class).get();
        gunFamily = Family.one(Gun.class).get();
        //physicsWorld.setContactListener(this);
        system.manager.addListener(this);
        // bullets = new Queue<Entity>(10);
        // explosionFragments = new Queue<Entity>(10);
    }


    public Family getFamily() {
        return playerFamily;
    }

    @Override
    public void beginContact(Fixture a, Fixture b, Contact contact) {
        Entity entityA = (Entity) a.getBody().getUserData();
        Entity entityB = (Entity) b.getBody().getUserData();

        if (entityA == null || entityB == null) {
            return;
        }


        Entity enemyKilled = null;
        ContactIdentity t = ContactIdentity.getIdentity(bulletComponentMapper, enemyComponentMapper, entityA, entityB);
        Entity bullet = t.a;
        Entity enemy = t.b;

        if (bullet != null && enemy != null) {
            Bullet bulletComponent = bulletComponentMapper.get(bullet);
            LifeTime lifeTime = lifeTimeComponentMapper.get(bullet);
            Enemy enemyComponent = enemyComponentMapper.get(enemy);
            if (!bulletComponent.inert) {
                bullet.remove(PhysicsBody.class);
                lifeTime.lifeTime = 1f;
                ((GnompEngine) getEngine()).reConstructEntity(bullet);
                lifeTime.lifeTime = 1f;
                if (++enemyComponent.shotTimes > 3) {
                    enemyKilled = enemy;
                }
            }
        }

        if (enemyKilled != null) {
            explode(enemyKilled.getComponent(Spatial.Node.class).world.cpy());
            getEngine().removeEntity(enemyKilled);
        }

    }

    @Override
    public void endContact(Fixture a, Fixture b, Contact contact) {

    }

    @Override
    public void preSolve(Fixture a, Fixture b, Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Fixture a, Fixture b, Contact contact, ContactImpulse impulse) {

    }


    private void explode(Transform transform) {

        GnompEngine world = ((GnompEngine) getEngine());
        Entity e = world.addEntity();

        world.addComponent(LifeTime.class, e).lifeTime = 1f;
        world.addComponent(Spatial.class, e);

        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
        orientationRelative.local = transform;
        orientationRelative.world = orientationRelative.local.cpy();

        Effect effect = world.addComponent(Effect.class, e);
        effect.effectFileName = "data/test2.p";
        effect.initialEmitters.addAll("simple");

        SoundComponent s = world.createComponent(SoundComponent.class, e);
        s.soundfile = "data/laser.mp3";
        s.play = true;
        s.pitch = 0.2f + (float) (Math.random() * 0.1f);
        s.volume = 0.5f;
        e.add(s);

        world.constructEntity(e);


        createBomb(transform);
    }


    private void createBomb(Transform transform) {
        Vector2 dir = new Vector2(500f, 0);
        for (int i = 0; i < 71; i++) {
            GnompEngine world = (GnompEngine) getEngine();
            Entity e = world.addEntity();
            e.removeAll();

            world.addComponent(LifeTime.class, e).lifeTime = 1;
            world.addComponent(PhysicsBody.Node.class, e);

            PhysicsBody b = world.addComponent(PhysicsBody.class, e);
            b.bodyDef.type = BodyDef.BodyType.DynamicBody;
            b.bodyDef.bullet = true;
            b.bodyDef.gravityScale = 0;
            b.bodyDef.fixedRotation = true;

            PhysicalProperties p = world.addComponent(PhysicalProperties.class, e);
            p.categoryBits = 127;
            p.maskBits = 127;
            p.density = 100f;
            p.friction = 100000000;

            PhysicsBodyState v = world.addComponent(PhysicsBodyState.class, e);
            v.velocity.vector.set(dir.rotate(5f));

            world.addComponent(Spatial.class, e);
            Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
            Transform t = transform.cpy();
            t.vector.add(dir.cpy().nor().scl(10));
            orientationRelative.local = t.cpy();
            orientationRelative.world = t.cpy();

            com.flatfisk.gnomp.engine.components.Shape<Circle> structure = world.createComponent(com.flatfisk.gnomp.engine.components.Shape.class, e);
            Circle c = structure.obtain(Circle.class);
            c.init(0, Color.WHITE, Color.DARK_GRAY);
            c.setRadius(0.5f);
            e.add(structure);
            world.constructEntity(e);

        }
    }


    float forward = 0, sideways = 0, timer = 0, shotInterval = 0.07f, shotCounter = 0f;

    private Vector2 lookAt = new Vector2(0, 0);


    private void movePlayer(float deltaTime, Entity player) {

        CameraSystem s = getEngine().getSystem(CameraSystem.class);
        Viewport p = s.hudViewPort;

        Vector3 v = s.getHudCamera().getPickRay(Gdx.input.getX(), Gdx.input.getY(),
                p.getScreenX(), p.getScreenY(), p.getScreenWidth(), p.getScreenHeight()).origin;

        v.x-=p.getWorldWidth()/2;
        v.y-=p.getWorldHeight()/2;

        lookAt = lookAt.interpolate(new Vector2(v.x, v.y).nor(), deltaTime * 10, Interpolation.linear).nor();

        player.getComponent(Player.class).lookAt = lookAt;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            forward = 50;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
            forward = -20;
        }else {
            forward = 0;
        }

        player.getComponent(Player.class).lookAt = lookAt;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            sideways = -30;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            sideways = 30;
        }else{
            sideways = 0;
        }

    }

    public void update(float deltaTime) {

        try {
            Entity player = getEngine().getEntitiesFor(playerFamily).get(0);
            Entity gun = getEngine().getEntitiesFor(gunFamily).get(0);

            Player playerComponent = player.getComponent(Player.class);


            PhysicsBody.Container physicsBody = player.getComponent(PhysicsBody.Container.class);
            if (physicsBody != null) {
                timer += deltaTime;


                movePlayer(deltaTime, player);

                Body b = physicsBody.body;
                if (b != null) {

                    if (!playerComponent.wasKilled) {
                        float speed = b.getLinearVelocity().len();
                        if (speed * PhysicsConstants.PIXELS_PER_METER < 1200) {
                            Vector2 force = lookAt.cpy().scl(forward * PhysicsConstants.METERS_PER_PIXEL);
                            force.add(lookAt.cpy().rotate90(-1).scl(sideways * PhysicsConstants.METERS_PER_PIXEL));
                            b.applyForceToCenter(force, true);
                        } else {
                            b.setLinearVelocity(b.getLinearVelocity().cpy().scl(0.9f));
                        }
                    } else {
                        playerComponent.wasKilled = false;
                    }


                    Vector2 playerLookAt = player.getComponent(Player.class).lookAt.cpy();
                    shotCounter += deltaTime;
                    if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shotCounter > shotInterval) {
                        Transform playerWorldPos = Pools.obtain(Transform.class);
                        playerWorldPos.set(player.getComponent(Spatial.Node.class).world);
                        shoot(playerWorldPos, playerLookAt, b.getLinearVelocity().cpy());
                        soundComponentMapper.get(player).play = true;
                        soundComponentMapper.get(player).pitch = 3f + (float) (Math.random() * 0.5f);
                        shotCounter = 0;
                    }

                    gun.getComponent(Spatial.Node.class).local.vector.set(playerLookAt.scl(10));
                    gun.getComponent(Spatial.Node.class).local.rotation = playerLookAt.angle();
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }


    }

    private void shoot(Transform playerWorldPos, Vector2 lookAtWorld, Vector2 speedBox2d) {
        playerWorldPos.vector.add(lookAtWorld.nor().cpy().scl(100f));

        getEngine().getSystem(CameraTrackerSystem.class).shake(.5f, 5);
        GnompEngine world = (GnompEngine) getEngine();
        Entity e = WeaponBuilder.playerBullet(world, playerWorldPos, lookAtWorld, speedBox2d);

        world.constructEntity(e);
        /*
        SoundComponent s = ((GnompEngine) getEngine()).addComponent(SoundComponent.class, e);
        s.soundfile = "data/laser.mp3";
        s.play = true;
        */


    }


    @Override
    public void entityAdded(Entity entity) {
        // TODO: Use groups?
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
