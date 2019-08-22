package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.second_encounter.alexandr.klad_se.GS;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.Tools;

public class GameTouchpad {
    
    private Maze game;
    private GameTouchpadListener listener;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Vector2[] vector2s = new Vector2[GS.max_touch];
    private Rectangle[] rectangles = new Rectangle[6];
    private TextureRegion[] regions = new TextureRegion[6];
    private int index, touchX, touchY, sizeIndex = 0;
    private float areaWidth, areaHeight;
    private boolean isArrangement = false, isTouched = false, debugDraw = false;
    private final float[] dimensions = { 96, 128, 160 };
    private final float pad = 8;
    private final int[] buttons = {
            Control.left,
            Control.right,
            Control.up,
            Control.down,
            Control.shootLeft,
            Control.shootRight
    };

    public GameTouchpad(Maze game, GameTouchpadListener listener) {
        this.game = game;
        this.listener = listener;
        for (int i = 0; i < vector2s.length; i++)
            vector2s[i] = new Vector2(0, 0);
        for (int i = 0; i < rectangles.length; i++)
            rectangles[i] = new Rectangle(0, 0, dimensions[sizeIndex], i < 4 ? dimensions[sizeIndex] : Tools.half(dimensions[sizeIndex]));
        String[] buttonNameList = { "left", "right", "up", "down", "shoot", "shoot" };
        for (int i = 0; i < regions.length; i++)
            regions[i] = game.commonAtlas.findRegion(buttonNameList[i]);
        shapeRenderer.setColor(Color.GRAY);

    }

