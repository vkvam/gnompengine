package com.github.vkvam.gnompengine;


public class PhysicsConstants {
    public static float WORLD_TO_BOX = 0.01f;
    public static float BOX_TO_WORLD = 100f;

    public static void setBoxToWorlScale(double scale){
        BOX_TO_WORLD = (float) scale;
        WORLD_TO_BOX = (float )(1 / scale);
    }
}
