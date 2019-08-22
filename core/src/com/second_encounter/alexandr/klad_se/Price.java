package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.second_encounter.alexandr.klad_se.lib.MWindow;

import java.util.Locale;

public class Price extends MWindow {

    public static final int price099 = 0x099;
    public static final int price100 = 0x100;
    public static final int price130 = 0x130;
    public static final int price160 = 0x160;
    public static final int price250 = 0x250;
    public static final int price400 = 0x400;

    private Table table = new Table();
    private int[] price = { price099, price100, price130, price160, price250, price400 };
    private ImageButton[] buttons = new ImageButton[price.length];
    private Label label;
    private int currentPrice;

    public Price(Maze game, final MWindowListener listener) {
        super(game, listener);
        for (int i = 0; i < price.length; i++) {
            buttons[i] = new ImageButton(game.skinCommon, priceToString(i));
            buttons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isShown)
                        listener.onApply();
                }
            });
        }
        label = new Label("", game.skinCommon);
        label.setFontScale(0.45f);
    }

    public void show(int price) {
        build();
        currentPrice = price;
        int index = 0;
        for (int i = 0; i < this.price.length; i++)
            if (this.price[i] == price) {
                index = i;
                break;
            }
        label.setText("[BLACK]" + game.bundle.get(priceToString(index)).replaceFirst("/", "[FIREBRICK]").replaceFirst("/", "[BLACK]"));
        table.clearChildren();
        table.add(buttons[index]).pad(8).row();
        table.add(label).pad(8);
        add(table).expand().top();
        super.show();
    }

    public int getPrice() {
        return currentPrice;
    }

    private String priceToString(int index) {
        return String.format(Locale.getDefault(), "price_%3s", Integer.toHexString(price[index])).replace(" ", "0");
    }
}
