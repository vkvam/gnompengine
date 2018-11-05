package com.flatfisk.gnomp.tests.platformer;


import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.tests.components.Player;
import com.flatfisk.gnomp.tests.systems.HooverMessage;

import static com.flatfisk.gnomp.engine.GnompMappers.physicsBodyMap;

public class EnemyMoverSystem extends IteratingSystem implements EntityListener {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public ComponentMapper<Enemy> enemyMapper = ComponentMapper.getFor(Enemy.class);
    private ComponentMapper<HooverMessage> eventComponentMapper = ComponentMapper.getFor(HooverMessage.class);

    private Family player = Family.all(Player.class).get();


    public EnemyMoverSystem(int priority) {
        super(Family.all(Enemy.class,PhysicsBody.Container.class).get(), priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        Family hooverEventFamily = Family.all(HooverMessage.class).get();
        engine.addEntityListener(hooverEventFamily,priority,this);
    }

    float c = 0;
    public void update(float f){
        super.update(f);
        c+=f;
        if(c>0.25f){
/*
            GnompEngine world= (GnompEngine) getEngine();

            Transform t = getEngine().getEntitiesFor(player).get(0).getComponent(Spatial.Node.class).world;
            Entity e = TestPlatformer.createEnemy(world, new Transform(t.vector.x + (Math.random() > 0.5 ? (200 + (float) Math.random() * 1000) : (-200 - (float) Math.random() * 1000)), -190, 0));
            world.constructEntity(e);

            c=0;
            */
        }
    }

    public void processEntity(Entity e,float f){
        PhysicsBody.Container physicsBody = physicsBodyMap.get(e);
        Enemy enemy = enemyMapper.get(e);
        if(!enemy.startedMoving){
            enemy.startedMoving = true;
            enemy.startingPositionX = physicsBody.body.getPosition().x;
        }else{
            enemy.movedAmount = Math.abs(enemy.startingPositionX-physicsBody.body.getPosition().x);
            if(enemy.movedAmount>enemy.amountToMove){
                enemy.movingLeft=!enemy.movingLeft;
                enemy.startingPositionX = physicsBody.body.getPosition().x;
            }
        }

        if(getEngine().getEntitiesFor(player).get(0).getComponent(PhysicsBody.Container.class).body.getPosition().x<physicsBody.body.getPosition().x){
            physicsBody.body.setLinearVelocity(-(float) Math.random(),physicsBody.body.getLinearVelocity().y);
        }else{
            physicsBody.body.setLinearVelocity((float) Math.random(),physicsBody.body.getLinearVelocity().y);
        }


    }

    @Override
    public void entityAdded(Entity entity) {
        HooverMessage event = eventComponentMapper.get(entity);
        if(event.isPressed) {
            getEngine().removeEntity(event.entityHoovered);
        }else{
            event.entityHoovered.getComponent(PhysicsBody.Container.class).body.applyLinearImpulse(0, 1f, 0, 0, true);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {}
}
