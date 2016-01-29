package com.flatfisk.gnomp.tests.platformer;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;
import com.flatfisk.gnomp.tests.components.Player;

public class EnemyMoverSystem extends IteratingSystem {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public ComponentMapper<Enemy> enemyMapper;
    public ComponentMapper<PhysicsBody.Container> physicsBodyComponentMapper;
    private Family player = Family.all(Player.class).get();

    public EnemyMoverSystem(int priority) {
        super(Family.all(Enemy.class,PhysicsBody.Container.class).get(), priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        enemyMapper = ComponentMapper.getFor(Enemy.class);
        physicsBodyComponentMapper=ComponentMapper.getFor(PhysicsBody.Container.class);
    }

    float c = 0;
    public void update(float f){
        super.update(f);
        c+=f;
        if(c>.5f){
            GnompEngine world= (GnompEngine) getEngine();
            Transform t = getEngine().getEntitiesFor(player).get(0).getComponent(Spatial.Node.class).world;
            Entity e = TestPlatformer.createEnemy(world,new Transform((float) Math.random()*1000-500+t.vector.x,-150,0));
            world.addEntity(e);
            world.constructEntity(e);
            c=0;
        }
    }

    public void processEntity(Entity e,float f){
        PhysicsBody.Container physicsBody = physicsBodyComponentMapper.get(e);
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
            //physicsBody.body.setLinearVelocity(enemy.movingLeft?-.5f:.5f,physicsBody.body.getLinearVelocity().y);
            physicsBody.body.setLinearVelocity(-(float) Math.random(),physicsBody.body.getLinearVelocity().y);
        }else{
            physicsBody.body.setLinearVelocity((float) Math.random(),physicsBody.body.getLinearVelocity().y);
        }


    }



}
