package com.example.wallto.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.data.User
import com.example.wallto.data.body.UserBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.AuthService
import com.example.wallto.ui.MainActivity
import com.example.wallto.utils.PrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class AuthFragment : Fragment() {
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var btnEnter: Button

    private lateinit var authService: AuthService

    private lateinit var prefs: SharedPreferences

    private var userBody: UserBody = UserBody("login", "password")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_auth, container, false)

        val retrofit = RestApi.getInstance()
        authService = retrofit.create(AuthService::class.java)

        login = v.findViewById(R.id.etLogin)
        password = v.findViewById(R.id.etPassword)
        btnEnter = v.findViewById(R.id.btnEnter)

        btnEnter.setOnClickListener(onEnterClickListener)

        return v
    }

    private val onEnterClickListener = View.OnClickListener {
        if (isLoginValid && isPasswordValid) {
            userAuth()
        } else {
            Toast.makeText(context, "Ошибка ввода", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("CheckResult")
    private fun userAuth() {
        userBody.login = login.text.toString()
        userBody.password = password.text.toString()
        authService.signIn("gnomes", userBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    if (t.message == "User is unknown.") {
                        Toast.makeText(context, t.user_token, Toast.LENGTH_SHORT).show()
                    } else if (!TextUtils.isEmpty(t.user_token)) {
                        successAuth(t)
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "Ошибка авторизации: " + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun successAuth(user: User) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val ed = prefs.edit()
        ed.putString(PrefsHelper.TOKEN, user.user_token)
        ed.putString(PrefsHelper.LOGIN, login.text.toString())
        ed.apply()

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private val isLoginValid: Boolean get() = !TextUtils.isEmpty(login.text)
    private val isPasswordValid: Boolean get() = !TextUtils.isEmpty(password.text)

}