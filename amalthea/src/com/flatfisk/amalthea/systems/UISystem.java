package com.flatfisk.amalthea.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Timer;
import com.flatfisk.gnomp.engine.systems.CameraSystem;


/**
 * Created by Vemund Kvam on 22/12/15.
 */
public class UISystem extends EntitySystem implements ApplicationListener {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private Skin skin;
    private Stage stage;
    private Engine engine;


    public UISystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        CameraSystem sys = engine.getSystem(CameraSystem.class);
        this.engine = engine;
        stage = new Stage(sys.hudViewPort);

        final TextButton button = new TextButton("Do", skin,"default");
        button.setWidth(200);
        button.setHeight(50);

        final Dialog dialog = new Dialog("Doing things",skin);
        dialog.text("Doing lots of things in a timely fashion, enjoy the wait");

        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.show(stage);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        dialog.hide();
                    }
                }, 5.5f);
            }
        });

        stage.addActor(button);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void update(float deltaTime) {
        stage.act(deltaTime);
        stage.draw();
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
