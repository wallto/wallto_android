/*
 * Created by Mark Abramenko on 10.08.19 20:51
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 10.08.19 20:50
 */

package com.example.wallto.ui.start.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import com.example.wallto.R
import com.example.wallto.data.User
import com.example.wallto.data.body.TokenBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.TokenService
import com.example.wallto.ui.MainActivity
import com.example.wallto.ui.PinCodeActivity
import com.example.wallto.ui.start.StartFragment
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class StartBaseActivity : AppCompatActivity(), StartBaseView {
    private lateinit var progressBar: ProgressBar
    private lateinit var error: TextView
    private var TAG = this.javaClass.simpleName

    private val presenter = StartBasePresenterImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        progressBar = findViewById(R.id.progressAuth)
        error = findViewById(R.id.tvError)

        presenter.checkForToken()
    }

    override fun showError() {
        error.text = applicationContext.getString(R.string.net_error)
    }

    override fun writeLog(mes: String?) {
        Log.e(TAG, mes)
    }

    override fun writeLog(mes: String?, e: Throwable?) {
        Log.e(TAG, mes, e)
    }

    override fun openNextActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(0, 0) // Блокирует анимацию при переходе, если пользователь уже авторизовался
    }

    override fun openPinCodeActivity() {
        val intent = Intent(this@StartBaseActivity, PinCodeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun showProgressBar() {
        progressBar.visibility = ProgressBar.VISIBLE
    }

    override fun openStartAuthActivity() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.authContainer, StartFragment())
            .commit()
    }
}
