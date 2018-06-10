package com.flatfisk.gnomp.engine;

import com.badlogic.ashley.core.ComponentMapper;
import com.flatfisk.gnomp.engine.components.*;


public class GnompMappers {
    public final static ComponentMapper<Renderable> renderableConstructorMap =
            ComponentMapper.getFor(Renderable.class);

    public final static ComponentMapper<PhysicsBody> physicsConstructorMap =
            ComponentMapper.getFor(PhysicsBody.class);

    public final static ComponentMapper<Light> lightConstructorMap =
            ComponentMapper.getFor(Light.class);

    public final static ComponentMapper<Spatial> spatialConstructorMap =
            ComponentMapper.getFor(Spatial.class);

    public final static ComponentMapper<Spatial.Node> spatialNodeMap =
            ComponentMapper.getFor(Spatial.Node.class);

    public final static ComponentMapper<Shape> shapeMap =
            ComponentMapper.getFor(Shape.class);

    public final static ComponentMapper<Velocity> velocityMap =
            ComponentMapper.getFor(Velocity.class);

    public final static ComponentMapper<PhysicalProperties> physicalPropertiesMap =
            ComponentMapper.getFor(PhysicalProperties.class);


    public final static ComponentMapper<Light.Container> lightMap =
            ComponentMapper.getFor(Light.Container.class);

    public final static ComponentMapper<Renderable.Container> renderableMap =
            ComponentMapper.getFor(Renderable.Container.class);

    public final static ComponentMapper<PhysicsBody.Container> physicsBodyMap =
                ComponentMapper.getFor(PhysicsBody.Container.class);

    public final static ComponentMapper<Effect.Container> effectMap =
            ComponentMapper.getFor(Effect.Container.class);

    public final static ComponentMapper<Scenegraph.Node> scenegraphNodeMap =
            ComponentMapper.getFor(Scenegraph.Node.class);

}
