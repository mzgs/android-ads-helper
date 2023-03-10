package com.mzgs.adshelper

import android.content.Context
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import java.util.concurrent.Callable

class MaxHelper {

    companion object{

        fun Init(context: Context, onInit: ()-> Unit = {} ){
            AppLovinSdk.getInstance( context ).setMediationProvider( "max" )
            AppLovinSdk.getInstance( context ).initializeSdk { configuration: AppLovinSdkConfiguration ->
                onInit()
            }
        }

    }
}