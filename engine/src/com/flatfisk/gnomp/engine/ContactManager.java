package com.flatfisk.gnomp.engine;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import static com.flatfisk.gnomp.engine.CollisionCategories.CATEGORY_SENSOR;

public class ContactManager implements ContactListener{
    Array<GnompContactListener> listeners = new Array<GnompContactListener>(4);

    public void addListener(GnompContactListener listener){
        listeners.add(listener);
    }

    public boolean hasListener(GnompContactListener listener){
        return listeners.contains(listener, true);
    }


    public void removeListener(GnompContactListener listener){
        listeners.removeValue(listener, true);
    }

    @Override
    public void beginContact(Contact contact) {
        for(GnompContactListener listener: listeners){
            listener.beginContact(contact.getFixtureA(), contact.getFixtureB(), contact);
        }
    }

    @Override
    public void endContact(Contact contact) {
        for(GnompContactListener listener: listeners){
            listener.endContact(contact.getFixtureA(), contact.getFixtureB(), contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getFilterData().categoryBits == CATEGORY_SENSOR || b.getFilterData().categoryBits == CATEGORY_SENSOR) {
            contact.setEnabled(false);
        }

        for(GnompContactListener listener: listeners){
            listener.preSolve(a,b, contact, oldManifold);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        for(GnompContactListener listener: listeners){
            listener.postSolve(a, b, contact, impulse);
        }
    }
}
