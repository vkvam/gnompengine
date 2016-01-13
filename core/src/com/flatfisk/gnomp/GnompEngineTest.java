package com.flatfisk.gnomp;

import com.badlogic.gdx.Gdx;
import com.flatfisk.gnomp.gdx.GnompEngineApplicationListener;
import com.flatfisk.gnomp.systems.RenderSystem;

public class GnompEngineTest extends GnompEngineApplicationListener {

    @Override
    public void create () {
        super.create();

        world.addSystem(new RenderSystem(1000));
    }

    @Override
    public void render () {
        world.update(Gdx.graphics.getDeltaTime());
    }
}
