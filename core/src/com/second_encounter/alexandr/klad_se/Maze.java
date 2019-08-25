package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.second_encounter.alexandr.klad_se.game.Data;
import com.second_encounter.alexandr.klad_se.lib.GameMusic;
import com.second_encounter.alexandr.klad_se.lib.GameSound;
import com.second_encounter.alexandr.klad_se.lib.GreyscaleShader;
import com.second_encounter.alexandr.klad_se.lib.Screenshot;
import com.second_encounter.alexandr.klad_se.screens.MainScreen;
import com.second_encounter.alexandr.klad_se.screens.StartScreen;

public class Maze extends Game {

    public ActivityRequestHandler handler;
    public Config config;
    public Data data;
	public AssetManager assetManager;
	public GameMusic music;
	public GameSound sound;
	public TextureAtlas commonAtlas, miniAtlas, languageAtlas;
	public VExtend extendViewport, zoomExtendViewport;
    public SpriteBatch batch;
    public Stage stage;
    public Skin skinCommon, skinLanguage;
    public LanguageBundle bundle;
    public Shadow shadow;

    private Preferences preferences;
    private Timer aTimer, bTimer;
    private GreyscaleShader greyscaleShader;
    private BitmapFont bitmapFont;

    public Maze(ActivityRequestHandler handler) {
        this.handler = handler;
    }

	@Override
	public void create () {
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        //
        config = new Config(this);
        data = new Data(this);
		assetManager = new AssetManager();
		data.load();
		music = new GameMusic(this);
		music.load();
		sound = new GameSound(this);
		sound.load();
		assetManager.load("common/sprite.txt", TextureAtlas.class);
        assetManager.load("common/mini.txt", TextureAtlas.class);
        assetManager.load("common/skin.json", Skin.class);
        assetManager.load("common/oa_one_team.png", Texture.class);
        assetManager.load("common/space.png", Texture.class);
        assetManager.load("common/panel_bottom.png", Texture.class);
        assetManager.load("common/shadow.png", Texture.class);
        assetManager.load("common/border.png", Texture.class);
        assetManager.load("common/frame.png", Texture.class);
        assetManager.load(config.getLanguage() + "/sprite.txt", TextureAtlas.class);
		assetManager.load(config.getLanguage() + "/skin.json", Skin.class);
		assetManager.load(config.getLanguage() + "/panel_top.png", Texture.class);
		assetManager.finishLoading();
		bundle = new LanguageBundle("localize_" + config.getLanguage() + ".xml");
		//
        extendViewport = new VExtend(GS.WIDTH, GS.HEIGHT);
        zoomExtendViewport = new VExtend(GS.WIDTH, GS.HEIGHT);
        zoomExtendViewport.camera.zoom = 1f;
        batch = new SpriteBatch();
        stage = new Stage(extendViewport);
        stage.setDebugAll(false);
        skinCommon = assetManager.get("common/skin.json", Skin.class);
        skinCommon.getFont("font").getData().markupEnabled = true;
        skinCommon.getFont("font_32").getData().markupEnabled = true;
        skinLanguage = assetManager.get(config.getLanguage() + "/skin.json", Skin.class);
        commonAtlas = assetManager.get("common/sprite.txt", TextureAtlas.class);
        miniAtlas = assetManager.get("common/mini.txt", TextureAtlas.class);
        languageAtlas = assetManager.get(config.getLanguage() + "/sprite.txt", TextureAtlas.class);
        data.initialize();
        music.create();
        sound.create();
        shadow = new Shadow(this);
        aTimer = new Timer();
        bTimer = new Timer();
        greyscaleShader = new GreyscaleShader();
        bitmapFont = new BitmapFont(Gdx.files.internal("common/font_32.fnt"), skinCommon.getRegion("font_32"));
        //
        setGreyscale();
		setSplashScreen(handler.isBuildConfig() ? new MainScreen(this) : new StartScreen(this));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		extendViewport.apply(true);
        batch.setProjectionMatrix(extendViewport.camera.combined);
		super.render();
        stage.act();
        stage.draw();
		// TODO draw fps
		if (GS.showFPS) {
            batch.begin();
            bitmapFont.draw(batch, "Fps: " + Gdx.graphics.getFramesPerSecond(), 16, extendViewport.getWorldHeight() - 20);
            batch.end();
        }
		// TODO screenshoot
		if (Gdx.input.isKeyPressed(Input.Keys.S))
		    Screenshot.saveScreenshot(0, 0, (int) extendViewport.getWorldWidth(), (int) extendViewport.getWorldHeight(), true);
	}

	@Override
	public void resize (int width, int height) {
        extendViewport.update(width, height, true);
        zoomExtendViewport.update(width, height, true);
        super.resize(width, height);
        shadow.resize();
	}

	@Override
	public void pause () {
        config.save();
	}

	@Override
	public void resume () {

	}
	
	@Override
	public void dispose () {
		assetManager.dispose();
        batch.dispose();
        stage.dispose();
		commonAtlas.dispose();
		miniAtlas.dispose();
		languageAtlas.dispose();
		skinCommon.dispose();
		skinLanguage.dispose();
        bitmapFont.dispose();
        bundle.dispose();
        music.dispose();
        sound.dispose();
	}

    public Preferences prefs() {
        if (preferences == null)
            preferences = Gdx.app.getPreferences("com.oa.maze");
        return preferences;
    }

    public void setGreyscale() {
        if (config.greyscale) {
            batch.setShader(greyscaleShader);
            stage.getBatch().setShader(greyscaleShader);
        }
        else {
            batch.setShader(null);
            stage.getBatch().setShader(null);
        }
    }

    public void setSplashScreen(Screen screen) {
        stage.clear();
        setScreen(new SplashScreen(screen));
    }

    public void delayedRunnable(float delay, int timer, final Runnable runnable) {
        switch (timer) {
            case 1:
                aTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                }, delay);
                break;
            case 2:
                bTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                }, delay);
                break;
        }
    }

    public void clearTimer(int timer) {
        switch (timer) {
            case 1:
                aTimer.clear();
                break;
            case 2:
                bTimer.clear();
                break;
        }
    }

    public Runnable nullRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                // TODO OAOneTeam null runnable
            }
        };
    }

    // Splash Screen
    class SplashScreen implements Screen {

        private static final long waitTime = 500L;

        private Screen screen;
        private long startTime = TimeUtils.millis();
        private boolean b = false;

        public SplashScreen(Screen screen) {
            this.screen = screen;
        }

        @Override
        public void show() {

        }

        @Override
        public void render(float delta) {
            if (b)
                return;
            if (TimeUtils.millis() > (startTime + waitTime)) {
                if (screen == null)
                    Gdx.app.exit();
                else {
                    clearTimer(2);
                    setScreen(screen);
                }
                b = true;
            }
        }

        @Override
        public void resize(int width, int height) {

        }

        @Override
        public void pause() {

        }

        @Override
        public void resume() {

        }

        @Override
        public void hide() {

        }

        @Override
        public void dispose() {

        }
    }
}