package com.flatfisk.gnomp.tests.launchers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flatfisk.gnomp.engine.constructors.gfx.AWTTextureFactory;
import com.flatfisk.gnomp.tests.TestReconstruct;

public class TestReconstructLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new TestReconstruct(new AWTTextureFactory()), config);
	}
}
