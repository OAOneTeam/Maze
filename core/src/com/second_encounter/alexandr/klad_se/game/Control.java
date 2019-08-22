package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.IntArray;

public class Control {

    public static final int idMotion   = 0;
    public static final int idShoot    = 1;

    public static final int left       = Input.Keys.LEFT;
    public static final int right      = Input.Keys.RIGHT;
    public static final int up         = Input.Keys.UP;
    public static final int down       = Input.Keys.DOWN;
    public static final int shootLeft  = Input.Keys.Z;
    public static final int shootRight = Input.Keys.X;

    private IntArray[] events = {
            new IntArray(),
            new IntArray()
    };

    public Control() {
    }

    public void add(int value) {
        int index = (value == shootLeft || value == shootRight) ? idShoot : idMotion;
        if (events[index].isEmpty())
            events[index].add(value);
        else
            if (events[index].first() != value)
                events[index].insert(0, value);
    }

    public void delete(int value) {
        events[(value == shootLeft || value == shootRight) ? idShoot : idMotion].removeValue(value);
    }

    public void clear(int id) {
        events[id].clear();
    }

    public int get(int id) {
        if (events[id].isEmpty())
            return 0;
        else
            return events[id].first();
    }
}
