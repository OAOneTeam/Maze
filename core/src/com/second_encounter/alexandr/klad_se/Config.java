package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.utils.Base64Coder;

import java.util.Locale;

public class Config {

    // languageIndex: 0 = auto, 1 = ru, 2 = en
    public int languageIndex = 0;

    private final String[] languages = {
            Locale.getDefault().getISO3Language().toLowerCase().equals("rus") ? "ru" : "en", "ru", "en"
    };

    // speed table
    // man figure time/ man sped/ man sleep time/ devil figure time/ devil speed/ devil sleep time/ bullet speed/ magic wall update time
    private static final float[][] speed = {
            { 0.08f, 150f, 0.5f, 0.12f, 95f, 1.1f, 550f, 6f },
            { 0.08f, 150f, 0.5f, 0.12f, 95f, 1.1f, 550f, 6f },
            { 0.08f, 150f, 0.5f, 0.12f, 95f, 1.1f, 550f, 6f },
            { 0.08f, 150f, 0.5f, 0.12f, 95f, 1.1f, 550f, 6f },
            { 0.08f, 150f, 0.5f, 0.12f, 95f, 1.1f, 550f, 6f },
            { 0.08f, 150f, 0.5f, 0.12f, 95f, 1.1f, 550f, 6f }
    };

    public boolean firstStart = true;
    public boolean showPrivacyWarning = true;
    public boolean greyscale = false;
    // game speed
    public int agility = 3;
    //
    public int[] level = { 1, 1 }; // [0] - current level, [1] - max level
    public int[] gold  = { 0, 0 }; // [0] - current gold,  [1] - max gold
    public int   live  = 5;
    // gamepad
    public boolean useGamepad = false;
    //
    public boolean music = true;
    //
    public boolean sound = true;
    //
    public boolean footSteps = false;

    private Maze game;

    public Config(Maze game) {
        this.game = game;
        load();
    }

    public String getLanguage() {
        return languages[languageIndex];
    }

    public float getManFigureTime() {
        return speed[agility][0];
    }

    public float getManSpeed() {
        return speed[agility][1];
    }

    public float getManSleepTime() {
        return speed[agility][2];
    }

    public float getDevilFigureTime() {
        return speed[agility][3];
    }

    public float getDevilSpeed() {
        return speed[agility][4];
    }

    public float getDevilSleepTime() {
        return speed[agility][5];
    }

    public float getBulletSpeed() {
        return speed[agility][6];
    }

    public float getWallTime() {
        return speed[agility][7];
    }

    public void save() {
        game.prefs().putString(Base64Coder.encodeString("cfg_languageIndex"), Base64Coder.encodeString(Integer.toString(languageIndex)));
        game.prefs().putString(Base64Coder.encodeString("cfg_agility"), Base64Coder.encodeString(Integer.toString(agility)));
        game.prefs().putString(Base64Coder.encodeString("cfg_level_0"), Base64Coder.encodeString(Integer.toString(level[0])));
        game.prefs().putString(Base64Coder.encodeString("cfg_level_1"), Base64Coder.encodeString(Integer.toString(level[1])));
        game.prefs().putString(Base64Coder.encodeString("cfg_gold_0"), Base64Coder.encodeString(Integer.toString(gold[0])));
        game.prefs().putString(Base64Coder.encodeString("cfg_gold_1"), Base64Coder.encodeString(Integer.toString(gold[1])));
        game.prefs().putString(Base64Coder.encodeString("cfg_live"), Base64Coder.encodeString(Integer.toString(live)));
        game.prefs().putBoolean(Base64Coder.encodeString("cfg_useGamepad"), useGamepad);
        game.prefs().putBoolean(Base64Coder.encodeString("cfg_music"), music);
        game.prefs().putBoolean(Base64Coder.encodeString("cfg_sound"), sound);
        game.prefs().putBoolean(Base64Coder.encodeString("cfg_first_start"), firstStart);
        game.prefs().putBoolean(Base64Coder.encodeString("cfg_privacy_policy"), showPrivacyWarning);
        game.prefs().putBoolean(Base64Coder.encodeString("cfg_greyscale"), greyscale);
        game.prefs().flush();
    }

    private void load() {
        languageIndex = Integer.parseInt(Base64Coder.decodeString(game.prefs().getString(Base64Coder.encodeString("cfg_languageIndex"), Base64Coder.encodeString(String.valueOf(languageIndex)))));
        agility = Integer.parseInt(Base64Coder.decodeString(game.prefs().getString(Base64Coder.encodeString("cfg_agility"), Base64Coder.encodeString(String.valueOf(agility)))));
        level[0] = Integer.parseInt(Base64Coder.decodeString(game.prefs().getString(Base64Coder.encodeString("cfg_level_0"), Base64Coder.encodeString(String.valueOf(level[0])))));
        level[1] = Integer.parseInt(Base64Coder.decodeString(game.prefs().getString(Base64Coder.encodeString("cfg_level_1"), Base64Coder.encodeString(String.valueOf(level[1])))));
        gold[0] = Integer.parseInt(Base64Coder.decodeString(game.prefs().getString(Base64Coder.encodeString("cfg_gold_0"), Base64Coder.encodeString(String.valueOf(gold[0])))));
        gold[1] = Integer.parseInt(Base64Coder.decodeString(game.prefs().getString(Base64Coder.encodeString("cfg_gold_1"), Base64Coder.encodeString(String.valueOf(gold[1])))));
        live = Integer.parseInt(Base64Coder.decodeString(game.prefs().getString(Base64Coder.encodeString("cfg_live"), Base64Coder.encodeString(String.valueOf(live)))));
        useGamepad = game.prefs().getBoolean(Base64Coder.encodeString("cfg_useGamepad"), useGamepad);
        music = game.prefs().getBoolean(Base64Coder.encodeString("cfg_music"), music);
        sound = game.prefs().getBoolean(Base64Coder.encodeString("cfg_sound"), sound);
        firstStart = game.prefs().getBoolean(Base64Coder.encodeString("cfg_first_start"), firstStart);
        showPrivacyWarning = game.prefs().getBoolean(Base64Coder.encodeString("cfg_privacy_policy"), showPrivacyWarning);
        greyscale = game.prefs().getBoolean(Base64Coder.encodeString("cfg_greyscale"), greyscale);
    }
}
