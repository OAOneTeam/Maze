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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Base64Coder;
import com.second_encounter.alexandr.klad_se.ActivityRequestHandler;
import com.second_encounter.alexandr.klad_se.Controls;
import com.second_encounter.alexandr.klad_se.Exit;
import com.second_encounter.alexandr.klad_se.GS;
import com.second_encounter.alexandr.klad_se.Instruction;
import com.second_encounter.alexandr.klad_se.Main;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.Notice;
import com.second_encounter.alexandr.klad_se.Price;
import com.second_encounter.alexandr.klad_se.PrivacyWarning;
import com.second_encounter.alexandr.klad_se.Records;
import com.second_encounter.alexandr.klad_se.Scroll;
import com.second_encounter.alexandr.klad_se.Select;
import com.second_encounter.alexandr.klad_se.Stars;
import com.second_encounter.alexandr.klad_se.Stripes;
import com.second_encounter.alexandr.klad_se.TextPane;
import com.second_encounter.alexandr.klad_se.Tools;
import com.second_encounter.alexandr.klad_se.lib.MWindow;

import java.util.ArrayList;

public class MainScreen extends InputAdapter implements Screen, ControllerListener {

    private Maze game;
    private Texture space;
    private Stripes stripes;
    private Stars stars;
    private PrivacyWarning privacyWarning;
    private Main main;
    private Select select;
    private Instruction instruction;
    private Controls controls;
    private Records records;
    private Scroll scroll;
    private Price price;
    private Notice notice;
    private Exit exit;
    private float sx, sy;

