package com.flatfisk.amalthea;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class ContactIdentity {
    public static ContactIdentity getIdentity(ComponentMapper mapperA, ComponentMapper mapperB, Entity a, Entity b){
        ContactIdentity t = new ContactIdentity();
        if(mapperA.has(a)){
            t.a = a;
            if (mapperB.has(b)){
                t.b = b;
            }
        }else if(mapperA.has(b)){
            t.a = b;
            if (mapperB.has(a)){
                t.b = a;
            }
        }
        return t;
    }
    public Entity a = null;
    public Entity b = null;
}