package com.example.wallto.ui.auth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.wallto.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.authContainer, StartFragment())
            .commit()
    }
}
