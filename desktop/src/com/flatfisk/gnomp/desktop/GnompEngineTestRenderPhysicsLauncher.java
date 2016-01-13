package com.flatfisk.gnomp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flatfisk.gnomp.GnompEngineTestRenderPhysics;
import com.flatfisk.gnomp.engine.constructors.gfx.AWTTextureFactory;

public class GnompEngineTestRenderPhysicsLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GnompEngineTestRenderPhysics(new AWTTextureFactory()), config);
	}
}