    public MainScreen(final Maze game) {
        this.game = game;
        space = game.assetManager.get("common/space.png", Texture.class);
        space.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        space.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        stripes = new Stripes(game);
        stars = new Stars(game, 128);
        privacyWarning = new PrivacyWarning(game, new PrivacyWarning.PrivacyWarningListener() {
            @Override
            public void onClose() {
                privacyWarning.hide();
                game.delayedRunnable(0.5f, 1, new Runnable() {
                    @Override
                    public void run() {
                        main.show();
                    }
                });
                game.config.showPrivacyWarning = false;
            }

            @Override
            public void onPrivacyPolicy() {
                Gdx.net.openURI(GS.privacyPolicyLink);
            }
        });
        main = new Main(game, new Main.MainMenuListener() {
            @Override
            public void onGame() {
                if (game.config.level[0] == 1 && game.config.level[1] == 1) {
                    game.music.stopAll();
                    game.setSplashScreen(new GameScreen(game, 1));
                    return;
                }
                main.hide();
                select.build();
                select.show();
            }

            @Override
            public void onInstruction() {
                main.hide();
                instruction.show();
            }

            @Override
            public void onControls() {
                main.hide();
                controls.show();
            }

            @Override
            public void onRecords() {
                main.hide();
                records.show();
            }

            @Override
            public void onExit() {
                main.hide();
                exit.show();
            }
        });
        //
        select = new Select(game, new Select.SelectListener() {
            @Override
            public void onStart(int number) {
                game.music.stopAll();
                game.setSplashScreen(new GameScreen(game, number));
            }

            @Override
            public void onClosedLevel() {
                game.shadow.show();
                scroll.build(false, true, false, false, "");
                scroll.show();
            }

            @Override
            public void onOne() {
                game.shadow.show();
                scroll.build(false, false, true, false, "");
                scroll.show();
            }

            @Override
            public void onAll() {
                game.shadow.show();
                scroll.build(false, false, false, true, "");
                scroll.show();
            }

            @Override
            public void onOneAndAll() {
                game.shadow.show();
                scroll.build(false, false, game.config.level[0] < 26, true, "");
                scroll.show();
            }

            @Override
            public void onClose() {
                back();
            }
        });
        //
        instruction = new Instruction(game, new Instruction.InstructionListener() {
            @Override
            public void onClose() {
                back();
            }
        });
        //
        controls = new Controls(game, new Controls.ControlsListener() {
            @Override
            public void onClose() {
                back();
            }
        });
        //
        records = new Records(game, new Records.RecordsListener() {
            @Override
            public void onClose() {
                back();
            }
        });
        //
        scroll = new Scroll(game, new Scroll.ScrollListener() {
            @Override
            public void onJump() {

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
                scroll.hide(new Runnable() {
                    @Override
                    public void run() {
                        price.show(Price.price250);
                    }
                });
            }

            @Override
            public void onAll() {
                scroll.hide(new Runnable() {
                    @Override
                    public void run() {
                        price.show(Price.price400);
                    }
                });
            }

            @Override
            public void onClose() {
                back();
            }
        });
        //
        price = new Price(game, new MWindow.MWindowListener() {
            @Override
            public void onApply() {
                price.hide(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Purchase
                        switch (price.getPrice()) {
                            case Price.price099:
                                game.handler.initiatePurchaseFlow(GS.sku5Lives);
                                break;
                            case Price.price130:
                                game.handler.initiatePurchaseFlow(GS.sku10Lives);
                                break;
                            case Price.price160:
                                game.handler.initiatePurchaseFlow(GS.sku20Lives);
                                break;
                            case Price.price250:
                                game.handler.initiatePurchaseFlow(GS.skuSecondChapter);
                                break;
                            case Price.price400:
                                game.handler.initiatePurchaseFlow(GS.skuAllFeatures);
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
                                    if (skus.get(last).equals(GS.sku5Lives)) {
                                        game.config.level[0] = game.config.level[1];
                                        game.config.gold[0] = game.config.gold[1];
                                        game.config.live = 5;
                                    }
                                    if (skus.get(last).equals(GS.sku10Lives)) {
                                        game.config.level[0] = game.config.level[1];
                                        game.config.gold[0] = game.config.gold[1];
                                        game.config.live = 10;
                                    }
                                    if (skus.get(last).equals(GS.sku20Lives)) {
                                        game.config.level[0] = game.config.level[1];
                                        game.config.gold[0] = game.config.gold[1];
                                        game.config.live = 20;
                                    }
                                    if (skus.get(last).equals(GS.skuSecondChapter)) {
                                        game.config.level[0] = 26;
                                        game.config.level[1] = 26;
                                        game.config.live += 10;
                                    }
                                    if (skus.get(last).equals(GS.skuAllFeatures)) {
                                        game.config.level[0] = 65;
                                        game.config.level[1] = 66;
                                        game.config.live += 10;
                                    }
                                    game.prefs().remove(Base64Coder.encodeString("room_level"));
                                    select.build();
                                }
                                game.shadow.hide();
                                select.focus();
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
        //
        if (game.config.firstStart) {
            // TODO answer event
            game.handler.eventFirstStart(true);
            //
            if (GS.SC.equals(GS.skuSecondChapter)) {
                game.config.level[0] = 26;
                game.config.level[1] = 26;
                GS.SC = "OAOneTeam";
            }
            if (GS.AF.equals(GS.skuAllFeatures)) {
                game.config.level[0] = 65;
                game.config.level[1] = 66;
                GS.AF = "OAOneTeam";
            }
        }
        else {
            // TODO answer event
            game.handler.eventFirstStart(false);
        }
        game.config.firstStart = false;
        //
        notice = new Notice(game, null);
        //
        exit = new Exit(game, game.bundle.get("exit"), new MWindow.MWindowListener() {
            @Override
            public void onApply() {
                exit.hide(new Runnable() {
                    @Override
                    public void run() {
                        close();
                    }
                });
            }

            @Override
            public void onClose() {
                exit.hide(new Runnable() {
                    @Override
                    public void run() {
                        main.show();
                    }
                });
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
        stripes.show();
        if (game.config.showPrivacyWarning)
            privacyWarning.show();
        else
            main.show();
        game.delayedRunnable(0.5f, 2, new Runnable() {
            @Override
            public void run() {
                game.music.play("main");
            }
        });
    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        stars.draw(game.batch, delta);
        game.batch.draw(space, sx, sy);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        sx = Tools.half(game.stage.getWidth() - space.getWidth());
        sy = Tools.half(game.stage.getHeight() - space.getHeight());
        stripes.resize();
        stars.resize(0, stripes.spaceY(), stripes.spaceWidth(), stripes.spaceHeight());
        privacyWarning.resize();
        main.resize();
        select.resize();
        instruction.resize();
        controls.resize();
        records.resize();
        scroll.resize();
        price.resize();
        notice.resize();
        exit.resize();
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
        return scroll.actions() | price.actions() | exit.actions();
    }

    @Override
    public boolean keyDown (int keycode) {
        if (actions())
            return false;
        if (keycode == Input.Keys.UP) {
            if (main.isShown) {
                main.cursorUp();
                return false;
            }
        }
        if (keycode == Input.Keys.DOWN) {
            if (main.isShown) {
                main.cursorDown();
                return false;
            }
        }
        if (keycode == Input.Keys.ENTER) {
            if (main.isShown) {
                main.enter();
                return false;
            }
            if (price.isShown) {
                price.apply();
                return false;
            }
            if (exit.isShown) {
                exit.apply();
                return false;
            }
        }
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            back();
            return false;
        }
        return false;
    }

    private void back() {
        if (actions())
            return;
        if (exit.isShown) {
            exit.close();
            return;
        }
        if (scroll.isShown) {
            scroll.hide(new Runnable() {
                @Override
                public void run() {
                    game.shadow.hide();
                    select.focus();
                }
            });
            return;
        }
        if (price.isShown) {
            price.close();
            return;
        }
        if (notice.isShown) {
            notice.close();
            return;
        }
        if (main.isShown) {
            main.cursorMove(5);
            return;
        }
        if (select.isShown) {
            select.hide();
            main.show();
            return;
        }
        if (instruction.isShown) {
            instruction.hide();
            main.show();
            return;
        }
        if (controls.isShown) {
            if (controls.hide())
                main.show();
            else {
                notice.combineButtons(true);
                notice.setListener(new MWindow.MWindowListener() {
                    @Override
                    public void onApply() {
                        notice.hide(new Runnable() {
                            @Override
                            public void run() {
                                main.show();
                            }
                        });
                    }

                    @Override
                    public void onClose() {

                    }
                });
                TextPane pane = new TextPane(game, 8, 0.45f);
                pane.set(game.bundle.get("controls_changed"), 0);
                notice.set(pane, null);
                notice.show();
            }
            return;
        }
        if (records.isShown) {
            records.hide();
            main.show();
        }
    }

    private void close() {
        game.delayedRunnable(0.5f, 1, new Runnable() {
            @Override
            public void run() {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        switch (buttonCode) {
            case 0:
                keyDown(Input.Keys.DOWN);
                break;
            case 1:
                keyDown(Input.Keys.ENTER);
                break;
            case 2:
                keyDown(Input.Keys.ESCAPE);
                break;
            case 3:
                keyDown(Input.Keys.UP);
                break;
        }
        System.out.println(buttonCode);
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        switch (value) {
            case north:
                keyDown(Input.Keys.UP);
                break;
            case south:
                keyDown(Input.Keys.DOWN);
                break;
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
