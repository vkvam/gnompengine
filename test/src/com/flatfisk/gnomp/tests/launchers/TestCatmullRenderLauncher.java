package com.flatfisk.gnomp.tests.launchers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flatfisk.gnomp.engine.constructors.gfx.AWTTextureFactory;
import com.flatfisk.gnomp.engine.shape.texture.SimpleTextureFactory;
import com.flatfisk.gnomp.tests.TestCatmullRender;

public class TestCatmullRenderLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new TestCatmullRender(new SimpleTextureFactory()), config);
	}
}
