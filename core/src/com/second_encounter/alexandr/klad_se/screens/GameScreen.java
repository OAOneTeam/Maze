package com.second_encounter.alexandr.klad_se.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.second_encounter.alexandr.klad_se.ActivityRequestHandler;
import com.second_encounter.alexandr.klad_se.Exit;
import com.second_encounter.alexandr.klad_se.GS;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.Price;
import com.second_encounter.alexandr.klad_se.Scroll;
import com.second_encounter.alexandr.klad_se.Tools;
import com.second_encounter.alexandr.klad_se.game.Control;
import com.second_encounter.alexandr.klad_se.game.GameAction;
import com.second_encounter.alexandr.klad_se.game.GameMenu;
import com.second_encounter.alexandr.klad_se.game.GameTouchpad;
import com.second_encounter.alexandr.klad_se.lib.MWindow;

import java.util.ArrayList;

public class GameScreen extends InputAdapter implements Screen, GameTouchpad.GameTouchpadListener, ControllerListener {

    private Maze game;
    private GameAction action;
    private GameTouchpad touchpad;
    private ImageButton menu;
    private GameMenu gameMenu;
    private Scroll scroll;
    private Price price;
    private Exit exit;
    private Slider buttonSettingsSlider;
    private ImageButton buttonSettingsClose;
    private Label buttonSettingsLabel;

