/*
 * Created by Mark Abramenko on 12.08.19 20:10
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 12.08.19 19:58
 */

package com.example.wallto.ui.start.register

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
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
import com.example.wallto.ui.start.StartFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class RegisterFragment : Fragment(), RegisterView {

    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var passwordAgain: EditText
    private lateinit var btnEnter: Button

    private var TAG = this.javaClass.simpleName

    private val presenter = RegisterPresenterImpl(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_register, container, false)

        login = v.findViewById(R.id.etLogin)
        password = v.findViewById(R.id.etPassword)
        passwordAgain = v.findViewById(R.id.etPasswordAgain)
        btnEnter = v.findViewById(R.id.btnSend)

        presenter.initNetwork()
        btnEnter.setOnClickListener(onSendClickListener)

        return v
    }

    private val onSendClickListener = View.OnClickListener {
        presenter.sendData(login.text.toString(), password.text.toString(), passwordAgain.text.toString())
        presenter.checkDataValid()
    }

    override fun openNextActivity() {
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authContainer, StartFragment())
            ?.commit()
    }

    override fun showError(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    override fun writeLog(mes: String?) {
        Log.e(TAG, mes)
    }

    override fun writeLog(mes: String?, e: Throwable?) {
        Log.e(TAG, mes, e)
    }

    override fun showToast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }
}