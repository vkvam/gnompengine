package com.flatfisk.amalthea.factories.color;

import com.badlogic.gdx.graphics.Color;

public class Colors {
    private Color from;
    private Color to;

    public Colors(Color from, Color to) {
        this.from = from;
        this.to = to;
    }

    public Color gradientColor(float p) {
        return new Color(
                from.r * p + to.r * (1 - p),
                from.g * p + to.g * (1 - p),
                from.b * p + to.b * (1 - p),
                1.0f
        );
    }
}
