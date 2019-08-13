/*
 * Created by Mark Abramenko on 13.08.19 14:09
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 13.08.19 14:09
 */

package com.example.wallto.ui.start.auth

import android.annotation.SuppressLint
import com.example.wallto.data.body.UserBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.AuthService
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AuthPresenterImpl(val view: AuthView) : AuthPresenter {

    private lateinit var authService: AuthService
    private lateinit var login: String
    private lateinit var password: String

    override fun sendData(login: String, password: String) {
        this.login = login
        this.password = password
    }

    override fun checkDataValid() {
        if (isLoginValid && isPasswordValid) {
            doUserAuth()
        } else view.showError("Ошибка ввода")
    }

    override fun initNetwork() {
        val retrofit = RestApi.getInstance()
        authService = retrofit.create(AuthService::class.java)
    }

    @SuppressLint("CheckResult")
    private fun doUserAuth() {
        val userBody = UserBody(login, password)
        authService.signIn("gnomes", userBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    user ->
                    checkAuthResponse(user.message, user.user_token)
                },
                {
                    throwable ->
                    view.writeLog("Error doUserAuth", throwable)
                }
            )
    }

    private fun checkAuthResponse(mes: String?, userToken: String?) {
        if (mes == "User is unknown.") {
            view.showError("Пользователя с таким именем не существует")
        } else if (userToken!!.isNotEmpty()) {
            successAuth(userToken)
        }
    }

    private fun successAuth(token: String) {
        PrefsRepository.putValue(PrefsRepository.Keys.TOKEN.toString(), token)
        PrefsRepository.putValue(PrefsRepository.Keys.LOGIN.toString(), login)
        view.openMainActivity()
    }


    private val isLoginValid: Boolean get() = login.isNotEmpty()
    private val isPasswordValid: Boolean get() = password.isNotEmpty()
}