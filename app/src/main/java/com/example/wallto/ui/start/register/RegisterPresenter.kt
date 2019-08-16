/*
 * Created by Mark Abramenko on 12.08.19 20:12
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 12.08.19 20:12
 */

package com.example.wallto.ui.start.register

import android.annotation.SuppressLint
import com.example.wallto.base.BasePresenter
import com.example.wallto.data.User
import com.example.wallto.data.body.UserBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.AuthService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*

class RegisterPresenter(private val view: RegisterView) : BasePresenter {

    private lateinit var authService: AuthService
    private lateinit var login: String
    private lateinit var password: String
    private lateinit var passwordAgain: String

    fun checkDataValid() {
        if (isLoginValid && isPasswordValid) {
            if (isPasswordsCons) {
                doUserRegister()
            } else view.showError("Пароли не совпадают")
        } else view.showError("Ошибка ввода")
    }

    override fun initNetwork() {
        val retrofit = RestApi.getInstance()
        authService = retrofit.create(AuthService::class.java)
    }

    fun sendData(login: String, password: String, passwordAgain: String) {
        this.login = login
        this.password = password
        this.passwordAgain = passwordAgain
    }

    @SuppressLint("CheckResult")
    private fun doUserRegister() {
        val userBody = UserBody(login, password)
        authService.signUp("gnomes", userBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    checkRegisterSuccess(user.message)
                },
                { throwable ->
                    view.showError("Ошибка регистрации: " + throwable.message)
                    view.writeLog("Error doUserRegister", throwable)
                }
            )
    }

    private fun checkRegisterSuccess(message: String?) {
        if (message == "Successfully") {
            view.showToast("Авторизация прошла успешно")
            successRegister()
        } else {
            view.showError(message)
            view.writeLog(message)
        }
    }

    private fun successRegister() {
        view.openNextActivity()
    }

    private val isLoginValid: Boolean get() = login.isNotEmpty()
    private val isPasswordValid: Boolean get() = password.isNotEmpty()
    private val isPasswordsCons: Boolean get() = Objects.equals(password, passwordAgain)

}