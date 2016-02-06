package com.flatfisk.gnomp.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.PhysicsConstants;
//import com.flatfisk.gnomp.utils.Pools;

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

    public Transform subtractedCopy(Transform subtractor){
        Transform copy = this.getCopy();
        copy.vector.sub(subtractor.vector);
        copy.vector.rotate(-subtractor.rotation);
        copy.rotation -=subtractor.rotation;
        return copy;
    }

    public Transform getCopy() {
        Transform t = Pools.obtain(Transform.class);
        t.vector.x = vector.x;
        t.vector.y = vector.y;
        t.rotation = rotation;
        return t;
    }

    public void setCopy(Transform t) {
        this.vector.x = t.vector.x;
        this.vector.y = t.vector.y;
        this.rotation = t.rotation;
    }

    public void set(Vector2 position, float angle) {
        this.vector = position;
        this.rotation = angle;
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

    @Override
    public void reset() {
        vector.x=0;
        vector.y=0;
        rotation=0;
    }

}
