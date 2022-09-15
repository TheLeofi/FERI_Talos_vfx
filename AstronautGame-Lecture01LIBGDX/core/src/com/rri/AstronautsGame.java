package com.rri;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.rri.debug.DebugCameraController;
import com.rri.debug.DebugViewportUtils;
import com.rri.debug.MomoryInfo;


/**
 * Artwork from https://goodstuffnononsense.com/about/
 * https://goodstuffnononsense.com/hand-drawn-icons/space-icons/
 */
public class AstronautsGame extends ApplicationAdapter {
	private Texture astronautImage;
	private Texture rocketImage;
	private Texture asteroidImage;
	private Sound astronautSound;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle rocket;
	private Rectangle lastAsteroid;
	private Array<Rectangle> astronauts; //special LibGDX Array
	private Array<Rectangle> asteroids;
	private Array<Rectangle> pickups;
	private long lastAstronautTime;
	private long lastAsteroidTime;
	private int astronoutsRescuedScore;
	private int rocketHealth; //Starts with 100
	MomoryInfo momoryInfo;

	private BitmapFont font;

	//Values are set experimental
	private static int SPEED = 600; // pixels per second
	private static int SPEED_ASTRONAUT = 200; // pixels per second
	private static int SPEED_ASTROID = 100; // pixels per second
	private static long CREATE_ASTRONOUT_TIME = 1000000000; //ns
	private static long CREATE_ASTEROID_TIME = 2000000000; //ns


	//debug
	DebugCameraController dcc;
	ShapeRenderer sr;
	public Viewport vp;
	private boolean debug = false;


	//particle
	ParticleEffect peRocket;
	ParticleEffect peRoid;
	ParticleEffect peNaut;
	ParticleEffect peRoidExplo;
	ParticleEffect peRocketExplo;
	ParticleEffect test;
	Array<ParticleEffect> peNauts;

	boolean gameEnd = false;


	private void commandMoveLeft() {
		rocket.x -= SPEED * Gdx.graphics.getDeltaTime();
		if(rocket.x < 0) rocket.x = 0;
	}

	private void commandMoveReght() {
		rocket.x += SPEED * Gdx.graphics.getDeltaTime();
		if(rocket.x > Gdx.graphics.getWidth() - rocketImage.getWidth())
			rocket.x = Gdx.graphics.getWidth() - rocketImage.getWidth();
	}

	private void commandMoveLeftCorner() {
		rocket.x = 0;
	}
	private void commandMoveRightCorner() {
		rocket.x = Gdx.graphics.getWidth() - rocketImage.getWidth();
	}

