package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

public class NumericText extends HorizontalGroup {

    private Maze game;
    private int oldValue = -1;

    public NumericText(Maze game, int value) {
        this.game = game;
        set(value);
        setOrigin(Align.center);
    }

    public void set(int value) {
        if (oldValue != value) {
            if (getChildren().size > 0)
                clearChildren();
            String s = String.valueOf(value);
            for (int i = 0; i < s.length(); i++) {
                Image image = new Image(game.commonAtlas.findRegion(String.valueOf(s.charAt(i) - 48)));
                addActor(image);
            }
            oldValue = value;
            pack();
        }
    }
}
