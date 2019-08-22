package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Base64Coder;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.lib.DeltaTimer;
import com.second_encounter.alexandr.klad_se.lib.Pair;

public class Character {

    private static final int eventNo    = 0x00;
    private static final int eventFall  = 0x01;
    private static final int eventLeft  = 0x02;
    private static final int eventRight = 0x03;
    private static final int eventLift  = 0x04;
    private static final int eventSlide = 0x05;

    private static final int[] sequence = { 0, 4, 5, 6, 7, 8, 9, 3, 2, 1, 1, 2, 3, 10, 11 };

    private Maze game;
    private CharacterListener listener;
    private Room room;
    private TextureRegion[] region = new TextureRegion[sequence.length];
    private DeltaTimer figureTimer = new DeltaTimer(0);
    private Pair<Integer> pair, oldPair, exitPair;
    private Color color = Color.WHITE;
    private float x, y, speed, sleepTime;
    private int id, event, figure;
    private boolean isEnabled, isVisible, isAlive, falling, isDangerous;
    private String prefix;

    public Character(Maze game, Room room, int id) {
        this.game = game;
        this.room = room;
        this.id = id;
        for (int i = 0; i < sequence.length; i++)
            region[i] = game.commonAtlas.findRegion((id == Room.idMan ? 'm' : 'e') + "_" + sequence[i] + "_");
        prefix = "character_" + id + "_";
    }

    private void init(Color color, boolean isLoaded) {
        this.color = color;
        event = eventNo;
        figure = 0;
        speed = game.config.getManSpeed();
        sleepTime = isLoaded ? 0.2f : game.config.getManSleepTime();
        figureTimer.set(game.config.getManFigureTime());
        if (oldPair != null)
            room.get(oldPair.a, oldPair.b).enemyId = 0;
        oldPair = room.getFromId(id);
        if (isDevils()) {
            room.get(oldPair.a, oldPair.b).enemyId = id;
            speed = game.config.getDevilSpeed();
            sleepTime = isLoaded ? 0.2f : game.config.getDevilSleepTime();
            figureTimer.set(game.config.getDevilFigureTime());
        }
        pair = room.getFromId(id);
        x = room.get(pair.a, pair.b).x;
        y = room.get(pair.a, pair.b).y;
        exitPair = room.getFromId(Room.idExit);
        isEnabled = x + y > 0;
        isVisible = true;
        isAlive = true;
        falling = false;
        isDangerous = false;
    }

    public void initialize(Color color) {
        init(color, room.isLoaded());
        if (room.isLoaded()) {
            load();
            if (sleepTime > 0 || !isAlive)
                init(color, false);
        }
    }

    public void setListener(CharacterListener listener) {
        this.listener = listener;
    }

    public void draw(float delta, boolean update) {
        if (isEnabled) {
            if (sleepTime > 0) {
                if (update)
                    sleepTime -= delta;
                return;
            }
            if (isAlive && update) {
                pair.set((int) (x + 16) / 32, (int) (y + 11) / 22);
                int unit = room.get(pair.a, pair.b).id;
                if (isDevils()) {
                    if (!oldPair.a.equals(pair.a) || !oldPair.b.equals(pair.b)) {
                        room.get(oldPair.a, oldPair.b).enemyId = 0;
                        oldPair.set(pair.a, pair.b);
                        room.get(oldPair.a, oldPair.b).enemyId = id;
                    }
                    if (unit == 4 || unit == 5) {
                        // TODO devil fell into the water
                        game.delayedRunnable(0.5f, 2, new Runnable() {
                            @Override
                            public void run() {
                                game.delayedRunnable(3f, 2, new Runnable() {
                                    @Override
                                    public void run() {
                                        initialize(color);
                                    }
                                });
                                figure = 14;
                                isDangerous = false;
                            }
                        });
                        figure = 13;
                        isAlive = false;
                        if (listener != null)
                            listener.onDeath();
                    }
                }
                else {
                    if (unit == 1)
                        room.haveGold(pair);
                    if (unit == 2)
                        room.openDoor(pair);
                    if (pair.a.equals(exitPair.a) && pair.b.equals(exitPair.b))
                        room.levelComplete(false);
                    if (unit == 4 || unit == 5) {
                        // TODO man fell into the water
                        game.delayedRunnable(0.5f, 2, new Runnable() {
                            @Override
                            public void run() {
                                x = room.get(pair.a, pair.b).x;
                                y = room.get(pair.a, pair.b).y;
                                figure = 14;
                                listener.onDeath();
                            }
                        });
                        figure = 13;
                        isAlive = false;
                    }
                }
                isDangerous = true;
            }
            if (isVisible) {
                if (isDangerous)
                    game.batch.setColor(color);
                game.batch.draw(region[figure], room.getX() + x, room.getTop() - y);
                game.batch.setColor(Color.WHITE);
            }
        }
    }

