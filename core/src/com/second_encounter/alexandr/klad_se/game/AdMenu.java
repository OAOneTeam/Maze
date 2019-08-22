package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.TextPane;
import com.second_encounter.alexandr.klad_se.Tools;

public class AdMenu extends Window {

    public boolean isShown = false;

    private Maze game;
    private AdMenuListener adMenuListener;
    private Image image;
    private float dX, dY;

    public AdMenu(Maze game, AdMenuListener listener) {
        super("", game.skinCommon, "vertical");
        this.game = game;
        adMenuListener = listener;
        setModal(false);
        setKeepWithinStage(false);
        setTouchable(Touchable.disabled);
        pack();
        setOrigin(Align.center);
        TextPane pane_a = new TextPane(game, 0, 0.5f);
        pane_a.set(game.bundle.get("ad_offer_top"), 0);
        image = new Image(game.commonAtlas.findRegion("live_plus"));
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showAd();
            }
        });
        Label label = new Label(game.bundle.get("ad_offer_hint"), game.skinCommon);
        label.setFontScale(0.4f);
        ImageButton close = new ImageButton(game.skinCommon, "close");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });
        add(pane_a).row();
        add(image).padTop(40).padBottom(8).row();
        add(label).row();
        add(close).padTop(16);
    }

    public void showAd() {
        if (isShown) {
            adMenuListener.onShowAd();
            game.sound.play("click");
        }
    }

    public void close() {
        if (isShown) {
            adMenuListener.onClose();
            game.sound.play("click");
        }
    }

    public void show() {
        clearActions();
        image.clearActions();
        setY(-getHeight());
        addAction(
                Actions.sequence(
                        Actions.moveTo(dX, dY, 0.4f, Interpolation.swingOut),
                        Actions.touchable(Touchable.enabled)
                )
        );
        image.setScale(1);
        image.addAction(
                Actions.forever(
                        Actions.sequence(
                                Actions.delay(0.5f),
                                Actions.scaleTo(1.4f, 1.4f, 0.3f, Interpolation.bounceOut),
                                Actions.scaleTo(1f, 1f, 0.2f, Interpolation.fade)
                        )
                )
        );
        game.stage.addActor(this);
        isShown = true;
    }

    public void hide(Runnable runnable) {
        clearActions();
        image.clearActions();
        addAction(
                Actions.sequence(
                        Actions.touchable(Touchable.disabled),
                        Actions.moveTo(dX, -getHeight(), 0.4f, Interpolation.swingIn),
                        Actions.run(runnable == null ? game.nullRunnable() : runnable),
                        Actions.removeActor()
                )
        );
        isShown = false;
    }

    public void resize() {
        setPosition(Tools.half(game.stage.getWidth() - getWidth()), Tools.half(game.stage.getHeight() - getHeight()));
        dX = getX();
        dY = getY();
    }

    public boolean actions() {
        return getActions().size > 0;
    }

    public interface AdMenuListener {
        void onShowAd();
        void onClose();
    }

}
