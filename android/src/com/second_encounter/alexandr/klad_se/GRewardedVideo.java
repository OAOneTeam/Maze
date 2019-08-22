package com.second_encounter.alexandr.klad_se;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.google.ads.mediation.adcolony.AdColonyMediationAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.jirbo.adcolony.AdColonyAdapter;
import com.jirbo.adcolony.AdColonyBundleBuilder;

public class GRewardedVideo implements RewardedVideoAdListener, AdColonyRewardListener {

    private static final String TAG = "RewardedVideo";
    private static final boolean interstitial = false;

    private Context context;
    private RewardedVideoAd rewardedVideoAd;
    private InterstitialAd interstitialAd;

    private ActivityRequestHandler.LoadAdListener loadAdListener;
    private ActivityRequestHandler.ShowAdListener showAdListener;

    public GRewardedVideo(final Activity activity) {
        context = activity.getApplicationContext();

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        AdColonyAppOptions adColonyOptions = AdColonyMediationAdapter.getAppOptions()
                .setGDPRConsentString("1")
                .setGDPRRequired(true);
        AdColony.configure(activity, adColonyOptions, GS.adColonyAppId, GS.adColonyZoneId);
        AdColony.setRewardListener(this);

        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(GS.appAdRewardUnitId);
        interstitialAd.setRewardedVideoAdListener(this);
    }

    public void onResume() {
        if (rewardedVideoAd != null)
            rewardedVideoAd.resume(context);
    }

    public void onPause() {
        if (rewardedVideoAd != null)
            rewardedVideoAd.pause(context);
    }

    public void onDestroy() {
        if (rewardedVideoAd != null)
            rewardedVideoAd.destroy(context);
    }

    public void loadAd(ActivityRequestHandler.LoadAdListener loadAdListener) {
        this.loadAdListener = loadAdListener;
        AdRequest request;
        if (BuildConfig.DEBUG) {
            // debug
            if (interstitial) {
                AdColonyBundleBuilder.setShowPrePopup(true);
                AdColonyBundleBuilder.setShowPostPopup(true);
                request = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build())
                        .build();
            }
            else {
                request = new AdRequest.Builder()
                        .addTestDevice("875E070523D7D6312C296A3217E53310") // Sony Tablet Z
                        .addTestDevice("7BDE04C7C245259DFB6CDBA465799E17") // Huawei P20
                        .addTestDevice("3F43DD970076037F45BBB93783CA5B5F") // Huawei Mate 10 Lite
                        .addTestDevice("077174E7576F6B4481E8CFADDB622062") // Huawei P10 Lite
                        .build();
            }
        }
        else {
            // release
            if (interstitial)
                request = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build())
                        .build();
            else
                request = new AdRequest.Builder().build();
        }
        //
        if (rewardedVideoAd.isLoaded())
            onRewardedVideoAdLoaded();
        else {
            if (interstitial)
                interstitialAd.loadAd(request);
            else
                rewardedVideoAd.loadAd(GS.appAdRewardUnitId, request);
        }
    }

    public void showAd(ActivityRequestHandler.ShowAdListener showAdListener) {
        this.showAdListener = showAdListener;
        if (rewardedVideoAd.isLoaded()) {
            if (interstitial)
                interstitialAd.show();
            else
                rewardedVideoAd.show();
        }
        else
            onRewardedVideoAdFailedToLoad(0);
    }

    public boolean isLoaded() {
        return rewardedVideoAd.isLoaded();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        if (loadAdListener != null)
            loadAdListener.onRewardedVideoAdLoaded();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdClosed");
        if (showAdListener != null)
            showAdListener.onRewardedVideoAdClosed();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d(TAG, "onRewarded ");
        if (showAdListener != null)
            showAdListener.onRewarded();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d(TAG, "onRewardedVideoAdFailedToLoad ");
        if (loadAdListener != null)
            loadAdListener.onRewardedVideoAdFailedToLoad();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d(TAG, "onRewardedVideoCompleted");
    }

    @Override
    public void onReward(AdColonyReward adColonyReward) {

    }
}
