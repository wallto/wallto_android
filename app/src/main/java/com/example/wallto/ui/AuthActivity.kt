package com.example.wallto.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.ProgressBar
import android.widget.TextView
import com.example.wallto.R
import com.example.wallto.model.User
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.TokenService
import com.example.wallto.ui.auth.StartFragment
import com.example.wallto.utils.PrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class AuthActivity : AppCompatActivity() {
    private lateinit var tokenService: TokenService
    private lateinit var prefs: SharedPreferences
    private lateinit var progressBar: ProgressBar
    private lateinit var error: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        progressBar = findViewById(R.id.progressAuth)
        error = findViewById(R.id.tvError)

        if (prefs.getString(PrefsHelper.TOKEN, "") != "") {
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
        tokenService.checkValid(prefs.getString(PrefsHelper.TOKEN, ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    if (t.message == "ok") {
                        System.out.println("Ответ на cvt: " + t.message)
                        successAuth()
                    }
                }

                override fun onError(e: Throwable) {
                    System.out.println("Ошибка cvt: " + e.message)
                    if (prefs.getString(PrefsHelper.PIN, "") == "") {
                        refreshToken()
                    } else {
                        val intent = Intent(this@AuthActivity, PinCodeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun refreshToken() {
        tokenService.refreshToken(prefs.getString(PrefsHelper.TOKEN, ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    System.out.println("Ответ на extend: " + t.user_token)
                    updateTokenData(t)
                }

                override fun onError(e: Throwable) {
                    System.out.println("Refresh error: " + e.message)
                    error.text = applicationContext.getString(R.string.net_error)
                }
            })
    }

    private fun updateTokenData(user: User) {
        val ed = prefs.edit()
        ed.putString(PrefsHelper.TOKEN, user.user_token)
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
