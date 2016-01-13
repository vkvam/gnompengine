package com.flatfisk.gnomp;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.components.ScenegraphNode;
import com.flatfisk.gnomp.gdx.GdxSystem;

/**
* Created by Vemund Kvam on 02/12/15.
*/
public class TurnSystem extends IteratingSystem implements GdxSystem {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);

    private ComponentMapper<ScenegraphNode> scenegraphMapper;
    private ComponentMapper<TurnComponent> turnMapper;

    public TurnSystem(int priority) {
        super(Family.all(ScenegraphNode.class,TurnComponent.class).get(), priority);

    }

    @Override
    public void addedToEngine(Engine engine) {
        LOG.info("System added to engine");
        super.addedToEngine(engine);
        scenegraphMapper = ComponentMapper.getFor(ScenegraphNode.class);
        turnMapper = ComponentMapper.getFor(TurnComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TurnComponent turnComponent = turnMapper.get(entity);
        turnComponent.currentSpeed += (Math.random()-0.5) * turnComponent.turnAcceleration*deltaTime;
        scenegraphMapper.get(entity).localTranslation.angle += Math.sin(turnComponent.currentSpeed)*turnComponent.turnSpeed;
    }


    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
