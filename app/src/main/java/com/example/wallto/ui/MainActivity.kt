package com.example.wallto.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.utils.PrefsHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        Toast.makeText(this@MainActivity, prefs.getString(PrefsHelper.TOKEN, ""), Toast.LENGTH_SHORT).show()
    }
}
