package com.flatfisk.gnomp.engine.systems;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public interface BackgroundProvider {
    public Sprite[] getTexture(Vector2 position);
}
