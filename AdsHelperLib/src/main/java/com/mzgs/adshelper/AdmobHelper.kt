package com.mzgs.adshelper

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdmobHelper {

    companion object{

        var mRewardedAd: RewardedAd? = null



        fun Init(context: Context, onInit: () -> Unit = {}) {
            MobileAds.initialize(context) { initializationStatus: InitializationStatus ->
                val statusMap =
                    initializationStatus.adapterStatusMap
                for (adapterClass in statusMap.keys) {
                    val status = statusMap[adapterClass]
                    Log.d(
                        "MyApp", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status!!.description, status.latency
                        )
                    )
                }

                onInit()
            }
        }

        fun ShowInterstitial(activity: Activity, adUnitID :String = "ca-app-pub-3940256099942544/1033173712" ) {
            val adRequest: AdRequest = AdRequest.Builder().build()
            InterstitialAd.load(activity, adUnitID, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        interstitialAd.show(activity)
                    }

                })
        }






        fun LoadRewarded(context: Context, adUnitID :String ="ca-app-pub-3940256099942544/5224354917") {
            val adRequest: AdRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context, adUnitID,
                adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        mRewardedAd = null
                        Handler(Looper.getMainLooper()).postDelayed({
                                LoadRewarded(context)
                        },5000)
                    }

                    override fun onAdLoaded(@NonNull rewardedAd: RewardedAd) {
                        mRewardedAd = rewardedAd
                        mRewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                mRewardedAd = null
                                LoadRewarded(context)

                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when ad fails to show.
                                mRewardedAd = null
                                LoadRewarded(context)

                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                            }
                        }
                    }
                })
        }

        fun ShowRewarded(activityContext: Activity, earned: () -> Unit = {}) {
            if (mRewardedAd != null) {
                mRewardedAd?.show(
                    activityContext
                ) { earned() }
            }
        }

        val isRewardedReady: Boolean
            get() = mRewardedAd != null


        fun ShowBanner(context: Context,container: ViewGroup,  adUnitID: String="ca-app-pub-3940256099942544/6300978111" ){

            var adView = AdView(context)
            adView.setAdSize(AdSize.FULL_BANNER)
            adView.adUnitId  = adUnitID
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            container.addView(adView)
        }

    }
}