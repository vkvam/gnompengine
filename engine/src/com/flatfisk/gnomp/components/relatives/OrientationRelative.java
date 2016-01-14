package com.flatfisk.gnomp.components.relatives;

import com.flatfisk.gnomp.components.Node;
import com.flatfisk.gnomp.components.Relative;
import com.flatfisk.gnomp.components.RelativeComponent;
import com.flatfisk.gnomp.math.Translation;

/**
 * Created by Vemund Kvam on 05/12/15.
 */
public class OrientationRelative extends Node implements RelativeComponent {
    public Relative relativeType = Relative.CHILD;
    public Translation localTranslation;
    public Translation worldTranslation = new Translation();
    public TranslationInheritType inheritFromParentType = TranslationInheritType.POSITION_ANGLE;
    protected OrientationRelative() {
        super(OrientationRelative.class);
    }

    @Override
    public void reset() {
        localTranslation.position.setZero();
        localTranslation.angle = 0;
        worldTranslation.position.setZero();
        worldTranslation.angle = 0;
        relativeType = Relative.CHILD;
    }

    @Override
    public Relative getRelativeType() {
        return relativeType;
    }

    public enum TranslationInheritType{
        POSITION,
        POSITION_ANGLE
    }
}
