package com.example.wallto.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.wallto.R

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }
}
