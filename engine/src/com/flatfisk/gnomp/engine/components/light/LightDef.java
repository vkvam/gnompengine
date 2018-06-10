package com.flatfisk.gnomp.engine.components.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;


public abstract class LightDef {

    public short categoryBits =1,group=1, maskBits =1;
    public Color color = new Color();
    public boolean active = true;
    public boolean soft = true;
    public boolean xray = false;
    public boolean staticLight = false;
    public boolean ignoreBody = false;
    public int rayNum = 10;
    public float distance = 2;
    public float softShadowLength = 2.5f;

    public static class Positional extends LightDef{
    }

    public static class Cone extends Positional{
        public float coneAngle;
    }

    public static class Point extends Positional{
    }

    public static class Chain extends LightDef{
        int rayDirection;
        Vector2 position = new Vector2();
    }
    
    public static class Directional extends LightDef{
        
    }

}
