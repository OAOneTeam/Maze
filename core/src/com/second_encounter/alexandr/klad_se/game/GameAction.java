package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.second_encounter.alexandr.klad_se.ActivityRequestHandler;
import com.second_encounter.alexandr.klad_se.GS;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.Notice;
import com.second_encounter.alexandr.klad_se.TextPane;
import com.second_encounter.alexandr.klad_se.Tools;
import com.second_encounter.alexandr.klad_se.lib.MWindow;
import com.second_encounter.alexandr.klad_se.screens.MainScreen;

public class GameAction {

    private Maze game;
    private Room room;
    private Progress progress;
    private Character man;
    private Bullet bullet;
    private Character[] devils = new Character[2];
    private Control control;
    private Notice notice;
    private AdMenu adMenu;
    private Image adProgressImage;
    private ImageButton adButtonClose;
    private int adCounter = GS.adDefault;
    private boolean action, initialized = false, adLoading = false, adCanceled, adReward = false, jump = false;

    public GameAction(final Maze game, int startLevel) {
        this.game = game;
        if (game.config.live == 0)
            game.config.live = game.config.level[1] > 25 ? 10 : 5;
        room = new Room(game, startLevel);
        room.setListener(new Room.RoomListener() {
            @Override
            public void onLevelComplete(final int nextLevel, final int bonus, boolean one, boolean all) {
                if (bonus > 0 || all) {
                    setAction(false);
                    game.shadow.show();
                    notice.combineButtons(true);
                    notice.setListener(new MWindow.MWindowListener() {
                        @Override
                        public void onApply() {
                            notice.hide(new Runnable() {
                                @Override
                                public void run() {
                                    setAction(true);
                                    game.shadow.hide();
                                    progress.setLive(game.config.live += bonus);
                                    room.initialize(nextLevel);
                                    game.sound.play("next");
                                    initialized = false;
                                }
                            });
                        }

                        @Override
                        public void onClose() {

                        }
                    });
                    TextPane pane = new TextPane(game, 8, 0.4f);
                    pane.set(all ? game.bundle.get("all_complete") : one ? game.bundle.get("one_complete") : game.bundle.get("life_obtained"), 0);
                    notice.set(pane, one || all ? null : new Image(game.commonAtlas.findRegion("live_plus")));
                    notice.show();
                    game.sound.play("magic");
                    return;
                }
                room.initialize(nextLevel);
                game.sound.play("next");
                initialized = false;
            }

            @Override
            public void onHaveGold(int value, boolean goldenKey) {
                if (goldenKey)
                    game.sound.play("key");
                else
                    game.sound.play("gold");
                progress.setGold(value);
            }

            @Override
            public void onOpenDoor() {
                game.sound.play("door");
            }

            @Override
            public void onFirstReady() {
                progress.setVisible(true);
            }

            @Override
            public void onReady(int level) {
                progress.setMaze(level);
            }
        });
        progress = new Progress(game, room, startLevel, game.config.gold[0], game.config.live);
        progress.setVisible(false);
        man = new Character(game, room, Room.idMan);
        man.setListener(new Character.CharacterListener() {
            @Override
            public void onDeath() {
                takeLife();
            }
        });
        bullet = new Bullet(game, room, man);
        devils[0] = new Character(game, room, Room.idDickensFirst);
        devils[1] = new Character(game, room, Room.idDickensSecond);
        control = new Control();
        notice = new Notice(game, null);
        adMenu = new AdMenu(game, new AdMenu.AdMenuListener() {
            @Override
            public void onShowAd() {
                adMenu.hide(new Runnable() {
                    @Override
                    public void run() {
                        // TODO answer event
                        game.handler.eventViewingAds(true);
                        //
                        game.handler.loadAd(new ActivityRequestHandler.LoadAdListener() {
                            @Override
                            public void onRewardedVideoAdLoaded() {
                                adReward = false;
                                if (adCanceled)
                                    return;
                                game.delayedRunnable(0.5f, 1, new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO answer event
                                        game.handler.eventAdLoading(true);
                                        //
                                        game.handler.showAd(new ActivityRequestHandler.ShowAdListener() {
                                            @Override
                                            public void onRewardedVideoAdClosed() {
                                                Gdx.app.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adNotice(game.bundle.get("ad_offer_" + (adReward ? "life_obtained" : "need_to_watch")), adReward);
                                                        adLoading = false;
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onRewarded() {
                                                Gdx.app.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // TODO answer event
                                                        game.handler.eventAdViewed();
                                                        //
                                                        adReward = true;
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                                adProgressImage.clearActions();
                                adProgressImage.remove();
                                adButtonClose.remove();
                            }

                            @Override
                            public void onRewardedVideoAdFailedToLoad() {
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO answer event
                                        game.handler.eventAdLoading(false);
                                        //
                                        game.delayedRunnable(0.5f, 1, new Runnable() {
                                            @Override
                                            public void run() {
                                                adNotice(game.bundle.get("ad_offer_error"), false);
                                                adLoading = false;
                                            }
                                        });
                                        adProgressImage.clearActions();
                                        adProgressImage.remove();
                                        adButtonClose.remove();
                                    }
                                });
                            }
                        });
                        adLoading = true;
                        if (game.handler.adIsLoaded())
                            return;
                        adProgressImage.setRotation(0);
                        adProgressImage.addAction(
                                Actions.forever(
                                        Actions.sequence(
                                            Actions.rotateTo(360, 1.5f, Interpolation.slowFast),
                                            Actions.rotateTo(0, 1.5f, Interpolation.fastSlow)
                                        )
                                )
                        );
                        game.stage.addActor(adProgressImage);
                        game.stage.addActor(adButtonClose);
                    }
                });
                adCanceled = false;
            }

            @Override
            public void onClose() {
                adMenu.hide(new Runnable() {
                    @Override
                    public void run() {
                        // TODO answer event
                        game.handler.eventViewingAds(false);
                        //
                        setAction(true);
                        game.shadow.hide();
                        room.restart();
                        initialized = false;
                    }
                });
            }
        });
        adProgressImage = new Image(game.commonAtlas.findRegion("progress"));
        adProgressImage.setOrigin(Align.center);
        adButtonClose = new ImageButton(game.skinCommon, "close");
        adButtonClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setAction(true);
                game.shadow.hide();
                room.restart();
                adProgressImage.clearActions();
                adProgressImage.remove();
                adButtonClose.remove();
                initialized = false;
                adCanceled = true;
            }
        });
    }

    public void show() {
        progress.show();
    }

    public void draw(float delta) {
        if (room.draw(delta, action)) {
            if (room.isReady()) {
                if (initialized) {
                    if (man.onSolid(delta)) {
                        switch (control.get(Control.idMotion)) {
                            case Control.left:
                                man.left(delta);
                                break;
                            case Control.right:
                                man.right(delta);
                                break;
                            case Control.up:
                                man.lift(delta);
                                break;
                            case Control.down:
                                man.slide(delta);
                                break;
                        }
                    }
                    man.draw(delta, true);
                    if (man.isAlive()) {
                        switch (control.get(Control.idShoot)) {
                            case Control.shootLeft:
                                bullet.start(Bullet.dLeft);
                                break;
                            case Control.shootRight:
                                bullet.start(Bullet.dRight);
                                break;
                        }
                    }
                    bullet.draw(delta);
                    for (Character dickens : devils) {
                        if (man.isAlive()) {
                            if (dickens.isDangerous()) {
                                if (dickens.getA() == man.getA() && dickens.getB() == man.getB()) {
                                    man.putToDeath();
                                    takeLife();
                                }
                            }
                            if (dickens.onSolid(delta)) {
                                boolean move = false;
                                if (dickens.getA() > man.getA())
                                    move = dickens.left(delta);
                                if (dickens.getA() < man.getA())
                                    move = dickens.right(delta);
                                if (dickens.getB() > man.getB() && !move)
                                    dickens.lift(delta);
                                if (dickens.getB() < man.getB() && !move)
                                    dickens.slide(delta);
                            }
                        }
                        dickens.draw(delta, man.isAlive());
                    }
                    if (jump) {
                        room.levelComplete(true);
                        jump = false;
                    }
                }
                else {
                    game.clearTimer(2);
                    man.initialize(Color.WHITE);
                    devils[0].initialize(Color.GREEN);
                    devils[1].initialize(Color.GREEN);
                    bullet.reset();
                    room.resetLoading();
                    initialized = true;
                }
            }
        }
    }

    public void resize() {
        room.resize();
        progress.resize();
        notice.resize();
        adMenu.resize();
        adProgressImage.setPosition(Tools.half(game.stage.getWidth() - adProgressImage.getWidth()), Tools.half(game.stage.getHeight() - adProgressImage.getHeight()));
        adButtonClose.setPosition(Tools.half(game.stage.getWidth() - adButtonClose.getWidth()), Tools.half(game.stage.getHeight() - adButtonClose.getHeight()));
    }

    public void addLive(int count) {
        progress.setLive(game.config.live += count);
        game.sound.play("bonus");
    }

    public void takeLife() {
        // TODO answer event
        game.handler.eventLevelEnd(room.getLevel(), false);
        //
        game.clearTimer(2);
        progress.setLive(--game.config.live);
        float waitTime = 5f;
        if (game.config.live == 0) {
            game.config.gold[0] = 0;
            game.config.level[0] = 1;
            game.music.stopAll();
            game.music.play("dead");
            waitTime = 35f;
        }
        else
            game.music.play("life_down");
        game.delayedRunnable(game.config.music ? waitTime : 2f, 2, new Runnable() {
            @Override
            public void run() {
                if (game.config.live == 0)
                    game.setSplashScreen(new MainScreen(game));
                else {
                    if (room.getLevel() == game.config.level[0] && getAction())
                        if (room.getLevel() > 1)
                            adCounter--;
                    if (adCounter == 0) {
                        setAction(false);
                        game.shadow.show();
                        adMenu.show();
                        adCounter = GS.adDefault;
                    }
                    else {
                        room.restart();
                        initialized = false;
                    }
                }
            }
        });
    }

    private void adNotice(String text, final boolean live) {
        notice.combineButtons(true);
        notice.setListener(new MWindow.MWindowListener() {
            @Override
            public void onApply() {
                notice.hide(new Runnable() {
                    @Override
                    public void run() {
                        setAction(true);
                        game.shadow.hide();
                        if (live)
                            progress.setLive(++game.config.live);
                        room.restart();
                        initialized = false;
                    }
                });
            }

            @Override
            public void onClose() {

            }
        });
        Label label = new Label(text, game.skinCommon);
        label.setFontScale(0.5f);
        notice.set(label);
        notice.show();
        if (live)
            game.sound.play("magic");
    }

    public void jump() {
        jump = true;
    }

    public Room room() {
        return room;
    }

    public Character man() {
        return man;
    }

    public Control control() {
        return control;
    }

    public Notice notice() {
        return notice;
    }

    public AdMenu adMenu() {
        return adMenu;
    }

    public Progress progress() {
        return progress;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public boolean getAction() {
        return action;
    }

    public boolean adIsLoading() {
        return adLoading;
    }

    public void save() {
        if (room.save(initialized && man.isAlive())) {
            man.save();
            devils[0].save();
            devils[1].save();
        }
    }
}
