package com.mzgs.adshelper

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.applovin.sdk.AppLovinSdkUtils
import java.util.concurrent.TimeUnit


class MaxHelper {

    companion object {

        private lateinit var interstitialAd: MaxInterstitialAd
        private var retryAttemptInterstitial = 0.0
        private lateinit var rewardedAd: MaxRewardedAd
        private var retryAttemptRewarded = 0.0

        fun Init(context: Context, onInit: () -> Unit = {}) {
            AppLovinSdk.getInstance(context).setMediationProvider("max")
            AppLovinSdk.getInstance(context)
                .initializeSdk { configuration: AppLovinSdkConfiguration ->
                    onInit()
                }
        }

        fun InitInterstitial(activity: Activity, adUnitId: String) {
            interstitialAd = MaxInterstitialAd(adUnitId, activity)
            interstitialAd.setRequestListener {
            }

            interstitialAd.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    retryAttemptInterstitial = 0.0;
                }

                override fun onAdDisplayed(ad: MaxAd?) {
                }

                override fun onAdHidden(ad: MaxAd?) {
                    interstitialAd.loadAd()
                }

                override fun onAdClicked(ad: MaxAd?) {
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                    retryAttemptInterstitial++
                    val delayMillis =
                        TimeUnit.SECONDS.toMillis(
                            Math.pow(
                                2.0,
                                Math.min(6.0, retryAttemptInterstitial)
                            ).toLong()
                        )

                    Handler(Looper.getMainLooper()).postDelayed({
                        interstitialAd.loadAd()
                    }, delayMillis)

                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    Log.d("MaxHelper", "max_ad_display_fail: " + (error?.message ?: ""))
                    interstitialAd.loadAd()
                }
            })
            interstitialAd.loadAd()

        }

        fun ShowInterstitial(placementName: String = "",notReady: () -> Unit = {}) {
            if (interstitialAd.isReady) {
                interstitialAd.showAd(placementName)
                return
            }
            notReady()
        }


        fun InitRewarded(activity: Activity, adUnitId: String, rewardUser: () -> Unit = {}) {

            rewardedAd = MaxRewardedAd.getInstance(adUnitId, activity)
            rewardedAd.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    retryAttemptRewarded = 0.0
                }

                override fun onAdDisplayed(ad: MaxAd?) {
                }

                override fun onAdHidden(ad: MaxAd?) {
                    rewardedAd.loadAd()
                }

                override fun onAdClicked(ad: MaxAd?) {
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                    retryAttemptRewarded++
                    val delayMillis = TimeUnit.SECONDS.toMillis(
                        Math.pow(2.0, Math.min(6.0, retryAttemptRewarded)).toLong()
                    )

                    Handler(Looper.getMainLooper()).postDelayed({
                        rewardedAd.loadAd()
                    }, delayMillis)
                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    rewardedAd.loadAd()
                }

                override fun onRewardedVideoStarted(ad: MaxAd?) {
                }

                override fun onRewardedVideoCompleted(ad: MaxAd?) {
                }

                override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {
                    rewardUser()
                }

            })


            rewardedAd.loadAd()


        }

        fun ShowRewarded(placementName: String = "",notReady: () -> Unit = {}) {

            if (rewardedAd.isReady) {
                rewardedAd.showAd(placementName);
                return
            }
            notReady()
        }


        fun ShowBanner(activity: Activity, container: ViewGroup, adUnitId: String) {
            var adView = MaxAdView(adUnitId, activity)
            var width = ViewGroup.LayoutParams.MATCH_PARENT;
            var heightPx = AppLovinSdkUtils.dpToPx(activity, 50);
            adView.setLayoutParams(FrameLayout.LayoutParams(width, heightPx));
            container.addView(adView);
            // Load the ad
            adView.loadAd();
        }

        fun ShowMREC(activity: Activity, container: ViewGroup, adUnitId: String) {

            var adView = MaxAdView(adUnitId, MaxAdFormat.MREC, activity)

            val widthPx = AppLovinSdkUtils.dpToPx(activity, 300)
            val heightPx = AppLovinSdkUtils.dpToPx(activity, 250)
            adView.layoutParams = FrameLayout.LayoutParams(widthPx, heightPx)
            container.addView(adView);
            // Load the ad
            adView.loadAd();

        }


    }
}