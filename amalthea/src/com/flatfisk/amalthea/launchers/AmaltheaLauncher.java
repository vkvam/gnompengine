package com.flatfisk.amalthea.launchers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flatfisk.gnomp.engine.constructors.gfx.AWTTextureFactory;
import com.flatfisk.amalthea.Amalthea;
import com.flatfisk.gnomp.engine.shape.texture.SimpleTextureFactory;

public class AmaltheaLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Amalthea(new SimpleTextureFactory()), config);
	}
}
