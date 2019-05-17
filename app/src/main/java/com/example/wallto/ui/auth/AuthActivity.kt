package com.example.wallto.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.model.User
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.TokenService
import com.example.wallto.ui.MainActivity
import com.example.wallto.utils.PrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class AuthActivity : AppCompatActivity() {
    private lateinit var tokenService: TokenService
    private lateinit var prefs: SharedPreferences
    private var isUserSignIn: Boolean = false
    private lateinit var userInfo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString(PrefsHelper.TOKEN, "") != "") {
            // Получает запрос список запросов для работы с токеном
            val retrofit = RestApi.getInstance()
            tokenService = retrofit.create(TokenService::class.java)
            checkTokenValid()
        } else {
            setContentView(R.layout.activity_auth)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.authContainer, StartFragment())
                .commit()
        }
    }

//TODO Здесь нужно будет сделать так, чтобы при отрицательном тесте на валидность
//     он перебрасывал нас на активити с вводом PIN-кода или отпечатка пальцев.
//     Пока что он заходит сразу на главную

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
                    refreshToken()
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
