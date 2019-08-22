package com.second_encounter.alexandr.klad_se.lib;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

import java.nio.ByteBuffer;

public class Screenshot {

    private static int counter = 27;

    public static void saveScreenshot(int x, int y, int width, int height, boolean yDown){
        try{
            FileHandle fileHandle;
            do {
                fileHandle = new FileHandle("e:mini_" + counter++ + "_.png");
            } while (fileHandle.exists());
            Pixmap pixmap = getScreenshot(x, y, width, height, yDown);
            PixmapIO.writePNG(fileHandle, pixmap);
            pixmap.dispose();
        } catch (Exception ignored) {
        }
    }

    private static Pixmap getScreenshot(int x, int y, int w, int h, boolean yDown){
        final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);
        if (yDown) {
            ByteBuffer pixels = pixmap.getPixels();
            int numBytes = w * h * 4;
            byte[] lines = new byte[numBytes];
            int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
            pixels.clear();
        }
        return pixmap;
    }
}