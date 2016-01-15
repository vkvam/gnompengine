package com.flatfisk.gnomp.components.constructed;


import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.math.Spatial;

public class PhysicsJoint implements Component, Pool.Poolable {
    public Entity a;
    public Entity b;
    public Entity anchor;
    public Spatial anchorOffset;
    public JointDef.JointType jointType;
    public Joint joint;

    @Override
    public void reset() {
        a = null;
        b = null;
        anchor = null;
        anchorOffset = null;
        jointType = JointDef.JointType.RevoluteJoint;
        joint = null;
    }
}
