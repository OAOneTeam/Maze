package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.ByteArray;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.Tools;
import com.second_encounter.alexandr.klad_se.lib.Pair;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Room {

    public static final int idExit          = 0;
    public static final int idMan           = 2;
    public static final int idDickensFirst  = 4;
    public static final int idDickensSecond = 6;

    private static final float drawTime = 0.033f;

    private Maze game;
    private RoomListener listener;
    private Texture frame;
    private SeparateRegion first, second, exit;
    private Random random = new Random(System.currentTimeMillis());
    private Unit[][] units = new Unit[32][22];
    private Unit[] unitsOrder = new Unit[704];
    private int[] unitsCache = new int[21];
    private int level, from, row = 22, goldAmount, goldCollected, goldWithKey, counter;
    private float x, y, rowDrawTime;
    private boolean isLoaded;

    public Room(Maze game, int startLevel) {
        this.game = game;
        frame = game.assetManager.get("common/frame.png");
        frame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        frame.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        first = new SeparateRegion(game.commonAtlas.findRegion("first"));
        second = new SeparateRegion(game.commonAtlas.findRegion("second"));
        exit = new SeparateRegion(game.commonAtlas.findRegion("exit_" + game.config.getLanguage()));
        for (int b = 0; b < 22; b++) {
            for (int a = 0; a < 32; a++) {
                units[a][b] = new Unit(a * 32, b * 22, 0, 0);
            }
        }
        initialize(startLevel);
        counter = 0;
    }

    public void initialize(int level) {
        // TODO answer event
        game.handler.eventLevelStart(level);
        //
        from = 367 * (level - 1) + 15;
        row = 0;
        rowDrawTime = drawTime;
        if (this.level != level)
            goldWithKey = 0;
        this.level = level;
        int value = game.data.get(367 * (level - 1) + 8 + (1 + random.nextInt(6)));
        if (level <= 25) {
            while (value == goldWithKey)
                value = game.data.get(367 * (level - 1) + 8 + (1 + random.nextInt(6)));
        }

        if (isLoaded = load())
            row = 21;
        else {
            goldWithKey = value;
            goldAmount = 0;
            goldCollected = 0;
        }
        first.setPair(getFromId(idDickensFirst));
        second.setPair(getFromId(idDickensSecond));
        exit.setPair(getFromId(idExit));
    }

    public void restart() {
        initialize(level);
    }

    public void setListener(RoomListener listener) {
        this.listener = listener;
    }

    private void set(int a, int b, int value) {
        units[a][b].reset();
        units[a][b].part = level > 25 ? 1 : 0;
        units[a][b].id = value;
        if (value == 1)
            units[a][b].goldNumber = ++goldAmount;
    }

    public boolean draw(float delta, boolean action) {
        if (row < 22) {
            if (isLoaded)
                row++;
            else {
                if (rowDrawTime == drawTime) {
                    int a = 0;
                    while (a < 32) {
                        String code = String.format(Locale.getDefault(), "%2s", Integer.toHexString(game.data.get(from++))).replace(' ', '0');
                        set(a++, row, code.charAt(1) - 48);
                        set(a++, row, code.charAt(0) - 48);
                    }
                }
                rowDrawTime -= delta;
                if (rowDrawTime <= 0f) {
                    rowDrawTime = drawTime;
                    row++;
                }
            }
            if (row == 22) {
                if (counter++ == 0)
                    listener.onFirstReady();
                else
                    listener.onReady(level);
            }
        }
        else
            separateDraw(game.batch);
        int offset = 0, value;
        Arrays.fill(unitsCache, 0);
        // pass one
        for (int b = 0; b < 22; b++) {
            for (int a = 0; a < 32; a++) {
                if (action) {
                    // TODO update magic wall
                    if (units[a][b].wallTime > 0f) {
                        units[a][b].wallTime -= delta;
                        if (units[a][b].wallTime < 0f) {
                            if (units[a][b].id < 20) {
                                if (units[a][b].id == 0)
                                    units[a][b].id = 18;
                                else
                                    units[a][b].id++;
                                units[a][b].wallTime = game.config.getWallTime();
                            }
                            else {
                                units[a][b].id = 8;
                                units[a][b].wallTime = 0f;
                            }
                        }
                    }
                }
                units[a][b].number = ++unitsCache[units[a][b].id];
            }
        }
        // calculate offset
        for (int i = 0; i < unitsCache.length; i++) {
            value = unitsCache[i];
            unitsCache[i] = offset;
            offset += value;
        }
        // pass two
        for (int b = 0; b < 22; b++) {
            for (int a = 0; a < 32; a++)
                unitsOrder[unitsCache[units[a][b].id] + (units[a][b].number - 1)] = units[a][b];
        }
        // draw units order
        for (Unit unit : unitsOrder) {
            if (unit.id > 0)
                game.batch.draw(game.data.getSprite(unit.part, unit.id), getX() + unit.x, getTop() - unit.y);
        }
        game.batch.draw(frame, x - 27, y - 35);
        return action;
    }

    public void resize() {
        x = Tools.half(game.extendViewport.getWorldWidth() - getWidth());
        y = Tools.half(game.extendViewport.getWorldHeight() - getHeight() + (game.config.useGamepad ? 0 : 88));
    }

    public Pair<Integer> getFromId(int id) {
        int l = game.data.get(367 * (level - 1) + id);
        int h = game.data.get(367 * (level - 1) + id + 1);
        int b = (h * 256 + l) / 32;
        int a = (h * 256 + l) - b * 32;
        return new Pair<>(a, b);
    }

    public Unit get(int a, int b) {
        if (a < 0 || a > 31 || b < 0 || b > 21)
            return Unit.nullUnit;
        else
            return units[a][b];
    }

    public void haveGold(Pair<Integer> pair) {
        goldCollected++;
        game.config.gold[0]++;
        if (game.config.level[0] == game.config.level[1])
            game.config.gold[1] = game.config.gold[0];
        if (get(pair.a, pair.b).goldNumber == goldWithKey) {
            get(pair.a, pair.b).id = 10;
            // open all doors
            for (int b = 0; b < 22; b++) {
                for (int a = 0; a < 32; a++)
                    if (get(a, b).id == 3)
                        get(a, b).id = 2;
            }
        }
        else
            get(pair.a, pair.b).id = 0;
        listener.onHaveGold(game.config.gold[0], get(pair.a, pair.b).id > 0);
    }

    public void openDoor(Pair<Integer> pair) {
        get(pair.a, pair.b).id = 11;
        listener.onOpenDoor();
    }

    public void levelComplete(boolean collectGold) {
        // TODO answer event
        game.handler.eventLevelEnd(level, true);
        //
        if (collectGold)
            goldCollected = goldAmount;
        if (level > 25 && goldAmount != goldCollected)
            return;
        int bonus = level == game.config.level[1] && goldAmount == goldCollected ? game.data.get(367 * (level - 1) + 8) : 0;
        int oldLevel = level;
        int maxLevel = game.config.level[1];
        if (game.config.level[0] < ++level)
            game.config.level[0] = level > 65 ? 65 : level;
        if (game.config.level[1] < level)
            game.config.level[1] = level;
        if (level > 65)
            level = 1;
        listener.onLevelComplete(level, bonus, level == 26, oldLevel + 1 == 66 && maxLevel < 66);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getTop() {
        return y + getHeight() - 22;
    }

    public float getWidth() {
        return 32 * 32;
    }

    public float getHeight() {
        return 22 * 22;
    }

    public int getLevel() {
        return level;
    }

    public boolean isReady() {
        return row == 22;
    }

    public boolean isMagicWall(int unit) {
        return unit == 8 || (unit > 11 && unit < 18);
    }

    public boolean isImmured(Pair<Integer> pair) {
        return isMagicWall(get(pair.a - 1, pair.b).id) && isMagicWall(get(pair.a, pair.b).id) && isMagicWall(get(pair.a + 1, pair.b).id);
    }

    private void separateDraw(SpriteBatch batch) {
        if (level > 25) {
            first.draw(batch);
            second.draw(batch);
            exit.draw(batch);
        }
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void resetLoading() {
        isLoaded = false;
    }

    public boolean save(boolean all) {
        for (int i = 1; i < 65; i++)
            if (Gdx.files.local("room_" + i + ".data").exists())
                Gdx.files.local("room_" + i + ".data").delete();
        if (all) {
            ByteArray byteArray = new ByteArray();
            for (int b = 0; b < 22; b++) {
                for (int a = 0; a < 32; a++) {
                    units[a][b].toByteArray(byteArray);
                }
            }
            FileHandle handle = Gdx.files.local("room_" + level + ".data");
            handle.writeBytes(byteArray.toArray(), false);
            byteArray.clear();
            game.prefs().putInteger(Base64Coder.encodeString("room_goldWithKey"), goldWithKey);
            game.prefs().putInteger(Base64Coder.encodeString("room_goldAmount"), goldAmount);
            game.prefs().putInteger(Base64Coder.encodeString("room_goldCollected"), goldCollected);
        }
        game.prefs().putInteger(Base64Coder.encodeString("room_level"), level);
        game.prefs().flush();
        return all;
    }

    private boolean load() {
        FileHandle handle = Gdx.files.local("room_" + level + ".data");
        boolean load = false;
        if (handle.exists()) {
            byte[] bytes = new byte[(int) handle.length()];
            handle.readBytes(bytes, 0, bytes.length);
            int offset = 0;
            for (int b = 0; b < 22; b++) {
                for (int a = 0; a < 32; a++) {
                    offset += units[a][b].fromBytes(bytes, offset);
                }
            }
            handle.delete();
            goldWithKey = game.prefs().getInteger(Base64Coder.encodeString("room_goldWithKey"), 0);
            goldAmount = game.prefs().getInteger(Base64Coder.encodeString("room_goldAmount"), 0);
            goldCollected = game.prefs().getInteger(Base64Coder.encodeString("room_goldCollected"), 0);
            load = game.prefs().getInteger(Base64Coder.encodeString("room_level"), 0) > 0;
            delete();
        }
        return load;
    }

    private void delete() {
        game.prefs().remove(Base64Coder.encodeString("room_goldWithKey"));
        game.prefs().remove(Base64Coder.encodeString("room_goldAmount"));
        game.prefs().remove(Base64Coder.encodeString("room_goldCollected"));
        game.prefs().remove(Base64Coder.encodeString("room_level"));
    }

    // TODO separate region for part II
    class SeparateRegion {
        TextureRegion region;
        Pair<Integer> pair;
        SeparateRegion(TextureRegion region) {
            this.region = region;
        }
        void setPair(Pair<Integer> pair) {
            this.pair = pair;
        }
        void draw(SpriteBatch batch) {
            batch.draw(region, getX() + pair.a * 32, getTop() - pair.b * 22);
        }
    }

    public interface RoomListener {
        void onLevelComplete(int nextLevel, int bonus, boolean one, boolean all);
        void onHaveGold(int value, boolean goldenKey);
        void onOpenDoor();
        void onFirstReady();
        void onReady(int level);
    }
}
