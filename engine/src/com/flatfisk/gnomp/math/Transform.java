package com.flatfisk.gnomp.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.PhysicsConstants;

/**
* A vector and a rotation.
*/
public class Transform implements Pool.Poolable {
    public Vector2 vector;
    public float rotation;

    public Transform() {
        this(0, 0, 0);
    }

    public Transform(float x, float y, float rotation) {
        this.vector = new Vector2(x, y);
        this.rotation = rotation;
    }

    public Transform(Vector2 vector, float rotation) {
        this.vector = vector;
        this.rotation = rotation;
    }

    public Transform subtract(Transform subtractor){
        this.vector.sub(subtractor.vector);
        this.vector.rotate(-subtractor.rotation);
        this.rotation -=subtractor.rotation;
        return this;
    }

    public Transform add(Transform t){
        this.vector.add(t.vector);
        this.rotation+=t.rotation;
        return this;
    }

    public Transform add(Vector2 vector, float rotation){
        this.vector.add(vector);
        this.rotation+=rotation;
        return this;
    }

    public Transform set(Transform t) {
        this.vector.set(t.vector);
        this.rotation = t.rotation;
        return this;
    }

    public Transform set(Vector2 position, float angle) {
        this.vector.set(position);
        this.rotation = angle;
        return this;
    }

    public Transform toBox2D(){
        vector.scl(PhysicsConstants.METERS_PER_PIXEL);
        rotation = rotation * MathUtils.degreesToRadians;
        return this;
    }

    public Transform toWorld(){
        vector.scl(PhysicsConstants.PIXELS_PER_METER);
        rotation = rotation * MathUtils.radiansToDegrees;
        return this;
    }

    public Transform setZero(){
        vector.setZero();
        rotation = 0;
        return this;
    }



    @Override
    public void reset() {
        setZero();
    }

}
