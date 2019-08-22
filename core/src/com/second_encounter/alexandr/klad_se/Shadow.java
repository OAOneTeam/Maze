package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Shadow {

    private Maze game;
    private TextureRegion region;
    private Image image;

    public Shadow(Maze game) {
        this.game = game;
        region = new TextureRegion(game.assetManager.get("common/shadow.png", Texture.class));
        region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        region.getTexture().setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        image = new Image(region);
        image.setTouchable(Touchable.enabled);
    }

    public void show() {
        game.stage.addActor(image);
    }

    public void hide() {
        image.remove();
    }

    public void resize() {
        region.setRegion(0, 0, (int) game.extendViewport.getWorldWidth(), (int) game.extendViewport.getWorldHeight());
        image.setSize(region.getRegionWidth(), region.getRegionHeight());
        image.setPosition(0, 0);
    }
}
