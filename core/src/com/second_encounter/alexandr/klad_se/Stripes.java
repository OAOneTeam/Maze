package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Stripes {

    private Maze game;
    private TextureRegion[] region = new TextureRegion[2];
    private Image[] image = new Image[2];
    private Label versionLabel;
    private Image libGDX;

    public Stripes(Maze game) {
        this.game = game;
        String[] fileName = {
                game.config.getLanguage() + "/panel_top.png",
                "common/panel_bottom.png"
        };
        for (int i = 0; i < 2; i++) {
            region[i] = new TextureRegion(game.assetManager.get(fileName[i], Texture.class));
            region[i].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            region[i].getTexture().setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
            image[i] = new Image(region[i]);
        }
        versionLabel = new Label("[YELLOW]ver. [GRAY]" + game.handler.versionName(), game.skinCommon, "font_32");
        versionLabel.setFontScale(0.7f);
        versionLabel.setColor(1f, 1f, 1f, 0.5f);
        libGDX = new Image(game.commonAtlas.findRegion("libgdx"));
        libGDX.setColor(1f, 1f, 1f, 0.1f);
    }

    public void show() {
        game.stage.addActor(image[0]);
        game.stage.addActor(image[1]);
        game.stage.addActor(versionLabel);
        game.stage.addActor(libGDX);
    }

    public void resize() {
        for (int i = 0; i < 2; i++) {
            int w = region[i].getTexture().getWidth();
            int h = region[i].getTexture().getHeight();
            int ratio = (int) (game.extendViewport.getWorldWidth() - w);
            region[i].setRegion(-ratio / 2, 0, w + ratio, h);
            image[i].setSize(region[i].getRegionWidth(), region[i].getRegionHeight());
        }
        image[0].setPosition(0, game.extendViewport.getWorldHeight() - image[0].getHeight() - 5);
        image[1].setPosition(0, 5);
        versionLabel.setPosition(10, spaceY() + 5);
        libGDX.setPosition(game.stage.getWidth() - libGDX.getWidth() - 4, spaceY() + 14);
    }

    public float spaceY() {
        return image[1].getHeight();
    }

    public float spaceWidth() {
        return game.extendViewport.getWorldWidth();
    }

    public float spaceHeight() {
        return game.extendViewport.getWorldHeight() - (image[0].getHeight() + image[1].getHeight());
    }
}
