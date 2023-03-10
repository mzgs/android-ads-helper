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
          MaxHelper.InitInterstitial(this,"asdsadad")


      }

        GlobalScope.async {
            delay(3000L)
            MaxHelper.ShowInterstitial()

            delay(15000L)
            MaxHelper.ShowInterstitial()

        }




    }
}