package com.flatfisk.gnomp.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.utils.Pools;
/*
* Spatial properties with transformations.
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

    public Spatial addRotated(Vector2 vector, float rotation){
        this.vector.add(Pools.obtainVector2FromCopy(vector).rotate(rotation));
        this.rotation+=rotation;
        return this;
    }


    public Spatial getCopy() {
        return new Spatial(Pools.obtainVector2FromCopy(vector), rotation);
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
        vector.scl(PhysicsConstants.WORLD_TO_BOX);
        rotation = rotation * MathUtils.degreesToRadians;
        return this;
    }

    public Spatial toWorld(){
        vector.scl(PhysicsConstants.BOX_TO_WORLD);
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
