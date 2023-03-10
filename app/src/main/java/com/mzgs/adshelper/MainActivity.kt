package com.mzgs.adshelpertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mzgs.adshelper.Ads
import com.mzgs.adshelper.MaxHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


      MaxHelper.Init(this){
          Log.d("mzgs","max init")
      }

    }
}