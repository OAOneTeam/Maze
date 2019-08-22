package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.utils.ByteArray;
import com.second_encounter.alexandr.klad_se.Tools;

public class Unit {

    public static final Unit nullUnit = new Unit(0, 0, 1, 9);

    public int part, id, goldNumber, enemyId, number;
    public float x, y, wallTime;

    public Unit(float x, float y, int part, int id) {
        this.x    = x;
        this.y    = y;
        this.part = part;
        this.id   = id;
        reset();
    }

    public void reset() {
        goldNumber = 0;
        enemyId    = 0;
        wallTime   = 0;
        number     = 0;
    }

    public void toByteArray(ByteArray byteArray) {
        byteArray.addAll(Tools.intToBytes(part));
        byteArray.addAll(Tools.intToBytes(id));
        byteArray.addAll(Tools.intToBytes(goldNumber));
        byteArray.addAll(Tools.intToBytes(enemyId));
        byteArray.addAll(Tools.intToBytes(number));
        byteArray.addAll(Tools.floatToBytes(wallTime));
    }

    public int fromBytes(byte[] bytes, int offset) {
        int index = offset;
        part = Tools.bytesToInt(bytes, index); index += Tools.integerSize;
        id = Tools.bytesToInt(bytes, index); index += Tools.integerSize;
        goldNumber = Tools.bytesToInt(bytes, index); index += Tools.integerSize;
        enemyId = Tools.bytesToInt(bytes, index); index += Tools.integerSize;
        number = Tools.bytesToInt(bytes, index); index += Tools.integerSize;
        wallTime = Tools.bytesToFloat(bytes, index); index += Tools.floatSize;
        return index - offset;
    }
}
