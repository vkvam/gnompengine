package com.flatfisk.amalthea.systems;

import com.flatfisk.gnomp.engine.systems.CameraSystem;
import com.flatfisk.gnomp.engine.systems.LabelSystem;

public class LabelSystem2 extends LabelSystem {
    public LabelSystem2(int priority, CameraSystem cameraSystem, int scale) {
        super(priority, cameraSystem, scale);
        isMap = true;

    }
}
