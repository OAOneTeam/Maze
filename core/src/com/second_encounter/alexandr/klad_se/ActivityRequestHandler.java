package com.second_encounter.alexandr.klad_se;

import java.util.ArrayList;

public interface ActivityRequestHandler {

    //Purchase
    void setPurchaseListener(PurchaseListener listener);
    void initiatePurchaseFlow(final String skuId);

    // Purchase listener
    interface PurchaseListener {
        void onPurchasesUpdated(ArrayList<String> skus);
    }

    // AD
    void loadAd(LoadAdListener listener);
    void showAd(ShowAdListener listener);
    boolean adIsLoaded();

    // AD listener
    interface LoadAdListener {
        void onRewardedVideoAdLoaded();
        void onRewardedVideoAdFailedToLoad();
    }

    interface ShowAdListener {
        void onRewardedVideoAdClosed();
        void onRewarded();
    }

    // Answer events
    void eventFirstStart(boolean first);
    void eventLevelStart(int level);
    void eventLevelEnd(int level, boolean success);
    void eventViewingAds(boolean canceled);
    void eventAdLoading(boolean canceled);
    void eventAdViewed();

    // Other
    String versionName();
    boolean isBuildConfig();
}
