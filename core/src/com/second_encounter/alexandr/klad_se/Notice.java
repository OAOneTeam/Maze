package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.second_encounter.alexandr.klad_se.lib.MWindow;

public class Notice extends MWindow {

    public Notice(Maze game, MWindowListener listener) {
        super(game, listener);
    }

    public void set(TextPane pane, Image image) {
        build();
        if (image != null) {
            add(image).padBottom(32);
        }
        add(pane).padBottom(14);
    }

    public void set(Label label) {
        build();
        add(label);
    }
}
