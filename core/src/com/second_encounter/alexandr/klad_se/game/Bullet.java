package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.lib.Pair;

public class Bullet {

    public static final int dNo    = 0x00;
    public static final int dLeft  = 0x01;
    public static final int dRight = 0x02;

    private Maze game;
    private Room room;
    private Character character;
    private TextureRegion region;
    private Pair<Integer> pair = new Pair<>(0, 0);
    private int direction = dNo;
    private float x, y, speed;

    public Bullet(Maze game, Room room, Character character) {
        this.game = game;
        this.room = room;
        this.character = character;
        region = game.commonAtlas.findRegion("bullet");
        speed = game.config.getBulletSpeed();
    }

    public void start(int direction) {
        if (this.direction == dNo) {
            this.direction = direction;
            x = character.getX();
            y = character.getY();
            game.sound.play("shoot");
        }
    }

    public void draw(float delta) {
        switch (direction) {
            case dLeft:
                x -= speed * delta;
                break;
            case dRight:
                x += speed * delta;
                break;
            default:
                return;
        }
        pair.set((int) (x + 16) / 32, (int) (y + 11) / 22);
        int unit = room.get(pair.a, pair.b).id;
        if (unit == 2 || unit == 3 || room.isMagicWall(unit) || unit == 9) {
            if (room.isMagicWall(unit)) {
                int value = drill(unit);
                if (value == 0)
                    room.get(pair.a, pair.b).wallTime = game.config.getWallTime();
                room.get(pair.a, pair.b).id = value;
                game.sound.play("crash");
            }
            direction = dNo;
        }
        game.batch.draw(region, room.getX() + x, room.getTop() - y);
    }

    public void reset() {
        direction = dNo;
    }

    private int drill(int unit) {
        switch (unit) {
            case 8:
                return direction == dLeft ? 12 : 15;
            case 12:
            case 15:
                return direction == dLeft ? 13 : 16;
            case 13:
            case 16:
                return direction == dLeft ? 14 : 17;
        }
        return 0;
    }
}
