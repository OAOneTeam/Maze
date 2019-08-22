package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PrivacyWarning extends TextPane {

    private Maze game;
    private TextButton button;

    public PrivacyWarning(final Maze game, final PrivacyWarningListener listener) {
        super(game, 8, 0.8f);
        this.game = game;
        ImageButton close = new ImageButton(game.skinCommon, "close");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onClose();
                game.sound.play("click");
            }
        });
        button = new TextButton("PRIVACY POLICY", game.skinCommon);
        button.getLabel().setColor(Color.BLUE);
        button.getLabel().addAction(
                Actions.forever(
                        Actions.sequence(
                                Actions.color(Color.SKY, 1f),
                                Actions.color(Color.BLUE, 1f)
                        )
                )
        );
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onPrivacyPolicy();
                game.sound.play("click");
            }
        });
        set(game.bundle.get("privacy_warning"), "font_32", 0);
        add(button).pad(16).row();
        add(close).pad(16);
    }

    public void show() {
        game.stage.addActor(this);
    }

    public void hide() {
        button.getLabel().clearActions();
        remove();
    }

    public void resize() {
        setPosition(Tools.half(game.stage.getWidth() - getWidth()), Tools.half(game.stage.getHeight() - getHeight()));
    }

    public interface PrivacyWarningListener {
        void onClose();
        void onPrivacyPolicy();
    }
}
