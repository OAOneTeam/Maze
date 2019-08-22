package com.second_encounter.alexandr.klad_se.lib;

import com.badlogic.gdx.audio.Sound;
import com.second_encounter.alexandr.klad_se.Maze;

public class GameSound {

    private final String[] list = { "noise", "click", "step", "gold", "key", "door", "shoot", "crash", "next", "bonus", "magic" };
    private final String type = ".wav";

    private Maze game;
    private Sound[] sound = new Sound[list.length];

    public GameSound(Maze game) {
        this.game = game;
    }

    public void load() {
        for (String name : list)
            game.assetManager.load("sound/" + name + type, Sound.class);
    }

    public void create() {
        for (int i = 0; i < list.length; i++)
            sound[i] = game.assetManager.get("sound/" + list[i] + type, Sound.class);
    }

    public void play(String name) {
        if (game.config.sound)
            for (int i = 0; i < list.length; i++)
                if (list[i].equals(name)) {
                    if (name.equals("step") && !game.config.footSteps)
                        break;
                    sound[i].play();
                    break;
                }
    }

    public void dispose() {
        for (int i = 0; i < list.length; i++)
            sound[i].dispose();
    }
}
