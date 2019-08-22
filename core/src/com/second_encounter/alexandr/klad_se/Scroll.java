package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Scroll extends Window {

    public boolean isShown = false;

    protected Maze game;

    private ScrollPane scroll;
    private Table table = new Table();
    private ImageButton jump, lives5, lives10, lives20, openOne, openAll, close;
    private float dX, dY;

    public Scroll(final Maze game, final ScrollListener listener) {
        super("", game.skinCommon, "vertical");
        this.game = game;
        setModal(false);
        setKeepWithinStage(false);
        setTouchable(Touchable.disabled);
        pack();
        setOrigin(Align.center);
        //
        jump = new ImageButton(game.skinCommon, "scroll_jump");
        jump.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onJump();
                    game.sound.play("click");
                }
            }
        });
        lives5 = new ImageButton(game.skinCommon, "scroll_lives_5");
        lives5.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onLives5();
                    game.sound.play("click");
                }
            }
        });
        lives10 = new ImageButton(game.skinCommon, "scroll_lives_10");
        lives10.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onLives10();
                    game.sound.play("click");
                }
            }
        });
        lives20 = new ImageButton(game.skinCommon, "scroll_lives_20");
        lives20.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onLives20();
                    game.sound.play("click");
                }
            }
        });
        openOne = new ImageButton(game.skinCommon, "scroll_open_one");
        openOne.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onOne();
                    game.sound.play("click");
                }
            }
        });
        openAll = new ImageButton(game.skinCommon, "scroll_open_all");
        openAll.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShown) {
                    listener.onAll();
                    game.sound.play("click");
                }
            }
        });
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
        //
        scroll = new ScrollPane(table, game.skinCommon);
        add(scroll).width(832).height(376);
    }

    public void build(boolean showJump, boolean showLives, boolean showOne, boolean showAll, String livesPrefix) {
        table.clearChildren();
        table.add(new Label("[BLACK]" + game.bundle.get("maze"), game.skinCommon)).padTop(24).colspan(2).row();
        Label label = new Label("[GRAY]" + game.bundle.get("hint_scroll"), game.skinCommon);
        label.setFontScale(0.35f);
        table.add(label).padTop(4).padBottom(28).colspan(2).row();
        if (showJump) {
            TextPane paneJump = new TextPane(game, 0, 0.4f);
            paneJump.set(game.bundle.get("pane_100"), 0);
            table.add(jump).pad(16);
            table.add(paneJump).pad(16).row();
        }
        if (showLives) {
            TextPane pane5 = new TextPane(game, 0, 0.4f);
            pane5.set(game.bundle.get("pane_099" + livesPrefix), game.config.level[1]);
            table.add(lives5).pad(16);
            table.add(pane5).pad(16).row();
            TextPane pane10 = new TextPane(game, 0, 0.4f);
            pane10.set(game.bundle.get("pane_130" + livesPrefix), game.config.level[1]);
            table.add(lives10).pad(16);
            table.add(pane10).pad(16).row();
            TextPane pane20 = new TextPane(game, 0, 0.4f);
            pane20.set(game.bundle.get("pane_160" + livesPrefix), game.config.level[1]);
            table.add(lives20).pad(32);
            table.add(pane20).pad(32).row();
        }
        if (showOne) {
            TextPane paneOne = new TextPane(game, 0, 0.4f);
            paneOne.set(game.bundle.get("pane_250" ), 0);
            table.add(openOne).pad(16);
            table.add(paneOne).pad(16).row();
        }
        if (showAll) {
            TextPane paneAll = new TextPane(game, 0, 0.4f);
            paneAll.set(game.bundle.get("pane_400"), 0);
            table.add(openAll).pad(16);
            table.add(paneAll).pad(16).row();
        }
        table.add(close).pad(32).colspan(2);
        table.pack();
    }

    public void show() {
        clearActions();
        setY(game.stage.getHeight());
        addAction(
                sequence(
                        Actions.moveTo(dX, dY, 0.4f, Interpolation.swingOut),
                        Actions.touchable(Touchable.enabled)
                )
        );
        scroll.layout();
        scroll.setScrollY(0);
        game.stage.addActor(this);
        game.stage.setScrollFocus(scroll);
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
        game.stage.unfocus(scroll);
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

    public interface ScrollListener {
        void onJump();
        void onLives5();
        void onLives10();
        void onLives20();
        void onOne();
        void onAll();
        void onClose();
    }
}
