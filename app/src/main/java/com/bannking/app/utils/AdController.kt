package com.bannking.app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.bannking.app.R
import com.bannking.app.core.BaseActivity.Companion.isPremium
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdController {
    private var adCounter = 1
    private var adDisplayCounter = 1
    var mInterstitialAd: InterstitialAd? = null
    private lateinit var savedAdTime: SessionManager
    fun initAd(context: Context?) {
        MobileAds.initialize(context!!) {}
        savedAdTime = SessionManager(context, SessionManager.ADTIME)
    }

    fun loadInterAd(context: Context?) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context!!,
            context.getString(R.string.admob_interstitial),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    mInterstitialAd = null
                }
            })
    }

    fun showInterAd(context: Activity, intent: Intent?, requstCode: Int, onCallBack: () -> Unit = {}) {
        if (adCounter == adDisplayCounter && mInterstitialAd != null && !isPremium) {
            adCounter = 1
            mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
//                    Log.d("TAG", "The ad was dismissed.");
                    savedAdTime.saveLong(SessionManager.ADTIME, System.currentTimeMillis())
                    loadInterAd(context)
                    startActivity(context, intent, requstCode)
                    onCallBack.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                     Called when fullscreen content failed to show.
//                    Log.d("TAG", "The ad failed to show.");
                    onCallBack.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null
                    //                    Log.d("TAG", "The ad was shown.");
                }
            }
            mInterstitialAd!!.show(context)
        } else {
            if (adCounter == adDisplayCounter) {
                adCounter = 1
            }
            startActivity(context, intent, requstCode)
            onCallBack.invoke()
        }
    }

    fun startActivity(context: Activity, intent: Intent?, requestCode: Int) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode)
        }
    }

    fun showInterAd(context: Fragment, intent: Intent?, requstCode: Int) {
        if (adCounter == adDisplayCounter && mInterstitialAd != null) {
            adCounter = 1
            mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
//                    Log.d("TAG", "The ad was dismissed.");
                    loadInterAd(context.activity)
                    startActivity(context, intent, requstCode)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                     Called when fullscreen content failed to show.
//                    Log.d("TAG", "The ad failed to show.");
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null
                    //                    Log.d("TAG", "The ad was shown.");
                }
            }
            mInterstitialAd!!.show(context.requireActivity())
        } else {
            if (adCounter == adDisplayCounter) {
                adCounter = 1
            }
            startActivity(context, intent, requstCode)
        }
    }

    fun startActivity(context: Fragment, intent: Intent?, requestCode: Int) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode)
        }
    }
}