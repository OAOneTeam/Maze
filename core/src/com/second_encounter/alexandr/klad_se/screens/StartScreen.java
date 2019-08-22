package com.second_encounter.alexandr.klad_se.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.Tools;

public class StartScreen implements Screen {

    private Maze game;
    private Image oaOneTeam;

    public StartScreen(final Maze game) {
        this.game = game;
        Texture texture = game.assetManager.get("common/oa_one_team.png");
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        oaOneTeam = new Image(texture);
        oaOneTeam.setOrigin(Align.center);
    }

    @Override
    public void show() {
        oaOneTeam.setScale(10f);
        oaOneTeam.addAction(
                Actions.sequence(
                        Actions.scaleTo(1f, 1f, 0.4f, Interpolation.bounceOut),
                        Actions.parallel(
                                Actions.scaleTo(0.8f, 0.8f, 5f, Interpolation.fade),
                                Actions.delay(7f)
                        ),
                        Actions.alpha(0f, 0.3f, Interpolation.fade),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                game.setSplashScreen(new MainScreen(game));
                            }
                        })
                )
        );
        game.stage.addActor(oaOneTeam);
        game.sound.play("noise");
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        oaOneTeam.setPosition(Tools.half(game.stage.getWidth() - oaOneTeam.getWidth()), Tools.half(game.stage.getHeight() - oaOneTeam.getHeight()));
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
