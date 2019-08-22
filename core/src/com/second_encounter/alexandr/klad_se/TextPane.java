package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Locale;

public class TextPane extends Table {

    private Maze game;
    private float pad, fontScale;

    public TextPane(Maze game, float pad, float fontScale) {
        this.game = game;
        this.pad = pad;
        this.fontScale = fontScale;
    }

    public void set(String text, int...values) {
        set(text, "default", values);
    }

    public void set(String text, String styleName, int...values) {
        clearChildren();
        String s = text.concat("/");
        int first = 0, last, valIndex = 0;
        while (s.contains("/")) {
            last = s.indexOf("/");
            String line = s.substring(first, last).replace("\n", "").replaceAll("( )+", " ").trim();
            Label label = new Label(line.contains("%") ? String.format(Locale.getDefault(), line, values[valIndex++]) : line, game.skinCommon, styleName);
            label.setFontScale(fontScale);
            label.pack();
            add(label).pad(pad).row();
            s = s.replaceFirst("/", "");
            first = last;
        }
        pack();
    }
}