    public boolean onSolid(float delta) {
        if (sleepTime > 0)
            return false;
        if (isEnabled && isAlive) {
            if (room.get(pair.a, pair.b).id != 6) {
                int unit = (isDevils() && room.get(pair.a, pair.b + 1).enemyId != 0) ? 9 : room.get(pair.a, pair.b + 1).id;
                if (room.isMagicWall(unit))
                    unit = 9;
                if (unit < 2 || unit == 4 || unit == 5 || (unit == 7 && falling) || unit > 9) {
                    if (exactly_vertical(delta)) {
                        update_fall(delta);
                        falling = true;
                    }
                    return false;
                }
                if (y < room.get(pair.a, pair.b).y) {
                    update_fall(delta);
                    if (y > room.get(pair.a, pair.b).y) {
                        y = room.get(pair.a, pair.b).y;
                        if (!isDevils())
                            game.sound.play("step");
                        falling = false;
                    }
                    else
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean left(float delta) {
        int unit = (isDevils() && room.get(pair.a - 1, pair.b).enemyId != 0) ? 9 : room.get(pair.a - 1, pair.b).id;
        if (room.isImmured(pair))
            return false;
        if (room.isMagicWall(unit))
            unit = 9;
        if (unit == 3 || unit == (id == Room.idMan ? 3 : 2) || unit == 9) {
            if (x > room.get(pair.a, pair.b).x) {
                update_left(delta);
                if (x < room.get(pair.a, pair.b).x)
                    x = room.get(pair.a, pair.b).x;
                else
                    return true;
            }
        }
        else {
            if (exactly_horizontal(delta))
                update_left(delta);
            return true;
        }
        return false;
    }

    public boolean right(float delta) {
        int unit = (isDevils() && room.get(pair.a + 1, pair.b).enemyId != 0) ? 9 : room.get(pair.a + 1, pair.b).id;
        if (room.isImmured(pair))
            return false;
        if (room.isMagicWall(unit))
            unit = 9;
        if (unit == 3 || unit == (id == Room.idMan ? 3 : 2) || unit == 9) {
            if (x < room.get(pair.a, pair.b).x) {
                update_right(delta);
                if (x > room.get(pair.a, pair.b).x)
                    x = room.get(pair.a, pair.b).x;
                else
                    return true;
            }
        }
        else {
            if (exactly_horizontal(delta))
                update_right(delta);
            return true;
        }
        return false;
    }

    public void lift(float delta) {
        if (room.get(pair.a, pair.b).id == 6 || room.get(pair.a, pair.b + 1).id == 6) {
            int unit = (isDevils() && room.get(pair.a, pair.b - 1).enemyId != 0) ? 9 : room.get(pair.a, pair.b - 1).id;
            if (room.isMagicWall(unit))
                unit = 9;
            if (unit != 2 && unit != 3 && unit != 9 && room.get(pair.a, pair.b).id == 6) {
                if (exactly_vertical(delta))
                    update_lift(delta);
            }
            else {
                if (y > room.get(pair.a, pair.b).y) {
                    update_lift(delta);
                    if (y < room.get(pair.a, pair.b).y)
                        y = room.get(pair.a, pair.b).y;
                }
            }
        }
    }

    public void slide(float delta) {
        if (room.get(pair.a, pair.b).id == 6 || room.get(pair.a, pair.b + 1).id == 6) {
            int unit = (isDevils() && room.get(pair.a, pair.b + 1).enemyId != 0) ? 9 : room.get(pair.a, pair.b + 1).id;
            if (room.isMagicWall(unit))
                unit = 9;
            if (unit == 2 || unit == 3 || unit == 7 || unit == 9) {
                if (y < room.get(pair.a, pair.b).y) {
                    update_slide(delta);
                    if (y > room.get(pair.a, pair.b).y)
                        y = room.get(pair.a, pair.b).y;
                }
            }
            else
                if (exactly_vertical(delta))
                    update_slide(delta);
        }
    }

    private boolean exactly_horizontal(float delta) {
        if (y > room.get(pair.a, pair.b).y) {
            update_lift(delta);
            if (y < room.get(pair.a, pair.b).y)
                y = room.get(pair.a, pair.b).y;
            else
                return false;
        }
        if (y < room.get(pair.a, pair.b).y) {
            update_slide(delta);
            if (y > room.get(pair.a, pair.b).y)
                y = room.get(pair.a, pair.b).y;
            else
                return false;
        }
        return true;
    }

    private boolean exactly_vertical(float delta) {
        if (x < room.get(pair.a, pair.b).x) {
            update_right(delta);
            if (x > room.get(pair.a, pair.b).x)
                x = room.get(pair.a, pair.b).x;
            else
                return false;
        }
        if (x > room.get(pair.a, pair.b).x) {
            update_left(delta);
            if (x < room.get(pair.a, pair.b).x)
                x = room.get(pair.a, pair.b).x;
            else
                return false;
        }
        return true;
    }

    private void update_fall(float delta) {
        if (event != eventFall)
            if (event != eventNo)
                figure = 0;
        y += speed * delta;
        event = eventFall;
    }

    private void update_left(float delta) {
        if (event != eventLeft) {
            if (!isDevils())
                game.sound.play("step");
            figureTimer.reset();
            figure = 1;
        }
        else
            if (figureTimer.action(delta))
                if (++figure > 3) {
                    if (!isDevils())
                        game.sound.play("step");
                    figure = 1;
                }
        x -= speed * delta;
        event = eventLeft;
    }

    private void update_right(float delta) {
        if (event != eventRight) {
            if (!isDevils())
                game.sound.play("step");
            figureTimer.reset();
            figure = 4;
        }
        else
            if (figureTimer.action(delta))
                if (++figure > 6) {
                    if (!isDevils())
                        game.sound.play("step");
                    figure = 4;
                }
        x += speed * delta;
        event = eventRight;
    }

    private void update_lift(float delta) {
        if (event != eventLift) {
            if (!isDevils())
                game.sound.play("step");
            figureTimer.reset();
            figure = 7;
        }
        else
            if (figureTimer.action(delta))
                if (++figure > 9) {
                    if (!isDevils())
                        game.sound.play("step");
                    figure = 7;
                }
        y -= speed * delta;
        event = eventLift;
    }

    private void update_slide(float delta) {
        if (event != eventSlide) {
            if (!isDevils())
                game.sound.play("step");
            figureTimer.reset();
            figure = 10;
        }
        else
            if (figureTimer.action(delta))
                if (++figure > 12) {
                    if (!isDevils())
                        game.sound.play("step");
                    figure = 10;
                }
        y += speed * delta;
        event = eventSlide;
    }

    private boolean isDevils() {
        return id == Room.idDickensFirst || id == Room.idDickensSecond;
    }

    public int getA() {
        return pair.a;
    }

    public int getB() {
        return pair.b;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void putToDeath() {
        isVisible = isAlive = false;
    }

    public boolean isAlive() {
        return isAlive && sleepTime <= 0;
    }

    public boolean isDangerous() {
        return isDangerous;
    }

    public interface CharacterListener {
        void onDeath();
    }

    public void save() {
        game.prefs().putInteger(Base64Coder.encodeString(prefix + "event"), event);
        game.prefs().putInteger(Base64Coder.encodeString(prefix + "figure"), figure);
        game.prefs().putInteger(Base64Coder.encodeString(prefix + "oldPair_a"), oldPair.a);
        game.prefs().putInteger(Base64Coder.encodeString(prefix + "oldPair_b"), oldPair.b);
        game.prefs().putInteger(Base64Coder.encodeString(prefix + "pair_a"), pair.a);
        game.prefs().putInteger(Base64Coder.encodeString(prefix + "pair_b"), pair.b);
        game.prefs().putFloat(Base64Coder.encodeString(prefix + "sleepTime"), sleepTime);
        game.prefs().putFloat(Base64Coder.encodeString(prefix + "x_"), x);
        game.prefs().putFloat(Base64Coder.encodeString(prefix + "y_"), y);
        game.prefs().putBoolean(Base64Coder.encodeString(prefix + "isEnabled"), isEnabled);
        game.prefs().putBoolean(Base64Coder.encodeString(prefix + "isVisible"), isVisible);
        game.prefs().putBoolean(Base64Coder.encodeString(prefix + "isAlive"), isAlive);
        game.prefs().putBoolean(Base64Coder.encodeString(prefix + "falling"), falling);
        game.prefs().putBoolean(Base64Coder.encodeString(prefix + "isDangerous"), isDangerous);
        game.prefs().flush();
    }

    private void load() {
        event = game.prefs().getInteger(Base64Coder.encodeString(prefix + "event"), 0);
        figure = game.prefs().getInteger(Base64Coder.encodeString(prefix + "figure"), 0);
        oldPair.a = game.prefs().getInteger(Base64Coder.encodeString(prefix + "oldPair_a"), 0);
        oldPair.b = game.prefs().getInteger(Base64Coder.encodeString(prefix + "oldPair_b"), 0);
        pair.a = game.prefs().getInteger(Base64Coder.encodeString(prefix + "pair_a"), 0);
        pair.b = game.prefs().getInteger(Base64Coder.encodeString(prefix + "pair_b"), 0);
        sleepTime = game.prefs().getFloat(Base64Coder.encodeString(prefix + "sleepTime"), 0);
        x = game.prefs().getFloat(Base64Coder.encodeString(prefix + "x_"), 0);
        y = game.prefs().getFloat(Base64Coder.encodeString(prefix + "y_"), 0);
        isEnabled = game.prefs().getBoolean(Base64Coder.encodeString(prefix + "isEnabled"), false);
        isVisible = game.prefs().getBoolean(Base64Coder.encodeString(prefix + "isVisible"), false);
        isAlive = game.prefs().getBoolean(Base64Coder.encodeString(prefix + "isAlive"), false);
        falling = game.prefs().getBoolean(Base64Coder.encodeString(prefix + "falling"), false);
        isDangerous = game.prefs().getBoolean(Base64Coder.encodeString(prefix + "isDangerous"), false);
        delete();
    }

    private void delete() {
        game.prefs().remove(Base64Coder.encodeString(prefix + "event"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "figure"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "oldPair_a"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "oldPair_b"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "pair_a"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "pair_b"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "sleepTime"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "x_"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "y_"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "isEnabled"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "isVisible"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "isAlive"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "falling"));
        game.prefs().remove(Base64Coder.encodeString(prefix + "isDangerous"));
    }
}
