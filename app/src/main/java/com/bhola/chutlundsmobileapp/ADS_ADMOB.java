package com.bhola.chutlundsmobileapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;


public class ADS_ADMOB implements OnUserEarnedRewardListener {

    private RewardedInterstitialAd rewardedInterstitialAd;
    Context context;
    String AdUnit_ID;
    private final String TAG = "MainActivity";
    Activity activity = (Activity) context;

    public ADS_ADMOB(RewardedInterstitialAd rewardedInterstitialAd, Context context, String AdUnit_ID) {
        this.rewardedInterstitialAd = rewardedInterstitialAd;
        this.context=context;
        this.AdUnit_ID=AdUnit_ID;

    }

    public static void BannerAd(Context context, AdView mAdView) {

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                mAdView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

            }
        });


    }
    public void RewardedVideoAds() {

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete( InitializationStatus initializationStatus) {
                loadAd_rewardedIntertitialAds();
            }
        });
    }

    public void loadAd_rewardedIntertitialAds() {
        RewardedInterstitialAd.load(context, AdUnit_ID,
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        rewardedInterstitialAd.show(/* Activity */ activity,/*
    OnUserEarnedRewardListener */ (OnUserEarnedRewardListener) activity);

                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            /** Called when the ad failed to show full screen content. */
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.i(TAG, "onAdFailedToShowFullScreenContent");
                            }

                            /** Called when ad showed the full screen content. */
                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.i(TAG, "onAdShowedFullScreenContent");
                            }

                            /** Called when full screen content is dismissed. */
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.i(TAG, "onAdDismissedFullScreenContent");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "onAdFailedToLoad: "+loadAdError.getMessage());
                    }
                });
    }




    @Override
    public void onUserEarnedReward( RewardItem rewardItem) {

    }
}

