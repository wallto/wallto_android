package com.example.wallto.ui.auth

import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.AuthService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class RegisterFragment : Fragment() {
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var passwordAgain: EditText
    private lateinit var btnEnter: Button

    private lateinit var authService: AuthService

    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_register, container, false)

        val retrofit = RestApi.getInstance()
        authService = retrofit.create(AuthService::class.java)

        login = v.findViewById(R.id.etLogin)
        password = v.findViewById(R.id.etPassword)
        passwordAgain = v.findViewById(R.id.etPasswordAgain)
        btnEnter = v.findViewById(R.id.btnSend)
        btnEnter.setOnClickListener(onSendClickListener)

        return v
    }

    private val onSendClickListener = View.OnClickListener {
        if (isLoginValid && isPasswordValid) {
            if (isPasswordsCons) {
                userRegister()
            } else {
                Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Ошибка ввода", Toast.LENGTH_SHORT).show()
        }
    }

    private fun userRegister() {
        authService.signUp(login.text.toString(), password.text.toString(), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    if (t.message == "Successfully") {
                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                        successRegister()
                    } else {
                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "Ошибка регистрации: " + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun successRegister() {
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authContainer, StartFragment())
            ?.commit()
    }


    private val isLoginValid: Boolean get() = !TextUtils.isEmpty(login.text)
    private val isPasswordValid: Boolean get() = !TextUtils.isEmpty(password.text)
    private val isPasswordsCons: Boolean get() = TextUtils.equals(password.text, passwordAgain.text)
}