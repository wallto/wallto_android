/*
 * Created by Mark Abramenko on 10.08.19 12:35
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 09.08.19 12:26
 */

package com.example.wallto.ui.start

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
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class StartBaseActivity : AppCompatActivity() {
    private lateinit var tokenService: TokenService
    private lateinit var prefs: SharedPreferences
    private lateinit var progressBar: ProgressBar
    private lateinit var error: TextView
    private var TAG = this.javaClass.simpleName

    private val startBasePresenter = StartBasePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        progressBar = findViewById(R.id.progressAuth)
        error = findViewById(R.id.tvError)

        if (PrefsRepository.getValue(PrefsRepository.Keys.TOKEN.toString()) != "") {
            progressBar.visibility = ProgressBar.VISIBLE

            // Получает запрос список запросов для работы с токеном
            val retrofit = RestApi.getInstance()
            tokenService = retrofit.create(TokenService::class.java)
            checkTokenValid()
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.authContainer, StartFragment())
                .commit()
        }
    }

    @SuppressLint("CheckResult")
    private fun checkTokenValid() {
        tokenService.checkValid(prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    if (t.message == "ok") {
                        Log.e(TAG, "Ответ checkTokenValid: " + t.message)
                        successAuth()
                    }
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "Ошибка checkTokenValid: ", e)
                    if (prefs.getString(PrefsRepository.Keys.PIN.toString(), "") == "") {
                        refreshToken()
                    } else {
                        val intent = Intent(this@StartBaseActivity, PinCodeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun refreshToken() {
        val tokenBody = TokenBody(prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""))
        tokenService.refreshToken(tokenBody, "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    Log.e(TAG, "Ответ на refreshToken: " + t.user_token)
                    updateTokenData(t)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "Refresh error: ", e)
                    error.text = applicationContext.getString(R.string.net_error)
                }
            })
    }

    private fun updateTokenData(user: User) {
        val ed = prefs.edit()
        ed.putString(PrefsRepository.Keys.TOKEN.toString(), user.user_token)
        ed.apply()
        successAuth()
    }

    private fun successAuth() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(0, 0) // Блокирует анимацию при переходе, если пользователь уже авторизовался
    }
}
