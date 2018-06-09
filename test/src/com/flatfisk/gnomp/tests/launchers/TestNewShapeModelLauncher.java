package com.flatfisk.gnomp.tests.launchers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flatfisk.gnomp.engine.constructors.gfx.AWTTextureFactory;
import com.flatfisk.gnomp.tests.TestNewShapeModel;
import com.flatfisk.gnomp.tests.TestPlatformer;

public class TestNewShapeModelLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new TestNewShapeModel(new AWTTextureFactory()), config);
	}
}
