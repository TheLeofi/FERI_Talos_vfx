package com.rri.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rri.AstronautsGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Space rescue mission";
		config.width = 1024;
		config.height = 480;
		config.forceExit=false;

		new LwjglApplication(new AstronautsGame(), config);
	}
}
