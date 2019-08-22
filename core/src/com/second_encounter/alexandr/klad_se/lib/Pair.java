package com.second_encounter.alexandr.klad_se.lib;

public class Pair<AType> {

    public AType a, b;

    public Pair(AType a, AType b) {
        this.a = a;
        this.b = b;
    }

    public void set(AType a, AType b) {
        this.a = a;
        this.b = b;
    }
}
