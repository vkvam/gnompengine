package com.flatfisk.gnomp.engine;

public class CollisionCategories {
    public final static short CATEGORY_PLATFORM =     0x0001;  // 0000000000000001 in binary
    public final static short CATEGORY_PLAYER =       0x0002; //  0000000000000010 in binary
    public final static short CATEGORY_SENSOR =       0x0004; //  0000000000000100 in binary
    public final static short CATEGORY_ENEMY  =       0x0008; //  0000000000001000 in binary
    public final static short CATEGORY_LIGHT =        0x0010; //  0000000000010000 in binary
}
