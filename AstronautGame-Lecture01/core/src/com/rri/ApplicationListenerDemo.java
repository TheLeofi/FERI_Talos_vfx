package com.rri;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/**
 * Demo of application live cycle!
 * Demonstrate logger util.
 */
public class ApplicationListenerDemo implements ApplicationListener {

	private Logger logger = new Logger(ApplicationListenerDemo.class.getSimpleName(),Logger.DEBUG); //Logger level
	private boolean renderOnce;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Logger.DEBUG);
		logger.debug("create");
		renderOnce = true;
	}

	@Override
	public void resize(int w, int h) {
		logger.debug("resize:"+w+","+h);
		renderOnce = true;

	}

	@Override
	public void render() {
		if (renderOnce) {
			logger.debug("render");
			renderOnce = false;
		}

	}

	@Override
	public void pause() {
		renderOnce = false;
		logger.debug("pause");

	}

	@Override
	public void resume() {
		renderOnce = true;
		logger.debug("resume");

	}

	@Override
	public void dispose() {
		logger.debug("dispose");

	}
}
