package com.flatfisk.gnomp.engine;

import com.badlogic.gdx.physics.box2d.*;

public interface GnompContactListener {
    void beginContact(Fixture a, Fixture b, Contact contact);
    void endContact(Fixture a, Fixture b, Contact contact);
    void preSolve(Fixture a, Fixture b, Contact contact, Manifold oldManifold);
    void postSolve(Fixture a, Fixture b, Contact contact, ContactImpulse impulse);
}
