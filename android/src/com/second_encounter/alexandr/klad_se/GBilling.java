package com.second_encounter.alexandr.klad_se;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GBilling implements PurchasesUpdatedListener {

    public static final int BILLING_MANAGER_NOT_INITIALIZED  = -1;

    private static final String TAG = "Billing";
    private BillingClient billingClient;
    private boolean isServiceConnected;
    private final BillingUpdatesListener billingUpdatesListener;
    private final Activity activity;
    private final List<Purchase> purchases = new ArrayList<>();
    private Set<String> tokensToBeConsumed;
    private int billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED;

    private static final String BASE_64_ENCODED_PUBLIC_KEY = GS.base64EncodedPublicKey;

    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();
        void onConsumeFinished(String token, @BillingClient.BillingResponse int result);
        void onPurchasesUpdated(List<Purchase> purchases);
        void onClose(int result);
    }

    public interface ServiceConnectedListener {
        void onServiceConnected(@BillingClient.BillingResponse int resultCode);
    }

    public GBilling(Activity activity, final BillingUpdatesListener updatesListener) {
        Log.d(TAG, "Creating Billing client.");
        this.activity = activity;
        billingUpdatesListener = updatesListener;
        billingClient = BillingClient.newBuilder(this.activity).setListener(this).build();
        Log.d(TAG, "Starting setup.");
        startServiceConnection(new Runnable() {
            @Override
            public void run() {
                billingUpdatesListener.onBillingClientSetupFinished();
                Log.d(TAG, "Setup successful. Querying inventory.");
                queryPurchases();
            }
        });
    }

    @Override
    public void onPurchasesUpdated(int resultCode, List<Purchase> purchases) {
        if (resultCode == BillingClient.BillingResponse.OK) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
            billingUpdatesListener.onPurchasesUpdated(this.purchases);
        }
        else {
            if (resultCode == BillingClient.BillingResponse.USER_CANCELED)
                Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
            else
                Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + resultCode);
        }
        billingUpdatesListener.onClose(resultCode);

    }

    public void initiatePurchaseFlow(final String skuId, final @BillingClient.SkuType String billingType) {
        initiatePurchaseFlow(skuId, null, billingType);
    }

    public void initiatePurchaseFlow(final String skuId, final ArrayList<String> oldSkus, final @BillingClient.SkuType String billingType) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSkus != null));
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                        .setSku(skuId)
                        .setType(billingType)
                        .setOldSkus(oldSkus)
                        .build();
                billingClient.launchBillingFlow(activity, purchaseParams);
            }
        };
        executeServiceRequest(purchaseFlowRequest);
    }

    public void onDestroy() {
        Log.d(TAG, "Destroying the manager.");
        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
            billingClient = null;
        }
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType, final List<String> skuList, final SkuDetailsResponseListener listener) {
        Runnable queryRequest = new Runnable() {
            @Override
            public void run() {
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(itemType);
                billingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                            }
                        });
            }
        };
        executeServiceRequest(queryRequest);
    }

    public void consumeAsync(final String purchaseToken) {
        if (tokensToBeConsumed == null) {
            tokensToBeConsumed = new HashSet<>();
        }
        else
            if (tokensToBeConsumed.contains(purchaseToken)) {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
            return;
        }
        tokensToBeConsumed.add(purchaseToken);
        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String purchaseToken) {
                billingUpdatesListener.onConsumeFinished(purchaseToken, responseCode);
            }
        };

        Runnable consumeRequest = new Runnable() {
            @Override
            public void run() {
                billingClient.consumeAsync(purchaseToken, onConsumeListener);
            }
        };
        executeServiceRequest(consumeRequest);
    }

    public int getBillingClientResponseCode() {
        return billingClientResponseCode;
    }

    private void handlePurchase(Purchase purchase) {
        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
            Log.i(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
            return;
        }
        Log.d(TAG, "Got a verified purchase: " + purchase);
        purchases.add(purchase);
    }

    private void onQueryPurchasesFinished(Purchase.PurchasesResult result) {
        if (billingClient == null || result.getResponseCode() != BillingClient.BillingResponse.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.getResponseCode() + ") was bad - quitting");
            return;
        }
        Log.d(TAG, "Query inventory was successful.");
        purchases.clear();
        onPurchasesUpdated(BillingClient.BillingResponse.OK, result.getPurchasesList());
    }

    public boolean areSubscriptionsSupported() {
        int responseCode = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        if (responseCode != BillingClient.BillingResponse.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: " + responseCode);
        }
        return responseCode == BillingClient.BillingResponse.OK;
    }

    public void queryPurchases() {
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                Log.i(TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time) + "ms");
                if (areSubscriptionsSupported()) {
                    Purchase.PurchasesResult subscriptionResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
                    Log.i(TAG, "Querying purchases and subscriptions elapsed time: " + (System.currentTimeMillis() - time) + "ms");
                    Log.i(TAG, "Querying subscriptions result code: " + subscriptionResult.getResponseCode() + " res: " + subscriptionResult.getPurchasesList().size());
                    if (subscriptionResult.getResponseCode() == BillingClient.BillingResponse.OK) {
                        purchasesResult.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
                    }
                    else
                        Log.e(TAG, "Got an error response trying to query subscription purchases");
                }
                else {
                    if (purchasesResult.getResponseCode() == BillingClient.BillingResponse.OK)
                        Log.i(TAG, "Skipped subscription purchases query since they are not supported");
                    else
                        Log.w(TAG, "queryPurchases() got an error response code: " + purchasesResult.getResponseCode());
                }
                onQueryPurchasesFinished(purchasesResult);
            }
        };
        executeServiceRequest(queryToExecute);
    }

    public void startServiceConnection(final Runnable executeOnSuccess) {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                Log.d(TAG, "Setup finished. Response code: " + billingResponseCode);
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    isServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }
                billingClientResponseCode = billingResponseCode;
            }

            @Override
            public void onBillingServiceDisconnected() {
                isServiceConnected = false;
            }
        });
    }

    private void executeServiceRequest(Runnable runnable) {
        if (isServiceConnected) {
            runnable.run();
        }
        else {
            startServiceConnection(runnable);
        }
    }

    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
        } catch (IOException e) {
            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }
}