    public GameScreen(final Maze game, int level) {
        this.game = game;
        game.zoomExtendViewport.camera.zoom = 0.9f;
        action = new GameAction(game, level);
        action.setAction(true);
        touchpad = new GameTouchpad(game, this);
        touchpad.setArrangement(false);
        touchpad.setDebugDraw(false);
        menu = new ImageButton(game.skinCommon, "menu");
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.setAction(false);
                game.shadow.show();
                gameMenu.show(game.config.level[0] == action.room().getLevel() || game.config.level[1] == 66);
                game.sound.play("click");
            }
        });
        gameMenu = new GameMenu(game, new GameMenu.GameMenuListener() {
            @Override
            public void onChest() {
                gameMenu.hide(new Runnable() {
                    @Override
                    public void run() {
                        if (game.config.level[0] == action.room().getLevel() || game.config.level[1] == 66)
                            exit.show();
                        else
                            close();
                    }
                });
            }

            @Override
            public void onButtonSettings() {
                boolean arrangement = touchpad.getArrangement();
                touchpad.setArrangement(!arrangement);
                back();
            }

            @Override
            public void onRestart() {
                gameMenu.hide(new Runnable() {
                    @Override
                    public void run() {
                        boolean arrangement = touchpad.getArrangement();
                        action.setAction(!arrangement);
                        game.shadow.hide();
                        action.man().putToDeath();
                        action.takeLife();
                    }
                });
            }

            @Override
            public void onShop() {
                gameMenu.hide(new Runnable() {
                    @Override
                    public void run() {
                        scroll.build(true, true, false, false, "_1");
                        scroll.show();
                    }
                });
            }

            @Override
            public void onClose() {
                back();
            }
        });
        scroll = new Scroll(game, new Scroll.ScrollListener() {
            @Override
            public void onJump() {
                scroll.hide(new Runnable() {
                    @Override
                    public void run() {
                        price.show(Price.price100);
                    }
                });
            }

            @Override
            public void onLives5() {
                scroll.hide(new Runnable() {
                    @Override
                    public void run() {
                        price.show(Price.price099);
                    }
                });
            }

            @Override
            public void onLives10() {
                scroll.hide(new Runnable() {
                    @Override
                    public void run() {
                        price.show(Price.price130);
                    }
                });
            }

            @Override
            public void onLives20() {
                scroll.hide(new Runnable() {
                    @Override
                    public void run() {
                        price.show(Price.price160);
                    }
                });
            }

            @Override
            public void onOne() {

            }

            @Override
            public void onAll() {

            }

            @Override
            public void onClose() {
                back();
            }
        });
        price = new Price(game, new MWindow.MWindowListener() {
            @Override
            public void onApply() {
                price.hide(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Purchase
                        switch (price.getPrice()) {
                            case Price.price100:
                                game.handler.initiatePurchaseFlow(GS.skuJump);
                                break;
                            case Price.price099:
                                game.handler.initiatePurchaseFlow(GS.sku5Lives);
                                break;
                            case Price.price130:
                                game.handler.initiatePurchaseFlow(GS.sku10Lives);
                                break;
                            case Price.price160:
                                game.handler.initiatePurchaseFlow(GS.sku20Lives);
                                break;
                        }
                    }
                });
                game.handler.setPurchaseListener(new ActivityRequestHandler.PurchaseListener() {
                    @Override
                    public void onPurchasesUpdated(final ArrayList<String> skus) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (skus != null) {
                                    int last = skus.size() - 1;
                                    if (skus.get(last).equals(GS.skuJump))
                                        action.jump();
                                    if (skus.get(last).equals(GS.sku5Lives))
                                        action.addLive(5);
                                    if (skus.get(last).equals(GS.sku10Lives))
                                        action.addLive(10);
                                    if (skus.get(last).equals(GS.sku20Lives))
                                        action.addLive(20);
                                }
                                boolean arrangement = touchpad.getArrangement();
                                action.setAction(!arrangement);
                                game.shadow.hide();
                            }
                        });
                    }
                });
            }

            @Override
            public void onClose() {
                price.hide(new Runnable() {
                    @Override
                    public void run() {
                        scroll.show();
                    }
                });
            }
        });
        exit = new Exit(game, game.bundle.get("exit_save"), new MWindow.MWindowListener() {
            @Override
            public void onApply() {
                exit.hide(new Runnable() {
                    @Override
                    public void run() {
                        action.save();
                        close();
                    }
                });
            }

            @Override
            public void onClose() {
                exit.hide(new Runnable() {
                    @Override
                    public void run() {
                        boolean arrangement = touchpad.getArrangement();
                        action.setAction(!arrangement);
                        game.shadow.hide();
                    }
                });
            }
        });
        buttonSettingsSlider = new Slider(0, 2, 1, false, game.skinCommon, "small");
        buttonSettingsSlider.setValue(touchpad.getSizeIndex());
        buttonSettingsSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                touchpad.changeSize((int) ((Slider) actor).getValue());
            }
        });
        buttonSettingsLabel = new Label(game.bundle.get("button_settings"), game.skinCommon);
        buttonSettingsLabel.setFontScale(0.4f);
        buttonSettingsLabel.pack();
        buttonSettingsClose = new ImageButton(game.skinCommon, "close");
        buttonSettingsClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.setAction(true);
                touchpad.setArrangement(false);
                buttonSettingsSlider.remove();
                buttonSettingsLabel.remove();
                buttonSettingsClose.remove();
                action.progress().setVisible(true);
            }
        });
        //
        Gdx.input.setInputProcessor(new InputMultiplexer(game.stage, this));
        if (Controllers.getListeners().size > 0)
            Controllers.clearListeners();
        Controllers.addListener(this);
    }

    @Override
    public void show() {
        action.show();
        game.stage.addActor(menu);
    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        action.draw(delta);
        touchpad.draw(game.config.useGamepad);
        game.batch.end();
        touchpad.debugDraw();
    }

    @Override
    public void resize(int width, int height) {
        action.resize();
        touchpad.resize();
        menu.setPosition(Tools.half(game.stage.getWidth() - menu.getWidth()), action.room().getTop() + 58);
        gameMenu.resize();
        scroll.resize();
        price.resize();
        exit.resize();
        buttonSettingsSlider.setPosition(Tools.half(game.stage.getWidth() - buttonSettingsSlider.getWidth()), action.room().getY() - 64);
        buttonSettingsLabel.setPosition(Tools.half(game.stage.getWidth() - buttonSettingsLabel.getWidth()), action.room().getY() - 88);
        buttonSettingsClose.setPosition(Tools.half(game.stage.getWidth() - buttonSettingsClose.getWidth()), action.room().getY() - 160);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private boolean actions() {
        return gameMenu.actions() || scroll.actions() || action.notice().actions() || action.adMenu().actions() || price.actions() || exit.actions();
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (gameMenu.isShown || exit.isShown || actions() || game.config.useGamepad)
            return false;
        return touchpad.onTouchDown(screenX, screenY, pointer);
    }

    public boolean touchDragged (int screenX, int screenY, int pointer) {
        return touchpad.onTouchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return touchpad.onTouchUp(pointer);
    }

    @Override
    public boolean keyDown (int keycode) {
        if (actions())
            return false;
        if (keycode == Input.Keys.ENTER) {
            if (exit.isShown) {
                exit.apply();
                return false;
            }
            if (price.isShown) {
                price.apply();
                return false;
            }
            if (action.notice().isShown) {
                action.notice().close();
                return false;
            }
            if (action.adMenu().isShown) {
                action.adMenu().showAd();
                return false;
            }
        }
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            back();
            return false;
        }
        // TODO for debug only
        if (keycode == Input.Keys.Q) {
            if (action.room().isReady() && action.getAction())
                action.jump();
            return false;
        }
        //
        action.control().add(keycode);
        return false;
    }

    public boolean keyUp (int keycode) {
        action.control().delete(keycode);
        return false;
    }

    private void back() {
        if (actions() || action.adIsLoading())
            return;
        if (exit.isShown) {
            exit.close();
            return;
        }
        if (price.isShown) {
            price.close();
            return;
        }
        if (action.notice().isShown) {
            action.notice().close();
            return;
        }
        if (action.adMenu().isShown) {
            action.adMenu().close();
            return;
        }
        if (gameMenu.isShown) {
            gameMenu.hide(new Runnable() {
                @Override
                public void run() {
                    boolean arrangement = touchpad.getArrangement();
                    action.setAction(!arrangement);
                    if (arrangement) {
                        game.stage.addActor(buttonSettingsSlider);
                        game.stage.addActor(buttonSettingsLabel);
                        game.stage.addActor(buttonSettingsClose);
                        action.progress().setVisible(false);
                    }
                    else {
                        buttonSettingsSlider.remove();
                        buttonSettingsLabel.remove();
                        buttonSettingsClose.remove();
                        action.progress().setVisible(true);
                    }
                    game.shadow.hide();
                }
            });
            return;
        }
        if (scroll.isShown) {
            scroll.hide(new Runnable() {
                @Override
                public void run() {
                    gameMenu.show(game.config.level[0] == action.room().getLevel() || game.config.level[1] == 66);
                }
            });
            return;
        }
        action.setAction(false);
        if (game.config.level[0] == action.room().getLevel() || game.config.level[1] == 66) {
            game.shadow.show();
            exit.show();
        }
        else
            close();
    }

    private void close() {
        game.delayedRunnable(0.5f, 1, new Runnable() {
            @Override
            public void run() {
                game.music.stopAll();
                game.setSplashScreen(new MainScreen(game));
            }
        });
        touchpad.save();
    }

    // TODO game touchpad
    @Override
    public void onTouchDown(int keyCode) {
        keyDown(keyCode);
    }

    @Override
    public void onTouchUp(int keyCode) {
        keyUp(keyCode);
    }

    // TODO controllers
    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        switch (buttonCode) {
            case 1:
                keyDown(Input.Keys.ENTER);
                break;
            case 2:
                keyDown(Input.Keys.ESCAPE);
                break;
            case 4:
                keyDown(Input.Keys.Z);
                break;
            case 5:
                keyDown(Input.Keys.X);
                break;
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        switch (buttonCode) {
            case 4:
                keyUp(Input.Keys.Z);
                break;
            case 5:
                keyUp(Input.Keys.X);
                break;
        }
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        switch (value) {
            case east:
                keyDown(Input.Keys.RIGHT);
                break;
            case west:
                keyDown(Input.Keys.LEFT);
                break;
            case north:
                keyDown(Input.Keys.UP);
                break;
            case south:
                keyDown(Input.Keys.DOWN);
                break;
                default:
                    action.control().clear(Control.idMotion);
        }
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }
}
