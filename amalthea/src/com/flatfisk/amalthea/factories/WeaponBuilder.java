package com.flatfisk.amalthea.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.flatfisk.amalthea.components.Bullet;
import com.flatfisk.gnomp.engine.CollisionCategories;
import com.flatfisk.gnomp.engine.components.LifeTime;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.*;
import com.flatfisk.gnomp.engine.shape.Circle;
import com.flatfisk.gnomp.math.Transform;

public class WeaponBuilder {

    public static Entity enemyBullet(GnompEngine world, Transform translation, Vector2 direction, Vector2 speed){
        Entity e = world.addEntity();
        e.removeAll();

        world.addComponent(LifeTime.class, e).lifeTime=2f;
        world.addComponent(PhysicsBody.Node.class, e);

        PhysicsBody b = world.addComponent(PhysicsBody.class, e);
        b.bodyDef.type = BodyDef.BodyType.DynamicBody;
        b.bodyDef.bullet = true;
        b.bodyDef.gravityScale = 0;
        b.bodyDef.fixedRotation=true;


        PhysicalProperties p = world.addComponent(PhysicalProperties.class, e);
        p.categoryBits = 127;
        p.maskBits = 127;
        p.density = 1f;
        p.friction = 10000000000.0f;

        PhysicsBodyState v = world.addComponent(PhysicsBodyState.class, e);
        v.velocity.vector.set(speed.cpy().scl(PhysicsConstants.PIXELS_PER_METER).add(direction.cpy().nor().scl(400)));

        world.addComponent(Spatial.class, e);
        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
        orientationRelative.local = translation;
        orientationRelative.world = translation;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;

        Effect effect = world.addComponent(Effect.class, e);
        effect.effectFileName = "data/test.p";
        effect.initialEmitters.addAll("simple");

        com.flatfisk.gnomp.engine.components.Shape structure = world.addComponent(com.flatfisk.gnomp.engine.components.Shape.class, e);
        structure.geometry = new Circle(0, 0.01f, Color.WHITE, Color.DARK_GRAY);
        return e;
    }

    //playerWorldPos, lookAtWorld, speedBox2d
    public static Entity playerBullet(GnompEngine world, Transform playerWorldPos, Vector2 lookAtWorld, Vector2 shooterSpeedBox2d){
        Entity e = world.addEntity();
        e.removeAll();

        world.addComponent(Bullet.class, e);
        world.addComponent(LifeTime.class, e).lifeTime=2f;
        world.addComponent(PhysicsBody.Node.class, e);

        PhysicsBody b = world.addComponent(PhysicsBody.class, e);
        b.bodyDef.type = BodyDef.BodyType.DynamicBody;
        b.bodyDef.bullet = true;
        b.bodyDef.gravityScale = 0;
        b.bodyDef.fixedRotation=true;


        PhysicalProperties p = world.addComponent(PhysicalProperties.class, e);
        p.categoryBits = CollisionCategories.CATEGORY_PLAYER;
        p.maskBits = CollisionCategories.CATEGORY_ENEMY;
        p.density = 1f;
        p.friction = 10000000000.0f;

        PhysicsBodyState v = world.addComponent(PhysicsBodyState.class, e);
        v.velocity.vector.set(shooterSpeedBox2d.cpy().add(lookAtWorld.cpy().nor().scl(20)));

        world.addComponent(Spatial.class, e);
        Spatial.Node orientationRelative = world.addComponent(Spatial.Node.class, e);
        orientationRelative.local = playerWorldPos;
        orientationRelative.world = playerWorldPos;
        orientationRelative.inheritFromParentType = Spatial.Node.SpatialInheritType.POSITION;

        Effect effect = world.addComponent(Effect.class, e);
        effect.effectFileName = "data/test.p";
        effect.initialEmitters.addAll("simple");

        com.flatfisk.gnomp.engine.components.Shape structure = world.addComponent(com.flatfisk.gnomp.engine.components.Shape.class, e);
        structure.geometry = new Circle(0, 0.01f, Color.WHITE, Color.DARK_GRAY);
        return e;
    }
}
