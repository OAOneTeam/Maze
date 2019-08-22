package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class Main extends Table {

    public boolean isShown = false;

    private Maze game;
    private Image cursor;
    private NumericText numericText;
    private ImageButton[] buttons = new ImageButton[6];
    private MainMenuListener listener;
    private int currentItem = 0;

    public Main(Maze game, final MainMenuListener listener) {
        this.game = game;
        this.listener = listener;
        //
        String[] buttonsId = {
                "game",
                "agility",
                "instruction",
                "controls",
                "records",
                "exit"
        };
        //
        cursor = new Image(game.commonAtlas.findRegion("cursor"));
        cursor.setColor(1f, 1f, 1f, 0.95f);
        cursor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (cursor.getActions().size == 0)
                    for (int i = 0; i < buttons.length; i++)
                        if (cursor.getY() == buttons[i].getY()) {
                            select(i);
                            break;
                        }
            }
        });
        addActor(cursor);
        for (int i = 0; i < buttons.length; i++) {
            final int finalI = i;
            buttons[i] = new ImageButton(game.skinLanguage, buttonsId[i]);
            buttons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    cursorMove(finalI);
                }
            });
            add(buttons[i]).pad(2).row();
        }
        pack();
        setTransform(true);
        setOrigin(Align.center);
        numericText = new NumericText(game, game.config.agility);
        numericText.setTouchable(Touchable.disabled);
        numericText.setPosition(392, buttons[1].getY());
        addActor(numericText);
    }

    public void show() {
        game.stage.addActor(this);
        setTouchable(Touchable.enabled);
        isShown = true;
    }

    public void hide() {
        remove();
        isShown = false;
    }

    public void resize() {
        setPosition(Tools.half(game.stage.getWidth() - getWidth()), Tools.half(game.stage.getHeight() - getHeight()));
        cursor.setPosition(Tools.half(getWidth() - cursor.getWidth()), buttons[currentItem].getY());
    }

    public void cursorUp() {
        if (currentItem == 0)
            return;
        cursorAction(--currentItem);
    }

    public void cursorDown() {
        if (currentItem == 5)
            return;
        cursorAction(++currentItem);
    }

    public void enter() {
        select(currentItem);
    }

    public void cursorMove(int item) {
        if (cursor.getY() == buttons[item].getY())
            select(item);
        else
            cursorAction(item);
    }

    private void select(int item) {
        if (isShown)
            switch (item) {
                case 0:
                    listener.onGame();
                    game.sound.play("click");
                    break;
                case 1:
                    if (++game.config.agility > 5)
                        game.config.agility = 0;
                    numericText.set(game.config.agility);
                    game.sound.play("click");
                    break;
                case 2:
                    listener.onInstruction();
                    game.sound.play("click");
                    break;
                case 3:
                    listener.onControls();
                    game.sound.play("click");
                    break;
                case 4:
                    listener.onRecords();
                    game.sound.play("click");
                    break;
                case 5:
                    listener.onExit();
                    game.sound.play("click");
                    break;
            }
    }

    private void cursorAction(int item) {
        cursor.addAction(
                Actions.moveTo(cursor.getX(), buttons[item].getY(), 0.2f, Interpolation.fastSlow)
        );
        currentItem = item;
    }

    public interface MainMenuListener {
        void onGame();
        void onInstruction();
        void onControls();
        void onRecords();
        void onExit();
    }
}
