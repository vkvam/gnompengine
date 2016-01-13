package com.flatfisk.gnomp.components;

import com.flatfisk.gnomp.math.Translation;

/**
 * Created by a-004213 on 22/04/14.
 */
public class ScenegraphNode extends Node {
    public Translation worldTranslation;
    public Translation localTranslation;
    public Translation velocity;

    public ScenegraphNode() {
        super(ScenegraphNode.class);
        worldTranslation = new Translation();
    }

    @Override
    public void reset() {
        super.reset();
        worldTranslation = new Translation();
        localTranslation = null;
        velocity = null;
    }



}
