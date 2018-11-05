package com.flatfisk.amalthea.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.components.*;
import com.flatfisk.gnomp.engine.components.Light;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class PlayerLightSystem extends EntitySystem implements EntityListener{
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    List<Entity> playerLightList = new ArrayList();
    Entity player;
    private Vector2 output;
    Family family;

    public PlayerLightSystem(int priority, World physicsWorld) {
        super(priority);
        family = Family.one(PlayerLight.class,Player.class).get();
        output = Vector2.Zero;
    }


    float flickerTimer=0,timer=0;

    public Family getFamily(){
        return family;
    }

    public void update (float deltaTime) {
        timer += deltaTime;
        if(player!=null) {
            output = player.getComponent(Player.class).lookAt.cpy();
            output.x += (float) Math.cos(timer)*0.3;
            output.y += (float) Math.sin(timer)*0.3;

            for(Entity playerLight: playerLightList) {
                Light.Container light = playerLight.getComponent(Light.Container.class);
                if (light == null){
                    continue;
                }
                Transform lightOffset = playerLight.getComponent(Spatial.Node.class).local;

                interpolate(lightOffset, output, deltaTime * 5);

                if (Math.random() > 0.994f) {
                    flickerTimer = (float) Math.random() * 0.4f;
                }

                if (flickerTimer > 0) {
                    flickerTimer -= deltaTime;
                    light.light.getColor().a = (float) Math.random() * 0.2f + 0.5f;
                    light.light.setColor(light.light.getColor());
                } else {
                    flickerTimer = 0;
                    light.light.getColor().a = 0.7f;
                    light.light.setColor(light.light.getColor());
                }
            }
        }
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

        if(entity.getComponent(Player.class)!=null){
            player = entity;
        }else if(entity.getComponent(PlayerLight.class)!=null){
            playerLightList.add(entity);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