	private void commandTouched() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		rocket.x = touchPos.x - rocketImage.getWidth() / 2;
	}

	private void commandExitGame() {
		Gdx.app.exit();
	}

	@Override
	public void create() {
		Gdx.app.setLogLevel(Logger.DEBUG);
		font = new BitmapFont();
		font.getData().setScale(2);
		astronoutsRescuedScore = 0;
		rocketHealth = 100;

		// default way to load texture
		rocketImage = new Texture(Gdx.files.internal("rocket64.png"));
		astronautImage = new Texture(Gdx.files.internal("astronaut48.png"));
		asteroidImage = new Texture(Gdx.files.internal("asteroid128.png"));
		astronautSound = Gdx.audio.newSound(Gdx.files.internal("pick.wav"));

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();

		//Debug
		momoryInfo = new MomoryInfo(500);
		dcc = new DebugCameraController();
		dcc.setStartPosition((float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2);
		vp = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		sr = new ShapeRenderer();
		// create a Rectangle to logically represents the rocket
		rocket = new Rectangle();
		rocket.x = Gdx.graphics.getWidth() / 2 - rocketImage.getWidth() / 2; // center the rocket horizontally
		rocket.y = 40; // bottom left corner of the rocket is 20 pixels above the bottom screen edge
		rocket.width = rocketImage.getWidth();
		rocket.height = rocketImage.getHeight();

		//izpušni ogenj rakete
		peRocket = new ParticleEffect();
		peRocket.load(Gdx.files.internal("rocket_libgdx.p"),Gdx.files.internal(""));
		peRocket.scaleEffect((float) 0.6);
		peRocket.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,rocket.y+10);
		peRocket.getEmitters().get(1).scaleSize(0);
		peRocket.start();
		//asteroid
		peRoid = new ParticleEffect();
		peRoid.load(Gdx.files.internal("asteroid_travel_libgdx.p"),Gdx.files.internal(""));
		peRoid.scaleEffect(3);
		peRoid.getEmitters().get(2).scaleSize(0);
		peNauts = new Array<ParticleEffect>();
		//eksplozija asteroida
		peRoidExplo = new ParticleEffect();
		peRoidExplo.load(Gdx.files.internal("asteroid_libgdx.p"),Gdx.files.internal(""));
		peRoidExplo.scaleEffect(3);
		//eksplozija rakete
		peRocketExplo = new ParticleEffect();
		peRocketExplo.load(Gdx.files.internal("rocket_explosion_libgdx.p"),Gdx.files.internal(""));
		peRocketExplo.scaleEffect(1);


		//other
		astronauts = new Array<Rectangle>();
		asteroids = new Array<Rectangle>();
		pickups = new Array<Rectangle>();
		//add first astronoutn and asteroid
		spawnAstronaut();
		spawnAsteroid();

	}

	private void spawnAstronaut() {
		Rectangle astronaut = new Rectangle();
		astronaut.x = MathUtils.random(0, Gdx.graphics.getWidth() - astronautImage.getWidth());
		astronaut.y = Gdx.graphics.getHeight();
		astronaut.width  = astronautImage.getWidth();
		astronaut.height = astronautImage.getHeight();
		astronauts.add(astronaut);
		lastAstronautTime = TimeUtils.nanoTime();
	}

	private void spawnAsteroid() {
		Rectangle asteroid = new Rectangle();
		asteroid.x = MathUtils.random(0, Gdx.graphics.getWidth()- asteroidImage.getWidth());
		asteroid.y = Gdx.graphics.getHeight();
		asteroid.width = asteroidImage.getWidth();
		asteroid.height = asteroidImage.getHeight();
		asteroids.add(asteroid);
		lastAsteroidTime = TimeUtils.nanoTime();
	}


	@Override
	public void render() { //runs every frame
		//clear screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		//debug
		if (debug) {
			momoryInfo.update();
			dcc.handleDebugInput(Gdx.graphics.getDeltaTime());
			dcc.applyTo(camera);
			batch.begin();
			{
				GlyphLayout layout = new GlyphLayout(font, "FPS:"+Gdx.graphics.getFramesPerSecond());
				font.setColor(Color.YELLOW);
				font.draw(batch,layout,Gdx.graphics.getWidth()-layout.width, Gdx.graphics.getHeight()-50);

				font.setColor(Color.YELLOW);
				font.draw(batch, "RC:" + batch.totalRenderCalls, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 20);
				momoryInfo.render(batch,font);
			}
			batch.end();

			batch.totalRenderCalls = 0;

			DebugViewportUtils.drawGrid(vp, sr, 50);

			//Print rectangles
			sr.setProjectionMatrix(camera.combined);
			//https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
			sr.begin(ShapeRenderer.ShapeType.Line);
			{
				sr.setColor(1, 1, 0, 1);
				for (Rectangle asteroid : asteroids) {
					sr.rect(asteroid.x, asteroid.y, asteroidImage.getWidth(), asteroidImage.getHeight());
				}
				for (Rectangle astronaut : astronauts) {
					sr.rect(astronaut.x, astronaut.y, astronautImage.getWidth(), astronautImage.getHeight());
				}
				for (Rectangle pickup : pickups) {
					sr.rect(pickup.x, pickup.y, astronautImage.getWidth(), astronautImage.getHeight());
				}
				sr.rect(rocket.x, rocket.y,rocketImage.getWidth(),rocketImage.getHeight());
			}
			sr.end();
		}

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the rocket, astronauts, asteroids
		batch.begin();
		{ //add brackets just for intent
			if(!gameEnd)
			{
				batch.draw(rocketImage, rocket.x, rocket.y);
				peRocket.setPosition(rocket.x+rocketImage.getWidth()/2, rocket.y+30);
				peRocket.update(Gdx.graphics.getDeltaTime()); //move line in update part
				peRocket.draw(batch);
			}
			for (Rectangle asteroid : asteroids) {
				batch.draw(asteroidImage, asteroid.x, asteroid.y);
				peRoid.setPosition(asteroid.x+asteroidImage.getWidth()/2, asteroid.y+asteroidImage.getHeight()/2);
				peRoid.update(Gdx.graphics.getDeltaTime()); //move line in update part
				peRoid.draw(batch);
			}
			for (Rectangle astronaut : astronauts) {
				batch.draw(astronautImage, astronaut.x, astronaut.y);
			}

			font.setColor(Color.YELLOW);
			font.draw(batch, "" + astronoutsRescuedScore, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
			font.setColor(Color.GREEN);
			font.draw(batch, "" + rocketHealth, 20, Gdx.graphics.getHeight() - 20);
			//particle




		}
		batch.end();
		if (peRocket.isComplete())
			peRocket.reset();
		if (peRoid.isComplete())
			peRoid.reset();

		if(!gameEnd)
		{
			// process user input
			if(Gdx.input.isTouched()) commandTouched(); //mouse or touch screen
			if(Gdx.input.isKeyPressed(Keys.LEFT)) {
				commandMoveLeft();

			}
			if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
				commandMoveReght();
				//if (peR.isComplete())
				//peR.reset();

			}
			//if(Gdx.input.isKeyPressed(Keys.A)) commandMoveLeftCorner();
			//if(Gdx.input.isKeyPressed(Keys.S)) commandMoveRightCorner();
			if(Gdx.input.isKeyPressed(Keys.ESCAPE)) commandExitGame();
			if(Gdx.input.isKeyJustPressed(Keys.F5)) debug=!debug;
		}


		// check if we need to create a new
		if(TimeUtils.nanoTime() - lastAstronautTime > CREATE_ASTRONOUT_TIME) spawnAstronaut();
		if(TimeUtils.nanoTime() - lastAsteroidTime > CREATE_ASTEROID_TIME) spawnAsteroid();

		if (rocketHealth > 0) { //is game end?
			// move and remove any that are beneath the bottom edge of
			// the screen or that hit the rocket.
			for (Iterator<Rectangle> iter = asteroids.iterator(); iter.hasNext(); ) {
				Rectangle asteroid = iter.next();
				asteroid.y -= SPEED_ASTROID * Gdx.graphics.getDeltaTime();
				if (asteroid.y + asteroidImage.getHeight() < -50) iter.remove();
				if (asteroid.overlaps(rocket)) {
					astronautSound.play();
					rocketHealth--;
					lastAsteroid = asteroid;
				}
			}

			for (Iterator<Rectangle> iter = astronauts.iterator(); iter.hasNext(); ) {
				Rectangle astronaut = iter.next();
				astronaut.y -= SPEED_ASTRONAUT * Gdx.graphics.getDeltaTime();
				if (astronaut.y + astronautImage.getHeight() < 0) iter.remove(); //From screen
				if (astronaut.overlaps(rocket)) {
					astronautSound.play();
					astronoutsRescuedScore++;
					pickups.add(astronaut);
					peNaut = new ParticleEffect();
					peNaut.load(Gdx.files.internal("astronaut_pickup_libgdx.p"),Gdx.files.internal(""));
					peNaut.scaleEffect(1.5F);
					peNaut.setPosition(astronaut.x+astronautImage.getWidth()/2, astronaut.y+astronautImage.getHeight()/2);
					peNaut.start();
					peNauts.add(peNaut);
					if (astronoutsRescuedScore%10==0) SPEED_ASTROID+=66; //speeds up
					iter.remove(); //smart Array enables remove from Array
				}
			}
		} else { //health of rocket is 0 or less
			batch.begin();
			{
				if(!gameEnd)
				{
					peRocketExplo.setPosition(rocket.x+rocketImage.getWidth()/2, rocket.y + rocketImage.getHeight()/2);
					peRoidExplo.setPosition(lastAsteroid.x+asteroidImage.getWidth()/2, lastAsteroid.y + asteroidImage.getHeight()/2);
					peRocketExplo.start();
					peRoidExplo.start();
					gameEnd = true;
				}
				for (Iterator<Rectangle> iter = asteroids.iterator(); iter.hasNext(); ) {
					Rectangle asteroid = iter.next();
					if (asteroid == lastAsteroid) iter.remove();

				}

				peRoidExplo.setPosition(lastAsteroid.x+asteroidImage.getWidth()/2, lastAsteroid.y + asteroidImage.getHeight()/2);
				peRoidExplo.update(Gdx.graphics.getDeltaTime());
				peRoidExplo.draw(batch);

				peRocketExplo.update(Gdx.graphics.getDeltaTime());
				peRocketExplo.draw(batch);

				font.setColor(Color.RED);
				font.draw(batch, "The END", Gdx.graphics.getHeight() / 2, Gdx.graphics.getHeight() / 2);
			}
			batch.end();
		}
		batch.begin();
		for (ParticleEffect peAstronaut : peNauts) {

			peAstronaut.update(Gdx.graphics.getDeltaTime());
			peAstronaut.draw(batch);
			if (peAstronaut.isComplete())
			{
				peNauts.removeIndex(peNauts.indexOf(peAstronaut, true));
			}
		}
		batch.end();
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		astronautImage.dispose();
		rocketImage.dispose();
		astronautSound.dispose();
		batch.dispose();
		font.dispose();
		sr.dispose();
	}
}