    public void debugDraw() {
        if (debugDraw) {
            shapeRenderer.setProjectionMatrix(game.extendViewport.camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (Rectangle rectangle : rectangles)
                shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            shapeRenderer.end();
        }
    }

    public void draw(boolean useGamepad) {
        if (useGamepad)
            return;
        game.batch.setColor(1, 1, 1, 0.7f);
        for (int i = 0; i < regions.length; i++)
            game.batch.draw(regions[i], rectangles[i].x, rectangles[i].y, rectangles[i].width, rectangles[i].height);
        game.batch.setColor(Color.WHITE);
    }

    public void changeSize(int newSizeIndex) {
        sizeIndex = newSizeIndex;
        resize();
    }

    public void resize() {
        areaWidth = game.extendViewport.getWorldWidth();
        areaHeight = game.extendViewport.getWorldHeight();
        rectangles[0].set(pad, pad, dimensions[sizeIndex], dimensions[sizeIndex]);
        rectangles[1].set(dimensions[sizeIndex] + pad * 2, pad, dimensions[sizeIndex], dimensions[sizeIndex]);
        rectangles[2].set(areaWidth - dimensions[sizeIndex] - pad, pad + dimensions[sizeIndex] + pad, dimensions[sizeIndex], dimensions[sizeIndex]);
        rectangles[3].set(areaWidth - dimensions[sizeIndex] - pad, pad, dimensions[sizeIndex], dimensions[sizeIndex]);
        rectangles[4].set(pad, dimensions[sizeIndex] + pad * 2, dimensions[sizeIndex], Tools.half(dimensions[sizeIndex]));
        rectangles[5].set(areaWidth - dimensions[sizeIndex] - pad, dimensions[sizeIndex] * 2 + pad * 3, dimensions[sizeIndex], Tools.half(dimensions[sizeIndex]));
    }

    public boolean onTouchDown(float screenX, float screenY, int pointer) {
        if (isArrangement && pointer > 0)
            return false;
        if (pointer >= GS.max_touch)
            return false;
        vector2s[pointer].set(game.extendViewport.unproject(vector2s[pointer].set(screenX, screenY)));
        for (int i = 0; i < rectangles.length; i++) {
            if (rectangles[i].contains(vector2s[pointer])) {
                if (isArrangement) {
                    touchX = (int) (vector2s[pointer].x / pad);
                    touchY = (int) (vector2s[pointer].y / pad);
                    index = i;
                }
                else
                    listener.onTouchDown(buttons[i]);
                isTouched = true;
                break;
            }
        }
        return true;
    }

    public boolean onTouchDragged(float screenX, float screenY, int pointer) {
        if (pointer > 0)
            return false;
        if (isTouched && isArrangement) {
            vector2s[pointer].set(game.extendViewport.unproject(vector2s[0].set(screenX, screenY)));
            int x = (int) (vector2s[pointer].x / pad);
            if (touchX != x) {
                float oldX = rectangles[index].x;
                rectangles[index].x += pad * (x - touchX);
                for (int i = 0; i < rectangles.length; i++) {
                    if (index != i && rectangles[index].overlaps(rectangles[i])) {
                        rectangles[index].x = oldX;
                        return false;
                    }
                }
                if (rectangles[index].x < pad || rectangles[index].x > areaWidth - rectangles[index].width - pad)
                    rectangles[index].x = oldX;
                touchX = x;
            }
            int y = (int) (vector2s[pointer].y / pad);
            if (touchY != y) {
                float oldY = rectangles[index].y;
                rectangles[index].y += pad * (y - touchY);
                for (int i = 0; i < rectangles.length; i++) {
                    if (index != i && rectangles[index].overlaps(rectangles[i])) {
                        rectangles[index].y = oldY;
                        return false;
                    }
                }
                if (rectangles[index].y < pad || rectangles[index].y > areaHeight - rectangles[index].height - pad)
                    rectangles[index].y = oldY;
                touchY = y;
            }
        }
        return false;
    }

    public boolean onTouchUp(int pointer) {
        if (isArrangement && pointer > 0)
            return false;
        if (pointer >= GS.max_touch)
            return false;
        for (int i = 0; i < rectangles.length; i++) {
            if (rectangles[i].contains(vector2s[pointer]))
                if (!isArrangement)
                    listener.onTouchUp(buttons[i]);
            isTouched = false;
        }
        return true;
    }

    public void setArrangement(boolean arrangement) {
        isArrangement = arrangement;
    }

    public boolean getArrangement() {
        return isArrangement;
    }

    public void setDebugDraw(boolean debug) {
        debugDraw = debug;
    }
    
    public void save() {
        game.prefs().putInteger("touchpad_areaWidth", (int) areaWidth);
        game.prefs().putInteger("touchpad_areaHeight", (int) areaHeight);
        game.prefs().putInteger("touchpad_sizeIndex", sizeIndex);
        for (int i = 0; i < rectangles.length; i++) {
            game.prefs().putInteger("touchpad_x_" + i, (int) rectangles[i].x);
            game.prefs().putInteger("touchpad_y_" + i, (int) rectangles[i].y);
            game.prefs().putInteger("touchpad_width_" + i, (int) rectangles[i].width);
            game.prefs().putInteger("touchpad_height_" + i, (int) rectangles[i].height);
        }
        game.prefs().flush();
    }

    private boolean load() {
        boolean change = game.prefs().getInteger("touchpad_areaWidth", 0) == (int) areaWidth && game.prefs().getInteger("touchpad_areaHeight", 0) == (int) areaHeight;
        if (change) {
            sizeIndex = game.prefs().getInteger("touchpad_sizeIndex", sizeIndex);
            for (int i = 0; i < rectangles.length; i++) {
                rectangles[i].x = game.prefs().getInteger("touchpad_x_" + i, (int) rectangles[i].x);
                rectangles[i].y = game.prefs().getInteger("touchpad_y_" + i, (int) rectangles[i].y);
                rectangles[i].width = game.prefs().getInteger("touchpad_width_" + i, (int) rectangles[i].width);
                rectangles[i].height = game.prefs().getInteger("touchpad_height_" + i, (int) rectangles[i].height);
            }
        }
        return change;
    }

    public interface GameTouchpadListener {
        void onTouchDown(int keyCode);
        void onTouchUp(int keyCode);
    }
}
