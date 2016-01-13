package com.badlogic.ashley.core;

import com.badlogic.ashley.utils.ImmutableArray;

/**
 * Created by Vemund Kvam on 24/11/15.
 */
public abstract class FamilySystem extends EntitySystem {

    private Family family;
    private ImmutableArray<Entity> entities;

    /**
     * Instantiates a system that will iterate over the entities described by the Family.
     * @param family The family of entities iterated over in this System
     */
    public FamilySystem (Family family) {
        this(family, 0);
    }

    /**
     * Instantiates a system that will iterate over the entities described by the Family, with a specific priority.
     * @param family The family of entities iterated over in this System
     * @param priority The priority to execute this system with (lower means higher priority)
     */
    public FamilySystem (Family family, int priority) {
        super(priority);

        this.family = family;
    }

    @Override
    public void addedToEngine (Engine engine) {
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void removedFromEngine (Engine engine) {
        entities = null;
    }

    /**
     * @return set of entities processed by the system
     */
    public ImmutableArray<Entity> getEntities () {
        return entities;
    }

    /**
     * @return the Family used when the system was created
     */
    public Family getFamily () {
        return family;
    }
}
