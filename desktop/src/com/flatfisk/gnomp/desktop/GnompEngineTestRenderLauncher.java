package com.flatfisk.gnomp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flatfisk.gnomp.GnompEngineTestRender;
import com.flatfisk.gnomp.engine.constructors.gfx.AWTTextureFactory;

public class GnompEngineTestRenderLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GnompEngineTestRender(new AWTTextureFactory()), config);
	}
}
