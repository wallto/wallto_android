/*
 * Created by Mark Abramenko on 13.08.19 14:12
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 12.08.19 19:58
 */

package com.example.wallto.ui.start.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
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
import com.example.wallto.data.body.UserBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.AuthService
import com.example.wallto.ui.MainActivity
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class AuthFragment : Fragment(), AuthView {
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var btnEnter: Button

    private var TAG = this.javaClass.simpleName

    private val presenter = AuthPresenterImpl(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_auth, container, false)

        login = v.findViewById(R.id.etLogin)
        password = v.findViewById(R.id.etPassword)
        btnEnter = v.findViewById(R.id.btnEnter)

        presenter.initNetwork()
        btnEnter.setOnClickListener(onEnterClickListener)

        return v
    }

    private val onEnterClickListener = View.OnClickListener {
        presenter.sendData(login.text.toString(), password.text.toString())
        presenter.checkDataValid()
    }

    override fun writeLog(mes: String?) {
        Log.e(TAG, mes)
    }

    override fun writeLog(mes: String?, e: Throwable?) {
        Log.e(TAG, mes, e)
    }

    override fun showError(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    override fun showToast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    override fun openMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}