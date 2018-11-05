package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.abstracts.ISerializable;
import com.flatfisk.gnomp.engine.components.abstracts.ISpatialController;
import com.flatfisk.gnomp.engine.steering.Steering;
import com.flatfisk.gnomp.engine.steering.SteeringSet;

/**
 * Root node for constructing a PhysicsBody
 */
public class PhysicsSteerable implements ISerializable<PhysicsSteerable>, Pool.Poolable {

    public ObjectMap<String, SteeringDefinition[]> behaviourSets = new ObjectMap<String, SteeringDefinition[]>();
    public float maxLinearAcceleration;
    public float maxLinearVelocity;
    public String active;

    @Override
    public void reset() {

    }

    @Override
    public PhysicsSteerable addCopy(GnompEngine gnompEngine, Entity entity) {
        return null;
    }


    public static class SteeringDefinition {
        public final float weight;
        public final Steering behaviour;

        public SteeringDefinition(float weight, Steering behaviour) {
            this.weight = weight;
            this.behaviour = behaviour;
        }
    }

    public static class Container implements ISpatialController, Component, Pool.Poolable {
        public Vector2 target = Vector2.Zero.cpy();
        public Vector2 tmpTarget = Vector2.Zero.cpy();
        public float maxLinearAcceleration = 1;
        public float maxLinearVelocity = 1;
        public float maxAngularVelocity = 1;
        public float maxAngularAcceleration = 10;


        private ObjectMap<String, SteeringSet> steeringSets = new ObjectMap<String, SteeringSet>();

        public SteeringSet activeSteering = null;
        public String activeSteeringName = null;

        public void setActiveSteering(String name){
            activeSteeringName = name;
            activeSteering = steeringSets.get(name);
        }

        public void putSteering(String steeringSetName, SteeringSet steering){
            steeringSets.put(steeringSetName, steering);
        }

        @Override
        public void reset() {

        }
    }
}
