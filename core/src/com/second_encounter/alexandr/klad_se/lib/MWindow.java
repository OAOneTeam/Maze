package com.second_encounter.alexandr.klad_se.lib;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.Tools;

public abstract class MWindow extends Window {

    protected Maze game;
    public boolean isShown = false;
    private MWindowListener windowListener;
    private ImageButton apply, close;
    private boolean combineButtons = false;

    public MWindow(Maze game, MWindowListener listener) {
        super("", game.skinCommon, "horizontal");
        this.game = game;
        windowListener = listener;
        setModal(false);
        setTouchable(Touchable.disabled);
        pack();
        setOrigin(Align.center);
        apply = new ImageButton(game.skinCommon, "apply");
        apply.setSize(72, 72);
        apply.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                apply();
            }
        });
        apply.setPosition(Tools.half(getWidth() - apply.getWidth()), 16);
        close = new ImageButton(game.skinCommon, "close");
        close.setSize(48, 48);
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });
        close.setPosition(getWidth() - close.getWidth() - 64, getHeight() - close.getHeight() - 24);
        build();
    }

    public void apply() {
        if (windowListener != null)
            if (isShown) {
                windowListener.onApply();
                game.sound.play("click");
            }
    }

    public void close() {
        if (windowListener != null)
            if (isShown) {
                if (combineButtons)
                    windowListener.onApply();
                else
                    windowListener.onClose();
                game.sound.play("click");
            }
    }

    public void setListener(MWindowListener listener) {
        windowListener = listener;
    }

    public void build() {
        clear();
        addActor(apply);
        if (combineButtons)
            return;
        addActor(close);
    }

    public void show() {
        clearActions();
        addAction(
                Actions.sequence(
                        Actions.scaleTo(1f, 1f, 0.4f, Interpolation.swingOut),
                        Actions.touchable(Touchable.enabled)
                )
        );
        setScale(0);
        game.stage.addActor(this);
        isShown = true;
    }

    public void hide(Runnable runnable) {
        clearActions();
        addAction(
                Actions.sequence(
                        Actions.touchable(Touchable.disabled),
                        Actions.scaleTo(0f, 0f, 0.3f, Interpolation.fade),
                        Actions.run(runnable == null ? game.nullRunnable() : runnable),
                        Actions.removeActor()
                )
        );
        isShown = false;
    }

    public void resize() {
        setPosition(Tools.half(game.stage.getWidth() - getWidth()), Tools.half(game.stage.getHeight() - getHeight()));
    }

    public boolean actions() {
        return getActions().size > 0;
    }

    public void combineButtons(boolean combine) {
        combineButtons = combine;
        build();
    }

    public interface MWindowListener {
        void onApply();
        void onClose();
    }
}
