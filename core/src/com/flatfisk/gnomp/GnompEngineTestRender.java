package com.flatfisk.gnomp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.RenderConstruction;
import com.flatfisk.gnomp.components.Root;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.components.StructureNode;
import com.flatfisk.gnomp.factories.NodeFactory;
import com.flatfisk.gnomp.gdx.DefaultGnompEngineApplicationListener;
import com.flatfisk.gnomp.math.Translation;
import com.flatfisk.gnomp.exceptions.NodeConflictException;
import com.flatfisk.gnomp.shape.CircleShape;
import com.flatfisk.gnomp.shape.RectangularLineShape;
import com.flatfisk.gnomp.shape.texture.ShapeTextureFactory;

public class GnompEngineTestRender extends DefaultGnompEngineApplicationListener {

    private RenderableFactory renderableFactory;
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    public GnompEngineTestRender(ShapeTextureFactory shapeTextureFactory){
        this.shapeTextureFactory = shapeTextureFactory;
    }

    @Override
    public void create () {
        super.create();

        try {
            test();
        } catch (NodeConflictException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render () {
        world.update(Gdx.graphics.getDeltaTime());
    }

    public void test() throws NodeConflictException {

        renderableFactory = new RenderableFactory(world);

        Entity gc = renderableFactory.createChildNode();
        renderableFactory.addRenderableComponent(gc);

        Entity c = renderableFactory.createChildNode();
        renderableFactory.addRenderableComponent(c);
        c.getComponent(StructureNode.class).addChild(gc);

        Entity p = renderableFactory.createParentNode();
        renderableFactory.addRenderableComponent(p);
        Root scenegraphRoot = world.createComponent(Root.class);
        ScenegraphNode scenegraphNode = world.createComponent(ScenegraphNode.class);
        scenegraphNode.localTranslation = new Translation(0,0,180);

        p.add(scenegraphRoot);
        p.add(scenegraphNode);

        p.getComponent(StructureNode.class).addChild(c);

        world.addEntity(gc);
        world.addEntity(c);

        world.addEntity(p);
        LOG.info("# entities:" + world.getEntities().size());
    }

    public class RenderableFactory extends NodeFactory {

        public RenderableFactory(PooledEngine world) {
            super(world, StructureNode.class);
        }

        public Entity createParentNode() throws NodeConflictException {
            Entity e = world.createEntity();
            addParentComponent(e);

            StructureNode node = createNode(StructureNode.class, e);
            node.localTranslation = new Translation(0,0,0);
            node.worldTranslation = new Translation();
            RectangularLineShape shape = new RectangularLineShape(2, 75, Color.WHITE, Color.BLUE);
            shape.from=new Vector2(0,0);
            shape.to = new Vector2(0,150);
            shape.createPolygonVertices();
            node.shape = shape;
            e.add(node);
            return e;
        }

        public Entity createChildNode() throws NodeConflictException {
            Entity e = world.createEntity();
            addChildComponent(e);
            return e;
        }

        public Root addParentComponent(Entity e) throws NodeConflictException {
            Root root = createRoot(Root.class, e);
            e.add(root);
            return root;
        }

        public StructureNode addChildComponent(Entity e) throws NodeConflictException {
            StructureNode child = createNode(StructureNode.class, e);
            child.worldTranslation = new Translation();
            child.localTranslation = new Translation(60, 0, 0);
            child.shape = new CircleShape(1, 100f, Color.WHITE, Color.ORANGE.sub(0.3f,0.3f,.3f,0));
            e.add(child);
            return child;
        }

        public RenderConstruction addRenderableComponent(Entity e) {
            RenderConstruction s = world.createComponent(RenderConstruction.class);
            e.add(s);
            return s;
        }
    }
}
