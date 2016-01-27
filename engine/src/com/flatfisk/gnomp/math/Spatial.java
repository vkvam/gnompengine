package com.flatfisk.gnomp.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.utils.Pools;

/**
* Spatial properties with transformation methods.
*/
public class Spatial implements Pool.Poolable {
    public Vector2 vector;
    public float rotation;

    public Spatial() {
        this(0, 0, 0);
    }

    public Spatial(float x, float y, float rotation) {
        this.vector = new Vector2(x, y);
        this.rotation = rotation;
    }

    public Spatial(Vector2 vector, float rotation) {
        this.vector = vector;
        this.rotation = rotation;
    }

    public Spatial subtractedCopy(Spatial subtractor){
        Spatial copy = this.getCopy();

        copy.vector.sub(subtractor.vector);
        copy.vector.rotate(-subtractor.rotation);
        copy.rotation -=subtractor.rotation;
        return copy;
    }

    public Spatial getCopy() {
        return Pools.obtainSpatialFromCopy(this);
    }

    public void setCopy(Spatial t) {
        this.vector = Pools.obtainVector2FromCopy(t.vector);
        this.rotation = t.rotation;
    }

    public void set(Vector2 position, float angle) {
        this.vector = position;
        this.rotation = angle;
    }
    public void setCopy(Vector2 position, float angle) {
        this.vector = Pools.obtainVector2FromCopy(position);
        this.rotation = angle;
    }

    public Spatial toBox2D(){
        vector.scl(PhysicsConstants.METERS_PER_PIXEL);
        rotation = rotation * MathUtils.degreesToRadians;
        return this;
    }

    public Spatial toWorld(){
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
