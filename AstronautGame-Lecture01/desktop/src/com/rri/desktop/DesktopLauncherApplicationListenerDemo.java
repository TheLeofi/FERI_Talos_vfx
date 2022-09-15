package com.rri.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rri.ApplicationListenerDemo;
import com.rri.AstronautsGame;

public class DesktopLauncherApplicationListenerDemo {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		new LwjglApplication(new ApplicationListenerDemo(), config);
	}
}
