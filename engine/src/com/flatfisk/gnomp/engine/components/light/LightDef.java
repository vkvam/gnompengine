package com.flatfisk.gnomp.engine.components.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.flatfisk.gnomp.math.Transform;

/**
* Created by Vemund Kvam on 31/01/16.
*/
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
    //public float direction = 0; Done through offset, since it is a Transform
    public float softShadowLength = 2.5f;


    public static class Positional extends LightDef{
        public Transform offset = Pools.obtain(Transform.class);
    }

    public static class Cone extends Positional{
        public float coneAngle;
    }

    public static class Point extends Positional{

    }

    public static class Chain extends LightDef{
        int rayDirection;
        Vector2 position = Pools.obtain(Vector2.class);
    }
    
    public static class Directional extends LightDef{
        
    }

}
