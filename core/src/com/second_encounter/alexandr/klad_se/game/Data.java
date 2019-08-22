package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.second_encounter.alexandr.klad_se.Maze;

import java.util.Locale;

public class Data {

    private Maze game;
    private byte[] data = new byte[23855];
    private Texture[][] sprites = new Texture[2][21];
    private TextureRegion[][] regions = new TextureRegion[2][21];

    public Data(Maze game) {
        this.game = game;
        FileHandle file = Gdx.files.internal("data/levels");
        file.readBytes(data, 0, data.length);
    }

    public void load() {
        // sprites
        for (int j = 1; j <= 2; j++)
            for (int i = 0; i < sprites[0].length; i++)
                game.assetManager.load(String.format(Locale.getDefault(), "common/%1d/%1d%02d.png", j * 100, j, i), Texture.class);
    }

    public void initialize() {
        // sprites
        for (int j = 1; j <= 2; j++)
            for (int i = 0; i < sprites[j - 1].length; i++) {
                sprites[j - 1][i] = game.assetManager.get(String.format(Locale.getDefault(), "common/%1d/%1d%02d.png", j * 100, j, i), Texture.class);
                sprites[j - 1][i].setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                sprites[j - 1][i].setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
            }
        // regions
        for (int j = 0; j < 2; j++)
            for (int i = 0; i < regions[0].length; i++) {
                regions[j][i] = game.commonAtlas.findRegion(String.format(Locale.getDefault(), "%1d%02d", j + 1, i));
                regions[j][i].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                regions[j][i].getTexture().setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
            }
    }

    // sprites
    public Texture getSprite(int part, int index) {
        return sprites[part][index];
    }

    // regions
    public TextureRegion getRegion(int part, int index) {
        return regions[part][index];
    }

    public int get(int index) {
        return data[index] & 0xff;
    }
}
