package com.second_encounter.alexandr.klad_se;

import java.nio.ByteBuffer;

public class Tools {

    public static final int integerSize = Integer.SIZE / Byte.SIZE;
    public static final int floatSize   = Float.SIZE   / Byte.SIZE;

    public static float half(float value) {
        return value * 0.5f;
    }

    public static byte[] intToBytes(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(integerSize);
        return buffer.putInt(value).array();
    }

    public static byte[] floatToBytes(float value) {
        ByteBuffer buffer = ByteBuffer.allocate(floatSize);
        return buffer.putFloat(value).array();
    }

    public static int bytesToInt(byte[] bytes, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, integerSize);
        return buffer.getInt();
    }

    public static float bytesToFloat(byte[] bytes, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, floatSize);
        return buffer.getFloat();
    }
}
