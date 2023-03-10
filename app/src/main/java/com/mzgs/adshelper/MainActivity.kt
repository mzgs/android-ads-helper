package com.mzgs.adshelpertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mzgs.adshelper.Ads

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Ads.Print("test")

    }
}