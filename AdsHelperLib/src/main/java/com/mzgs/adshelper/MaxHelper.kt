package com.mzgs.adshelper

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.applovin.sdk.AppLovinSdkUtils
import java.util.concurrent.TimeUnit


class MaxHelper {

    companion object {

        var showAds = true
        private lateinit var interstitialAd: MaxInterstitialAd
        private var retryAttemptInterstitial = 0.0
        private lateinit var rewardedAd: MaxRewardedAd
        private var retryAttemptRewarded = 0.0
        private lateinit var nativeAdLoader: MaxNativeAdLoader
        private var nativeAd: MaxAd? = null

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
           if (!showAds) return
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
            if (!showAds) return
            if (rewardedAd.isReady) {
                rewardedAd.showAd(placementName);
                return
            }
            notReady()
        }


        fun ShowBanner(activity: Activity, container: ViewGroup, adUnitId: String) {
            if (!showAds) return

            var adView = MaxAdView(adUnitId, activity)
            var width = ViewGroup.LayoutParams.MATCH_PARENT;
            var heightPx = AppLovinSdkUtils.dpToPx(activity, 50);
            adView.setLayoutParams(FrameLayout.LayoutParams(width, heightPx));
            container.addView(adView);
            // Load the ad
            adView.loadAd();
        }

        fun ShowMREC(activity: Activity, container: ViewGroup, adUnitId: String) {
            if (!showAds) return
            var adView = MaxAdView(adUnitId, MaxAdFormat.MREC, activity)

            val widthPx = AppLovinSdkUtils.dpToPx(activity, 300)
            val heightPx = AppLovinSdkUtils.dpToPx(activity, 250)
            adView.layoutParams = FrameLayout.LayoutParams(widthPx, heightPx)
            container.addView(adView);
            // Load the ad
            adView.loadAd();

        }

        fun  InitAppOpen(context: Context,adUnitId: String ){
            var appOpenManager = AppOpenManager(context, adUnitId)
        }



        fun ShowNative(activity: Activity, container: ViewGroup,adUnitId: String){
            if (!showAds) return

             nativeAdLoader = MaxNativeAdLoader( adUnitId, activity )
             nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {

                override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd)
                {
                    // Clean up any pre-existing native ad to prevent memory leaks.
                    if ( nativeAd != null )
                    {
                        nativeAdLoader.destroy( nativeAd )
                    }

                    // Save ad for cleanup.
                    nativeAd = ad

                    // Add ad view to view.
                    container.removeAllViews()
                    container.addView( nativeAdView )
                }

                override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError)
                {
                    Handler(Looper.getMainLooper()).postDelayed({
                        nativeAdLoader.loadAd()
                    }, 20000)
                }

                override fun onNativeAdClicked(ad: MaxAd)
                {
                    // Optional click callback
                }
            })
            nativeAdLoader.loadAd()
        }

        fun LoadNativeAd(){
            nativeAdLoader.loadAd()
        }




    }
}


class AppOpenManager(applicationContext: Context?, val adID: String?) : DefaultLifecycleObserver,
    MaxAdListener {
    private var appOpenAd: MaxAppOpenAd
    private lateinit var context: Context



    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        if (applicationContext != null) {
            context = applicationContext
        }

        appOpenAd = MaxAppOpenAd(adID!!, applicationContext!!)
        appOpenAd.setListener(this)
    }

    private fun showAdIfReady() {
        if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized) return

        if (appOpenAd.isReady) {

            appOpenAd.showAd(adID)
        } else {
            appOpenAd.loadAd()
        }
    }


    override fun onPause(owner: LifecycleOwner) {

        if (!appOpenAd.isReady) {
            appOpenAd.loadAd()
        }

    }

    override fun onResume(owner: LifecycleOwner) {
        showAdIfReady()
    }

    override fun onStart(owner: LifecycleOwner) {

    }

    override fun onAdLoaded(ad: MaxAd) {}
    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
    override fun onAdDisplayed(ad: MaxAd) {}
    override fun onAdClicked(ad: MaxAd) {}

    override fun onAdHidden(ad: MaxAd) {
        appOpenAd.loadAd()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        appOpenAd.loadAd()
    }
}