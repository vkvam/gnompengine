package com.flatfisk.gnomp.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
<<<<<<< HEAD
import com.flatfisk.gnomp.utils.Pools;
=======
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
import com.flatfisk.gnomp.PhysicsConstants;

public class Translation {
    public Vector2 position;
    public float angle;

    public Translation() {
        this(0, 0, 0);
    }

    public Translation(float x, float y, float angle) {
        this.position = new Vector2(x, y);
        this.angle = angle;
    }

    public Translation(Vector2 vector, float angle) {
        this.position = vector;
        this.angle = angle;
    }

<<<<<<< HEAD
    public Translation subtractCopy(Translation subtractor){
        Translation copy = this.getCopy();

        copy.position.sub(subtractor.position);
        copy.position.rotate(-subtractor.angle);
        copy.angle-=subtractor.angle;
        return copy;
    }


    public Translation getCopy() {
        return new Translation(Pools.obtainFromCopy(position), angle);
    }

    public void setCopy(Translation t) {
        this.position = Pools.obtainFromCopy(t.position);
        this.angle = t.angle;
    }

    public void set(Translation t) {
        this.position = t.position;
        this.angle = t.angle;
    }

    public void set(Vector2 position, float angle) {
        this.position = position;
=======
    public Translation getCopy() {
        return new Translation(position.cpy(), angle);
    }

    public void setCopy(Translation t) {
        this.position = t.position.cpy();
        this.angle = t.angle;
    }

    public void set(Vector2 vector, float angle) {
        this.position = vector;
>>>>>>> fc14ad1272c990219874203be172ce562fabaf5a
        this.angle = angle;
    }

    public Translation toBox2D(){
        position.scl(PhysicsConstants.WORLD_TO_BOX);
        angle = angle * MathUtils.degreesToRadians;
        return this;
    }

    public Translation toWorld(){
        position.scl(PhysicsConstants.BOX_TO_WORLD);
        angle = angle * MathUtils.radiansToDegrees;
        return this;
    }
}
