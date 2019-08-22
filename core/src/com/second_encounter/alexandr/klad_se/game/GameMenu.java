package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.Tools;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class GameMenu extends Window {

    public boolean isShown = false;

    private Maze game;
    private Table table = new Table();
    private ImageButton chest, buttonSettings, shop, restart, close;
    private Label labelChest, labelButtonSettings, labelShop, labelRestart;
    private float dX, dY;

    public GameMenu(final Maze game, final GameMenuListener listener) {
        super("", game.skinCommon, "vertical");
        this.game = game;
        setModal(false);
        setKeepWithinStage(false);
        setTouchable(Touchable.disabled);
        pack();
        setOrigin(Align.center);
        chest = new ImageButton(game.skinCommon, "chest");
        chest.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onChest();
                    game.sound.play("click");
                }
            }
        });
        labelChest = new Label("", game.skinCommon);
        labelChest.setFontScale(0.3f);
        buttonSettings = new ImageButton(game.skinCommon, "button_settings");
        buttonSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onButtonSettings();
                    game.sound.play("click");
                }
            }
        });
        labelButtonSettings = new Label(game.bundle.get("game_menu_control"), game.skinCommon);
        labelButtonSettings.setFontScale(0.3f);
        shop = new ImageButton(game.skinCommon, "shop");
        shop.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onShop();
                    game.sound.play("click");
                }
            }
        });
        labelShop = new Label(game.bundle.get("game_menu_shop"), game.skinCommon);
        labelShop.setFontScale(0.3f);
        restart = new ImageButton(game.skinCommon, "rip");
        restart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onRestart();
                    game.sound.play("click");
                }
            }
        });
        labelRestart = new Label(game.bundle.get("game_menu_restart"), game.skinCommon);
        labelRestart.setFontScale(0.3f);
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
        add(table);
    }

    public void build() {
        table.clearChildren();
        buttonSettings.setTouchable(game.config.useGamepad ? Touchable.disabled : Touchable.enabled);
        buttonSettings.setDisabled(game.config.useGamepad);
        shop.setTouchable(game.config.live == 0 || game.config.level[1] > 65 ? Touchable.disabled : Touchable.enabled);
        shop.setDisabled(game.config.live == 0 || game.config.level[1] > 65);
        restart.setTouchable(game.config.live == 0 ? Touchable.disabled : Touchable.enabled);
        restart.setDisabled(game.config.live == 0);
        table.add(chest);
        table.add(buttonSettings).padLeft(64).padRight(32);
        table.add(shop).padLeft(32).padRight(64);
        table.add(restart).row();
        table.add(labelChest).padTop(16);
        table.add(labelButtonSettings).padLeft(64).padRight(32).padTop(16);
        table.add(labelShop).padLeft(32).padRight(64).padTop(16);
        table.add(labelRestart).padTop(16).row();
        table.add(close).padTop(64).colspan(4);
        table.pack();
    }

    public void show(boolean save) {
        labelChest.setText(game.bundle.get(save ? "game_menu_save" : "game_menu_exit"));
        build();
        clearActions();
        setY(game.stage.getHeight());
        addAction(
                sequence(
                        Actions.moveTo(dX, dY, 0.4f, Interpolation.swingOut),
                        Actions.touchable(Touchable.enabled)
                )
        );

        game.stage.addActor(this);
        isShown = true;
    }

    public void hide(Runnable runnable) {
        clearActions();
        addAction(
                sequence(
                        Actions.touchable(Touchable.disabled),
                        Actions.moveTo(dX, game.stage.getHeight(), 0.4f, Interpolation.swingIn),
                        Actions.run(runnable == null ? game.nullRunnable() : runnable),
                        Actions.removeActor()
                )
        );
        isShown = false;
    }

    public void resize() {
        setPosition(Tools.half(game.stage.getWidth() - getWidth()), Tools.half(game.stage.getHeight() - getHeight()));
        dX = getX();
        dY = getY();
    }

    public boolean actions() {
        return getActions().size > 0;
    }

    public interface GameMenuListener {
        void onChest();
        void onButtonSettings();
        void onRestart();
        void onShop();
        void onClose();
    }
}
