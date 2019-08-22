package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.second_encounter.alexandr.klad_se.lib.MWindow;

public class Exit extends MWindow {

    public Exit(Maze game, CharSequence title, MWindowListener listener) {
        super(game, listener);
        getTitleLabel().setText(title);
        getTitleTable().padTop(256);
        getTitleLabel().setAlignment(Align.center);
        getTitleLabel().setFontScale(0.5f);
        getTitleLabel().setColor(Color.DARK_GRAY);
    }
}
