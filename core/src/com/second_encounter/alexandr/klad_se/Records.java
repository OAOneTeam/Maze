package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Records {

    public boolean isShown = false;

    private Maze game;
    private ImageButton close;

    public Records(final Maze game, final RecordsListener listener) {
        this.game = game;
        close = new ImageButton(game.skinCommon, "close");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onClose();
                    game.sound.play("click");
                }
            }
        });
    }

    public void show() {
        game.stage.addActor(close);
        isShown = true;
    }

    public void hide() {
        close.remove();
        isShown = false;
    }

    public void resize() {
        close.setPosition(game.extendViewport.getWorldWidth() - close.getWidth() - 6, 128);
    }

    public interface RecordsListener {
        void onClose();
    }
}
