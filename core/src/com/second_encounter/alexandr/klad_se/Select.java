package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Base64Coder;

import java.util.Locale;

public class Select extends Table {

    public boolean isShown = false;

    private Maze game;
    private SelectListener listener;
    private Image border, actionImage = null;
    private Table table = new Table();
    private ScrollPane scroll;
    private ImageButton one, all, close;

    public Select(final Maze game, final SelectListener listener) {
        this.game = game;
        this.listener = listener;
        Texture borderTexture = game.assetManager.get("common/border.png");
        borderTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        borderTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        border = new Image(borderTexture);
        border.setTouchable(Touchable.disabled);
        one = new ImageButton(game.skinCommon, "open_one");
        one.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return !scroll.isFlinging();
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (isShown && !scroll.isPanning()) {
                    listener.onOne();
                    game.sound.play("click");
                }
            }
        });
        all = new ImageButton(game.skinCommon, "open_all");
        all.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return !scroll.isFlinging();
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (isShown && !scroll.isPanning()) {
                    listener.onAll();
                    game.sound.play("click");
                }
            }
        });
        scroll = new ScrollPane(table, game.skinCommon);
        //
        close = new ImageButton(game.skinCommon, "close");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onClose();
                    game.sound.play("click");
                }
            }
        });
    }

    int level;

    public void build() {
        int space = 26;
        int columns = 4;
        one.setTouchable(Touchable.enabled);
        all.setTouchable(Touchable.enabled);
        if (game.config.level[0] <= game.config.level[1]) {
            if (game.config.level[1] > 65) {
                game.config.level[0] = 65;
                one.setTouchable(Touchable.disabled);
                all.setTouchable(Touchable.disabled);
            }
            else
                if (game.config.level[1] > 25) {
                    if (game.config.level[0] < 26)
                        game.config.level[0] = 26;
                    one.setTouchable(Touchable.disabled);
                }
        }
        int savedLevel = game.prefs().getInteger(Base64Coder.encodeString("room_level"), 0);
        clearChildren();
        table.clearChildren();
        table.add(one).pad(space).colspan(columns).row();
        actionImage = null;
        level = 0;
        while (++level <= 65) {
            String name = String.format(Locale.getDefault(), "mini_%d_", level <= game.config.level[0] || level <= game.config.level[1] ? level : 0);
            final Image image = new Image(game.miniAtlas.findRegion(name));
            image.setColor(level > game.config.level[0] && level <= game.config.level[1] ? Color.GRAY : Color.WHITE);
            image.setName(String.valueOf(level));
            image.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !scroll.isFlinging();
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (isShown && !scroll.isPanning()) {
                        image.clearActions();
                        if (Integer.valueOf(image.getName()) <= game.config.level[0]) {
                            listener.onStart(Integer.valueOf(image.getName()));
                            game.sound.play("click");
                            return;
                        }
                        if (Integer.valueOf(image.getName()) <= game.config.level[1]) {
                            listener.onClosedLevel();
                            game.sound.play("click");
                        }
                        else {
                            listener.onOneAndAll();
                            game.sound.play("click");
                        }
                    }
                }
            });
            if (level == savedLevel) {
                image.setOrigin(Align.center);
                image.setScale(1.2f);
                image.addAction(
                        Actions.forever(
                                Actions.sequence(
                                        Actions.rotateTo(-3f, 1.5f, Interpolation.fade),
                                        Actions.rotateTo(3f, 1.5f, Interpolation.fade)
                                )
                        ));
                actionImage = image;
            }
            if ((level - (level >= 25 ? 1 : 0)) % columns == 0)
                table.add(image).pad(space).row();
            else
                table.add(image).pad(space);
            if (level == 25)
                table.add(all).pad(space).colspan(columns).row();
        }
        table.pack();
        add(scroll).width(1000).height(416).row();
    }

    public void focus() {
        game.stage.setScrollFocus(scroll);
    }

    public void show() {
        game.stage.addActor(this);
        game.stage.addActor(border);
        game.stage.addActor(close);
        focus();
        isShown = true;
    }

    public void hide() {
        if (actionImage != null)
            actionImage.clearActions();
        remove();
        border.remove();
        close.remove();
        isShown = false;
    }

    public void resize() {
        setPosition(Tools.half(game.stage.getWidth() - getWidth()), Tools.half(game.stage.getHeight() - getHeight()));
        border.setPosition(Tools.half(game.stage.getWidth() - border.getWidth()), Tools.half(game.stage.getHeight() - border.getHeight()));
        close.setPosition(game.extendViewport.getWorldWidth() - close.getWidth() - 6, 128);
    }

    public interface SelectListener {
        void onStart(int number);
        void onClosedLevel();
        void onOne();
        void onAll();
        void onOneAndAll();
        void onClose();
    }
}
