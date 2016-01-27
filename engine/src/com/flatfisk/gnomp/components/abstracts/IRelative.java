package com.flatfisk.gnomp.components.abstracts;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Vemund Kvam on 06/12/15.
 */
public interface IRelative extends Component, Pool.Poolable {
    public Relative getRelativeType();

    /**
     * Created by Vemund Kvam on 04/12/15.
     */
    enum Relative {
        PARENT,         // Defines that node acts as parent
        INTERMEDIATE,   // Defines that node acts as an intermediate between parent and child.
        CHILD           // Defines that node acts as a child
    }
}
