/*
 * Created by Mark Abramenko on 09.08.19 12:34
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 09.08.19 12:26
 */

package com.example.wallto.ui

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.data.User
import com.example.wallto.data.body.TokenBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.TokenService
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pin_code.*

class PinCodeActivity : AppCompatActivity(), View.OnClickListener {
    private var circleCount: Int = 1
    private var isPinAgain: Boolean = false
    private var pin: String = ""
    private lateinit var prefs: SharedPreferences
    private lateinit var tokenService: TokenService

    private var action: String = ""

    private lateinit var desc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)

        desc = findViewById(R.id.tvPinDesc)

        val bundle = intent.extras
        if (bundle?.getString("ACT") != null) {
            action = bundle.getString("ACT")!!
        }

        changeDesc()

        val retrofit = RestApi.getInstance()
        tokenService = retrofit.create(TokenService::class.java)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        tvNum0.setOnClickListener(this)
        tvNum1.setOnClickListener(this)
        tvNum2.setOnClickListener(this)
        tvNum3.setOnClickListener(this)
        tvNum4.setOnClickListener(this)
        tvNum5.setOnClickListener(this)
        tvNum6.setOnClickListener(this)
        tvNum7.setOnClickListener(this)
        tvNum8.setOnClickListener(this)
        tvNum9.setOnClickListener(this)
        ibBack.setOnClickListener(onBackClickListener)
    }

    private fun changeDesc() {

        when (action) {
            "CHANGE" -> desc.text = "Введите старый PIN"
            "NEW" -> desc.text = "Придумайте PIN"
            "CANCEL" -> desc.text = "Сначала введите старый PIN"
        }

    }

    private val onBackClickListener = View.OnClickListener {
        if (circleCount > 1) {
            deletePin()
        }
    }

    override fun onClick(v: View?) {
        val t: TextView = findViewById(v!!.id)
        pin += t.text.toString()
        addCircles()
    }

    private fun deletePin() {
        circleCount = 1
        ivNum1.setImageResource(R.drawable.shape_circle)
        ivNum2.setImageResource(R.drawable.shape_circle)
        ivNum3.setImageResource(R.drawable.shape_circle)
        ivNum4.setImageResource(R.drawable.shape_circle)
        pin = ""
    }

    private fun addCircles() {
        when (circleCount) {
            1 -> {
                ivNum1.setImageResource(R.drawable.shape_circle_fill)
                circleCount++
            }
            2 -> {
                ivNum2.setImageResource(R.drawable.shape_circle_fill)
                circleCount++
            }
            3 -> {
                ivNum3.setImageResource(R.drawable.shape_circle_fill)
                circleCount++
            }
            4 -> {
                ivNum4.setImageResource(R.drawable.shape_circle_fill)
                checkPinValid()
            }
            else -> deletePin()
        }
    }

    //TODO Требуется рефакторинг

    private fun checkPinValid() {
        when (action) {
            "NEW" -> {
                Toast.makeText(this, "Теперь ваш кошелек защищен PIN-кодом", Toast.LENGTH_SHORT).show()
                val e = prefs.edit()
                e.putString(PrefsRepository.Keys.PIN.toString(), pin)
                e.apply()
                successAuth()
            }
            "CANCEL" -> {
                if (pin == prefs.getString(PrefsRepository.Keys.PIN.toString(), "")) {
                    Toast.makeText(this, "PIN-код удален", Toast.LENGTH_SHORT).show()
                    val e = prefs.edit()
                    e.putString(PrefsRepository.Keys.PIN.toString(), "")
                    e.apply()
                    successAuth()
                } else {
                    Toast.makeText(this, "Неверный PIN-код", Toast.LENGTH_SHORT).show()
                    deletePin()
                }
            }
            "CHANGE" -> {
                when {
                    isPinAgain -> {
                        if (pin == prefs.getString(PrefsRepository.Keys.PIN.toString(), "")) {
                            Toast.makeText(this, "Вы ввели старый PIN", Toast.LENGTH_SHORT).show()
                            deletePin()
                        } else {
                            Toast.makeText(this, "Вы успешно изменили PIN-код", Toast.LENGTH_SHORT).show()
                            val e = prefs.edit()
                            e.putString(PrefsRepository.Keys.PIN.toString(), pin)
                            e.apply()
                            successAuth()
                        }
                    }
                    pin == prefs.getString(PrefsRepository.Keys.PIN.toString(), "") -> {
                        desc.text = "Придумайте новый PIN"
                        Toast.makeText(this, "PIN-код введен верно, можете ввести новый", Toast.LENGTH_SHORT).show()
                        deletePin()
                        isPinAgain = true
                    }
                    else -> {
                        Toast.makeText(this, "Неверный PIN-код", Toast.LENGTH_SHORT).show()
                        deletePin()
                    }
                }
            }
            else -> {
                if (pin == prefs.getString(PrefsRepository.Keys.PIN.toString(), "")) {
                    refreshToken()
                } else {
                    Toast.makeText(this, "Неверный PIN-код", Toast.LENGTH_SHORT).show()
                    deletePin()
                }
            }
        }
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

    private fun refreshToken() {
        val tokenBody = TokenBody(prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""))
        tokenService.refreshToken(tokenBody, "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    println("Ответ на extend: " + t.user_token)
                    updateTokenData(t)
                }

                override fun onError(e: Throwable) {
                    println("Refresh error: " + e.message)
                }
            })
    }
}
