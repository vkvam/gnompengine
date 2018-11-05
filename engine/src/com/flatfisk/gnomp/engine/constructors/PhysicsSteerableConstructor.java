package com.flatfisk.gnomp.engine.constructors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.ContactManager;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.steering.Steering;
import com.flatfisk.gnomp.engine.steering.SteeringSet;
import com.flatfisk.gnomp.engine.steering.behaviours.Arrive;
import com.flatfisk.gnomp.engine.steering.behaviours.AvoidCollision;
import com.flatfisk.gnomp.engine.systems.PhysicsSystem;

import static com.flatfisk.gnomp.engine.CollisionCategories.*;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public class PhysicsSteerableConstructor extends Constructor<PhysicsSteerable,Spatial.Node,PhysicsSteerable.Container, PhysicsSteerable.Container> {


    private Logger LOG = new Logger(this.getClass().getName(),Logger.ERROR);

    private GnompEngine engine;

    public PhysicsSteerableConstructor(GnompEngine engine) {
        super(PhysicsSteerable.class, Spatial.Node.class, PhysicsSteerable.Container.class);
        this.engine = engine;
    }

    @Override
    public PhysicsSteerable.Container parentAdded(Entity entity, Spatial.Node constructor) {

        ContactManager contactManager = engine.getSystem(PhysicsSystem.class).manager;

        PhysicsSteerable steerable = constructorMapper.get(entity);
        World world = engine.getSystem(PhysicsSystem.class).getBox2DWorld();

        PhysicsSteerable.Container container = engine.createComponent(PhysicsSteerable.Container.class, entity);
        container.maxLinearAcceleration = steerable.maxLinearAcceleration;
        container.maxLinearVelocity = steerable.maxLinearVelocity;

        for(ObjectMap.Entry<String, PhysicsSteerable.SteeringDefinition[]> entry :steerable.behaviourSets.entries()){
            String setName = entry.key;

            SteeringSet set = new SteeringSet();

            for(PhysicsSteerable.SteeringDefinition def: entry.value){
                if(AvoidCollision.class.isInstance(def.behaviour)){
                    AvoidCollision avoidCollision = ((AvoidCollision) def.behaviour);
                    avoidCollision.setWorld(world);


                    if(!contactManager.hasListener(avoidCollision)) {
                        engine.getSystem(PhysicsSystem.class).manager.addListener(avoidCollision);
                    }

                }

                set.steerings.add(new SteeringSet.SteeringWithWeight(def.behaviour, def.weight));
            }
            container.putSteering(setName, set);
        }
        if (steerable.active!=null){
            container.setActiveSteering(steerable.active);
        }

        entity.add(container);


        return container;
    }

    @Override
    public void parentRemoved(Entity entity) {
        entity.remove(PhysicsSteerable.Container.class);
    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
