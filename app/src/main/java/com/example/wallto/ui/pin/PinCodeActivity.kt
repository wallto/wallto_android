/*
 * Created by Mark Abramenko on 15.08.19 20:39
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 15.08.19 19:50
 */

package com.example.wallto.ui.pin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.data.User
import com.example.wallto.data.body.TokenBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.TokenService
import com.example.wallto.ui.MainActivity
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pin_code.*

class PinCodeActivity : AppCompatActivity(), View.OnClickListener, PinCodeView {
    private val presenter = PinCodePresenterImpl(this)
    private lateinit var desc: TextView
    private var TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)

        desc = findViewById(R.id.tvPinDesc)

        initAction()
        initButtons()
        presenter.chooseDescription()
        presenter.initNetwork()
    }

    private val onBackClickListener = View.OnClickListener {
        presenter.onBackEntered()
    }

    override fun onClick(v: View?) {
        doVibrate()
        val currentPressedButton: TextView = findViewById(v!!.id)
        presenter.writePin(currentPressedButton.text.toString())
        presenter.checkNumberOfEnteredDigits()
    }

    private fun doVibrate() {
        val v: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(100)
        }
    }

    private fun initButtons() {
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

    private fun initAction() {
        val bundle = intent.extras
        if (bundle?.getString("ACT") != null) {
            presenter.setAction(bundle.getString("ACT")!!)
        }
    }

    override fun showDescription(res: Int) {
        desc.text = getString(res)
    }

    override fun drawFillCircle(circleNumber: Int) {
        when (circleNumber) {
            1 -> ivNum1.setImageResource(R.drawable.shape_circle_fill)
            2 -> ivNum2.setImageResource(R.drawable.shape_circle_fill)
            3 -> ivNum3.setImageResource(R.drawable.shape_circle_fill)
            4 -> ivNum4.setImageResource(R.drawable.shape_circle_fill)
        }
    }

    override fun drawEmptyCircles() {
        ivNum1.setImageResource(R.drawable.shape_circle)
        ivNum2.setImageResource(R.drawable.shape_circle)
        ivNum3.setImageResource(R.drawable.shape_circle)
        ivNum4.setImageResource(R.drawable.shape_circle)
    }

    override fun showToast(res: Int) {
        Toast.makeText(this, getString(res), Toast.LENGTH_SHORT).show()
    }

    override fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(0, 0) // Блокирует анимацию при переходе, если пользователь уже авторизовался
    }

    override fun writeLog(mes: String?) {
        Log.e(TAG, mes)
    }

    override fun writeLog(mes: String?, e: Throwable?) {
        Log.e(TAG, mes, e)
    }
}
