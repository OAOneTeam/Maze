package com.second_encounter.alexandr.klad_se.lib;

import com.badlogic.gdx.audio.Music;
import com.second_encounter.alexandr.klad_se.Maze;

public class GameMusic {

    private final String[] list = { "main", "life_down", "dead" };
    private final String type = ".wav";

    private Maze game;
    private Music[] music = new Music[list.length];

    public GameMusic(Maze game) {
        this.game = game;
    }

    public void load() {
        for (String name : list)
            game.assetManager.load("music/" + name + type, Music.class);
    }

    public void create() {
        for (int i = 0; i < list.length; i++)
            music[i] = game.assetManager.get("music/" + list[i] + type, Music.class);
    }

    public void play(String name) {
        if (game.config.music)
            for (int i = 0; i < list.length; i++)
                if (list[i].equals(name)) {
                    music[i].setVolume(1f);
                    music[i].play();
                    break;
                }
    }

    public void stopAll() {
        for (int i = 0; i < list.length; i++)
            if (music[i].isPlaying())
                music[i].stop();
    }

    public void dispose() {
        stopAll();
        for (int i = 0; i < list.length; i++)
            music[i].dispose();
    }
}
