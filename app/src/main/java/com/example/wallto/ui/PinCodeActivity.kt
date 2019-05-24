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
import com.example.wallto.model.User
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.TokenService
import com.example.wallto.utils.PrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pin_code.*
import kotlinx.android.synthetic.main.fragment_start.view.*

class PinCodeActivity : AppCompatActivity(), View.OnClickListener {
    private var circleCount: Int = 1
    private var pin: String = ""
    private lateinit var prefs: SharedPreferences
    private lateinit var tokenService: TokenService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)
        val retrofit = RestApi.getInstance()
        tokenService = retrofit.create(TokenService::class.java)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val e = prefs.edit()
        e.putString(PrefsHelper.PIN, "1488")
        e.apply()

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

    private fun checkPinValid() {
        if (pin == prefs.getString(PrefsHelper.PIN, "")) {
            Toast.makeText(this, "Пожилой успех", Toast.LENGTH_SHORT).show()
            refreshToken()
        } else {
            deletePin()
        }
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
}
