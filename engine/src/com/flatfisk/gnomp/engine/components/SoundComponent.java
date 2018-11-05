package com.flatfisk.gnomp.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class SoundComponent implements Component,Pool.Poolable{
    public String soundfile = "";
    public boolean play = false;
    public float pitch = 1;
    public float volume = 1;

    @Override
    public void reset() {
        soundfile = "";
        play = false;
    }
}
