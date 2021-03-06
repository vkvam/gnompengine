package com.flatfisk.gnomp.engine.constructors;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.Constructor;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.Light;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.engine.components.light.LightDef;
import com.flatfisk.gnomp.math.Transform;


public class LightConstructor extends Constructor<Light,Spatial.Node,Light.Container, Light.Container> {
    private final RayHandler rayHandler;
    public final GnompEngine engine;

    public LightConstructor(RayHandler rayHandler, GnompEngine engine) {
        super(Light.class, Spatial.Node.class, Light.Container.class);
        this.engine = engine;
        this.rayHandler = rayHandler;
    }

    @Override
    public Light.Container parentAdded(Entity entity, Spatial.Node constructorOrientation) {
        LightDef lightDef = constructorMapper.get(entity).lightDef;
        box2dLight.Light box2dLight = null;

        Light.Container container = engine.addComponent(Light.Container.class,entity);

        if(lightDef instanceof LightDef.Positional){
            LightDef.Positional def = (LightDef.Positional) lightDef;


            Transform worldTransform = constructorOrientation.world;
            Vector2 worldVector = worldTransform.vector;


            if(lightDef instanceof LightDef.Point){
                box2dLight = new PointLight(rayHandler,def.rayNum,
                        def.color,def.distance* PhysicsConstants.METERS_PER_PIXEL,
                        worldVector.x,worldVector.y);
            }

            if(lightDef instanceof LightDef.Cone){
                LightDef.Cone coneDef = (LightDef.Cone) lightDef;
                box2dLight = new ConeLight(rayHandler,def.rayNum,
                        def.color,def.distance* PhysicsConstants.METERS_PER_PIXEL,
                        worldVector.x* PhysicsConstants.METERS_PER_PIXEL,worldVector.y* PhysicsConstants.METERS_PER_PIXEL,
                        0, coneDef.coneAngle);
                box2dLight.setDirection(worldTransform.rotation);
            }

        }


        box2dLight.setSoftnessLength(lightDef.softShadowLength*PhysicsConstants.METERS_PER_PIXEL);
        box2dLight.setSoft(lightDef.soft);
        box2dLight.setStaticLight(lightDef.staticLight);
        box2dLight.setContactFilter(lightDef.categoryBits,lightDef.group,lightDef.maskBits);

        container.light = box2dLight;

        return container;
    }

    @Override
    public void parentRemoved(Entity entity) {

    }

    @Override
    public void childRemoved(Entity entity) {

    }
}
