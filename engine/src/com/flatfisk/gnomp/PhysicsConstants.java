package com.flatfisk.gnomp;


public class PhysicsConstants {
    public static float PIXELS_PER_METER = 100f;
    public static float METERS_PER_PIXEL = 0.01f;

    /**
     * Sets the scale of pixels in the game-world to meters in Box2d-world and vice versa
     *
     * @param pixels pr meter
     */
    public static void setPixelsPerMeter(double pixels){
        PIXELS_PER_METER = (float) pixels;
        METERS_PER_PIXEL = (float )(1 / pixels);
    }
}
