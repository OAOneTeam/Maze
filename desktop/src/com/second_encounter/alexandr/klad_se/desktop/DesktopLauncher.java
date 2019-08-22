package com.second_encounter.alexandr.klad_se.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.second_encounter.alexandr.klad_se.ActivityRequestHandler;
import com.second_encounter.alexandr.klad_se.GS;
import com.second_encounter.alexandr.klad_se.Maze;

import java.util.ArrayList;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GS.WIDTH;
		config.height = GS.HEIGHT;
        config.fullscreen = false;
        final ActivityRequestHandler.PurchaseListener[] purchaseListener = new ActivityRequestHandler.PurchaseListener[1];
        final ArrayList<String> skus = new ArrayList<String>();
		new LwjglApplication(new Maze(new ActivityRequestHandler() {
            @Override
            public void setPurchaseListener(PurchaseListener listener) {
                purchaseListener[0] = listener;
            }

            @Override
            public void initiatePurchaseFlow(String skuId) {
                skus.clear();
                skus.add(skuId);
                purchaseListener[0].onPurchasesUpdated(skus);
            }

            @Override
            public void loadAd(LoadAdListener listener) {
                listener.onRewardedVideoAdLoaded();
            }

            @Override
            public void showAd(ShowAdListener listener) {
                listener.onRewarded();
                listener.onRewardedVideoAdClosed();
            }

            @Override
            public boolean adIsLoaded() {
                return true;
            }

            @Override
            public void eventFirstStart(boolean first) {
                System.out.println("First start " + first);
            }

            @Override
            public void eventLevelStart(int level) {
                System.out.println("Level start #" + level);
            }

            @Override
            public void eventLevelEnd(int level, boolean success) {
                System.out.println("Level end #" + level + " success " + success);
            }

            @Override
            public void eventViewingAds(boolean canceled) {
                System.out.println("Viewing ads " + canceled);
            }

            @Override
            public void eventAdLoading(boolean canceled) {
                System.out.println("Ad loading " + canceled);
            }

            @Override
            public void eventAdViewed() {
                System.out.println("Ad viewed ADMob");
            }

            @Override
            public String versionName() {
                return "Desktop";
            }

            @Override
            public boolean isBuildConfig() {
                return true;
            }
        }), config);
	}
}
