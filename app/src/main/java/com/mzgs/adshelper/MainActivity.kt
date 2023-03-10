package com.mzgs.adshelpertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mzgs.adshelper.MaxHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


      MaxHelper.Init(this){
          Log.d("mzgs","max init")

//          MaxHelper.ShowMREC(this, findViewById(R.id.banner) ,"ascasc")
          MaxHelper.InitRewarded(this,"asdsadad")
//          MaxHelper.InitAppOpen(this,"dsvsdvs")

          MaxHelper.ShowNative(this, findViewById(R.id.banner) ,"ascasc")


      }

        GlobalScope.async {
//            MaxHelper.showAds = false
            delay(3000L)
//            MaxHelper.ShowRewarded (){}

            delay(15000L)

            MaxHelper.LoadNativeAd()
             Log.d("mzgs","native reloaded")


        }




    }
}