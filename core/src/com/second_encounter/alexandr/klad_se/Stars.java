package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public class Stars {

    private TextureRegion region;
    private Random random = new Random();
    private Star[] stars;
    private float x, y, width, height;

    public Stars(Maze game, int count) {
        region = game.commonAtlas.findRegion("point");
        stars = new Star[count];
    }

    public void draw(Batch batch, float delta) {
        for (Star star : stars) {
            star.update(width, height, delta);
            batch.draw(region, x + star.x, y + star.y, star.size, star.size);
        }
    }

    public void resize(float x, float y, float width, float height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        for (int i = 0; i < stars.length; i++)
            stars[i] = new Star(width, height);
    }

    class Star {
        float x, y, size;

        Star(float width, float height) {
            set(width, height, true);
        }

        void update(float width, float height, float delta) {
            x -= (size * 16f) * delta;
            if (x < 0f)
                set(width, height, false);
        }

        void set(float width, float height, boolean randomX) {
            x = randomX ? random.nextInt((int) width) : width;
            y = random.nextInt((int) height);
            size = 2 + random.nextInt(8);
        }
    }
}
