package com.second_encounter.alexandr.klad_se.lib;

public class DeltaTimer {

    private float millis, time;

    public DeltaTimer(float millis) {
        this.millis = millis;
        reset();
    }

    public void set(float millis) {
        this.millis = millis;
    }

    public void reset() {
        time = 0;
    }

    public boolean action(float delta) {
        time += delta;
        boolean result = false;
        if (time > millis) {
            time = time - millis;
            result = true;
        }
        return result;
    }
}
