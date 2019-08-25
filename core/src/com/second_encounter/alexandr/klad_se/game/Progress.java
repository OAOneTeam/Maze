package com.second_encounter.alexandr.klad_se.game;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.second_encounter.alexandr.klad_se.Maze;
import com.second_encounter.alexandr.klad_se.NumericText;
import com.second_encounter.alexandr.klad_se.Tools;

public class Progress extends Table {

    private Maze game;
    private Room room;
    private NumericText maze, gold, live;
    private int m, g, l;

    public Progress(Maze game, Room room, int m, int g, int l) {
        this.game = game;
        this.room = room;
        this.m = m;
        this.g = g;
        this.l = l;
        setScale(0.7f);
        setTransform(true);
        Image imageMaze = new Image(game.commonAtlas.findRegion("maze_" + game.config.getLanguage()));
        Image imageGold = new Image(game.commonAtlas.findRegion("gold_" + game.config.getLanguage()));
        Image imageLive = new Image(game.commonAtlas.findRegion("live_" + game.config.getLanguage()));
        maze = new NumericText(game, m);
        gold = new NumericText(game, g);
        live = new NumericText(game, l);
        add(imageMaze);
        add(maze).width(64);
        add(imageGold).padLeft(160);
        add(gold).width(128);
        add(imageLive).padLeft(128);
        add(live).width(64);
        pack();
    }

    public void show() {
        game.stage.addActor(this);
    }

    public void hide() {
        remove();
    }

    public void setMaze(int value) {
        maze.set(value);
        if (maze.getActions().size == 0 && m != value)
            maze.addAction(
                    Actions.sequence(
                            Actions.scaleTo(1.5f, 1.5f, 0.2f, Interpolation.bounceOut),
                            Actions.scaleTo(1f, 1f, 0.2f, Interpolation.fade)
                    )
            );
        m = value;
    }

    public void setGold(int value) {
        gold.set(value);
        if (gold.getActions().size == 0 && g != value)
            gold.addAction(
                    Actions.sequence(
                            Actions.scaleTo(1.5f, 1.5f, 0.2f, Interpolation.bounceOut),
                            Actions.scaleTo(1f, 1f, 0.2f, Interpolation.fade)
                    )
            );
        g = value;
    }

    public void setLive(int value) {
        live.set(value);
        if (live.getActions().size == 0 && l != value)
            live.addAction(
                    Actions.sequence(
                            Actions.scaleTo(1.5f, 1.5f, 0.2f, Interpolation.bounceOut),
                            Actions.scaleTo(1f, 1f, 0.2f, Interpolation.fade)
                    )
            );
        l = value;
    }

    public void resize() {
        setPosition(Tools.half(game.stage.getWidth() - getWidth() * getScaleX()), room.getY() - (game.config.drawFrame ? 88 : 68));
    }
}
