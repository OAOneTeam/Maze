package com.second_encounter.alexandr.klad_se;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.fabric.sdk.android.Fabric;

public class AndroidLauncher extends AndroidApplication {

	private ActivityRequestHandler.PurchaseListener purchaseListener;
	private GRewardedVideo rewardedVideo;
	private GBilling billing;
	private ArrayList<String> skus = new ArrayList<>();
	private boolean adIsLoaded;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		rewardedVideo = new GRewardedVideo(this);
		billing = new GBilling(this, new GBilling.BillingUpdatesListener() {
			@Override
			public void onBillingClientSetupFinished() {
				// TODO billing setup finished
			}

			@Override
			public void onConsumeFinished(String token, int result) {
                // TODO consume finished
			}

			@Override
			public void onPurchasesUpdated(List<Purchase> purchases) {
				skus.clear();
				for (Purchase purchase : purchases) {
					switch (purchase.getSku()) {
                        case GS.skuJump:
						case GS.sku5Lives:
						case GS.sku10Lives:
						case GS.sku20Lives:
							billing.consumeAsync(purchase.getPurchaseToken());
							break;
						case GS.skuSecondChapter:
                            if (BuildConfig.DEBUG && GS.consume)
                                billing.consumeAsync(purchase.getPurchaseToken());
                            else
                                GS.SC = GS.skuSecondChapter;
                            break;
						case GS.skuAllFeatures:
							if (BuildConfig.DEBUG && GS.consume)
								billing.consumeAsync(purchase.getPurchaseToken());
							else
                                GS.AF = GS.skuAllFeatures;
							break;
					}
					skus.add(purchase.getSku());
				}
				if (purchaseListener != null)
					purchaseListener.onPurchasesUpdated(skus);
			}

			@Override
			public void onClose(int result) {
				if (purchaseListener != null)
					purchaseListener.onPurchasesUpdated(null);
			}
		});
		//
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = 0;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;
		initialize(new Maze(new ActivityRequestHandler() {
			@Override
			public void setPurchaseListener(final PurchaseListener listener) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						purchaseListener = listener;
					}
				});
			}

			@Override
			public void initiatePurchaseFlow(final String skuId) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						billing.initiatePurchaseFlow(skuId, BillingClient.SkuType.INAPP);
					}
				});
			}

			@Override
			public void loadAd(final LoadAdListener loadAdListener) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						rewardedVideo.loadAd(loadAdListener);
					}
				});
			}

			@Override
			public void showAd(final ShowAdListener showAdListener) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						rewardedVideo.showAd(showAdListener);
					}
				});
			}

            @Override
            public boolean adIsLoaded() {
                final CountDownLatch latch = new CountDownLatch(1);
                adIsLoaded = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adIsLoaded = rewardedVideo.isLoaded();
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                }
                catch (InterruptedException e) {
                    adIsLoaded = false;
                }
                return adIsLoaded;
            }

            @Override
            public void eventFirstStart(final boolean first) {
                if (BuildConfig.DEBUG) {
                    System.out.println("First start " + first);
                    if (!GS.fabricAnswers)
                        return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Answers.getInstance().logCustom(new CustomEvent("Start")
                                .putCustomAttribute("First", Boolean.toString(first))
                        );
                    }
                });
            }

            @Override
            public void eventLevelStart(final int level) {
			    if (BuildConfig.DEBUG) {
                    System.out.println("Level start #" + level);
                    if (!GS.fabricAnswers)
                        return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Answers.getInstance().logLevelStart(new LevelStartEvent()
                                .putLevelName("Level #" + level));
                    }
                });
            }

            @Override
            public void eventLevelEnd(int level, boolean success) {
                if (BuildConfig.DEBUG) {
                    System.out.println("Level end #" + level + " success " + success);
                    if (!GS.fabricAnswers)
                        return;
                }
                Answers.getInstance().logLevelEnd(new LevelEndEvent()
                        .putLevelName("Level #" + level)
                        .putSuccess(success));
            }

            @Override
            public void eventViewingAds(final boolean canceled) {
                if (BuildConfig.DEBUG) {
                    System.out.println("Viewing ads " + canceled);
                    if (!GS.fabricAnswers)
                        return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Answers.getInstance().logCustom(new CustomEvent("Ads")
                                .putCustomAttribute("View offer", Boolean.toString(canceled))
                        );
                    }
                });
            }

            @Override
            public void eventAdLoading(final boolean canceled) {
                if (BuildConfig.DEBUG) {
                    System.out.println("Ad loading " + canceled);
                    if (!GS.fabricAnswers)
                        return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Answers.getInstance().logCustom(new CustomEvent("Ads")
                                .putCustomAttribute("Ad loading", Boolean.toString(canceled))
                        );
                    }
                });
            }

            @Override
            public void eventAdViewed() {
                if (BuildConfig.DEBUG) {
                    System.out.println("Ad viewed ADMob");
                    if (!GS.fabricAnswers)
                        return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Answers.getInstance().logCustom(new CustomEvent("Ads")
                                .putCustomAttribute("Ad viewed", "ADMob")
                        );
                    }
                });
            }

            @Override
            public String versionName() {
                return BuildConfig.VERSION_NAME;
            }

            @Override
            public boolean isBuildConfig() {
                return BuildConfig.DEBUG;
            }
        }), config);
	}

	@Override
	protected void onResume() {
		super.onResume();
		rewardedVideo.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	@Override
	protected void onPause() {
		rewardedVideo.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		rewardedVideo.onDestroy();
		billing.onDestroy();
		super.onDestroy();
	}
}
