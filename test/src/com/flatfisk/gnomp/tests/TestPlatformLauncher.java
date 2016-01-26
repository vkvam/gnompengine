package com.flatfisk.gnomp.tests;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flatfisk.gnomp.engine.constructors.gfx.AWTTextureFactory;

public class TestPlatformLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new TestPlatformer(new AWTTextureFactory()), config);
	}
}
