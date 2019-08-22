package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class Controls {

    public boolean isShown = false;

    private Maze game;
    private Table table = new Table();
    private HorizontalGroup group = new HorizontalGroup();
    private Image language;
    private Image[] images = new Image[3];
    private ImageButton gamepad, music, sound, greyscale, close;
    private CheckBox gamepadCheck, musicCheck, soundCheck, greyscaleCheck;
    private int languageIndex;
    private String oldLanguage;

    public Controls(final Maze game, final ControlsListener listener) {
        this.game = game;
        oldLanguage = game.config.getLanguage();
        language = new Image(game.languageAtlas.findRegion("language"));
        images[0] = new Image(game.languageAtlas.findRegion("default"));
        images[1] = new Image(game.commonAtlas.findRegion("ru"));
        images[2] = new Image(game.commonAtlas.findRegion("en"));
        gamepad = new ImageButton(game.skinLanguage, "gamepad");
        gamepad.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gamepadCheck.setChecked(!gamepadCheck.isChecked());
            }
        });
        gamepadCheck = new CheckBox("", game.skinCommon);
        gamepadCheck.setChecked(game.config.useGamepad);
        gamepadCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.config.useGamepad = gamepadCheck.isChecked();
                game.sound.play("click");
            }
        });
        //
        music = new ImageButton(game.skinLanguage, "music");
        music.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicCheck.setChecked(!musicCheck.isChecked());
            }
        });
        musicCheck = new CheckBox("", game.skinCommon);
        musicCheck.setChecked(game.config.music);
        musicCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.config.music = musicCheck.isChecked();
                game.sound.play("click");
                if (game.config.music)
                    game.music.play("main");
                else
                    game.music.stopAll();
            }
        });
        //
        sound = new ImageButton(game.skinLanguage, "sound");
        sound.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundCheck.setChecked(!soundCheck.isChecked());
            }
        });
        soundCheck = new CheckBox("", game.skinCommon);
        soundCheck.setChecked(game.config.sound);
        soundCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.config.sound = soundCheck.isChecked();
                game.sound.play("click");
            }
        });
        //
        greyscale = new ImageButton(game.skinLanguage, "greyscale");
        sound.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                greyscaleCheck.setChecked(!greyscaleCheck.isChecked());
            }
        });
        greyscaleCheck = new CheckBox("", game.skinCommon);
        greyscaleCheck.setChecked(game.config.greyscale);
        greyscaleCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.config.greyscale = greyscaleCheck.isChecked();
                game.setGreyscale();
                game.sound.play("click");
            }
        });
        //
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
        group.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (++languageIndex == images.length)
                    languageIndex = 0;
                build();
                game.sound.play("click");
            }
        });
        build();
    }

    private void build() {
        table.clearChildren();
        group.clearChildren();
        group.addActor(language);
        group.addActor(images[languageIndex]);
        group.pack();
        table.add(group).width(640).pad(12).colspan(2).row();
        table.add(gamepad).align(Align.right).pad(12);
        table.add(gamepadCheck).align(Align.left).pad(12).row();
        table.add(music).align(Align.right).pad(12);
        table.add(musicCheck).align(Align.left).pad(12).row();
        table.add(sound).align(Align.right).pad(12);
        table.add(soundCheck).align(Align.left).pad(12).row();
        table.add(greyscale).align(Align.right).pad(12);
        table.add(greyscaleCheck).align(Align.left).pad(12);
        table.pack();
    }

    public void show() {
        languageIndex = game.config.languageIndex;
        game.stage.addActor(table);
        game.stage.addActor(close);
        isShown = true;
        build();
    }

    public boolean hide() {
        game.config.languageIndex = languageIndex;
        table.remove();
        close.remove();
        isShown = false;
        return oldLanguage.equals(game.config.getLanguage());
    }

    public void resize() {
        table.setPosition(Tools.half(game.stage.getWidth() - table.getWidth()), Tools.half(game.stage.getHeight() - table.getHeight()));
        close.setPosition(game.extendViewport.getWorldWidth() - close.getWidth() - 6, 128);
    }

    public interface ControlsListener {
        void onClose();
    }
}
