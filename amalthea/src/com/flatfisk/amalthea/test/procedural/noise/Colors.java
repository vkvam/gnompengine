package com.flatfisk.amalthea.test.procedural.noise;

import com.badlogic.gdx.graphics.Color;

public class Colors {
    private static Color gradientColor(float x,
                                       float minX,
                                       float maxX,
                                       Color from, Color to) {

        float range = maxX - minX;
        float p = (x - minX) / range;

        return new Color(
                from.r * p + to.r * (1 - p),
                from.g * p + to.g * (1 - p),
                from.b * p + to.b * (1 - p),
                1.0f
        );
    }

    public static void main(String[] a){
        Color start = Color.DARK_GRAY;
        Color end = Color.RED;
        System.out.println(Colors.gradientColor(0,0,100, start, end));
        System.out.println(Colors.gradientColor(20,0,100, start, end));
        System.out.println(Colors.gradientColor(40,0,100, start, end));
        System.out.println(Colors.gradientColor(60,0,100, start, end));
        System.out.println(Colors.gradientColor(80,0,100, start, end));
        System.out.println(Colors.gradientColor(100,0,100, start, end));
    }

}
