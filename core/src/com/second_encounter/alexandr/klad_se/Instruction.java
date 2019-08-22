package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Instruction extends Image {

    public boolean isShown = false;

    private Maze game;
    private ImageButton close;

    public Instruction(final Maze game, final InstructionListener listener) {
        super(game.languageAtlas.findRegion("help"));
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
        game.stage.addActor(this);
        game.stage.addActor(close);
        isShown = true;
    }

    public void hide() {
        remove();
        close.remove();
        isShown = false;
    }

    public void resize() {
        setPosition(Tools.half(game.stage.getWidth() - getWidth()), Tools.half(game.stage.getHeight() - getHeight()));
        close.setPosition(game.extendViewport.getWorldWidth() - close.getWidth() - 6, 128);
    }

    public interface InstructionListener {
        void onClose();
    }
}
