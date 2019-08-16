/*
 * Created by Mark Abramenko on 15.08.19 20:40
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 15.08.19 20:40
 */

package com.example.wallto.ui.pin


import android.annotation.SuppressLint
import com.example.wallto.R
import com.example.wallto.data.body.TokenBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.TokenService
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PinCodePresenterImpl(val view: PinCodeView) : PinCodePresenter {
    private lateinit var tokenService: TokenService

    private var enteredDigitsNumber: Int = 1
    private var isChangingPinCorrect: Boolean = false
    private var pin: String = ""
    private var action: String = ""

    override fun initNetwork() {
        val retrofit = RestApi.getInstance()
        tokenService = retrofit.create(TokenService::class.java)
    }

    override fun chooseDescription() {
        when (action) {
            "CHANGE" -> view.showDescription(R.string.pin_oldpin)
            "NEW" -> view.showDescription(R.string.pin_concoct)
            "CANCEL" -> view.showDescription(R.string.pin_firstold)
        }
    }

    override fun setAction(action: String) {
        this.action = action
    }

    fun onBackEntered() {
        if (enteredDigitsNumber > 1) {
            clearPin()
        }
    }

    fun writePin(pressedButton: String) {
        pin += pressedButton
    }

    fun checkNumberOfEnteredDigits() {
        when (enteredDigitsNumber) {
            1 -> {
                view.drawFillCircle(enteredDigitsNumber)
                enteredDigitsNumber++
            }
            2 -> {
                view.drawFillCircle(enteredDigitsNumber)
                enteredDigitsNumber++
            }
            3 -> {
                view.drawFillCircle(enteredDigitsNumber)
                enteredDigitsNumber++
            }
            4 -> {
                view.drawFillCircle(enteredDigitsNumber)
                selectAction()
            }
            else -> clearPin()
        }
    }

    private fun selectAction() {
        when (action) {
            "NEW" -> onActionNewSelected()
            "CANCEL" -> onActionDeleteSelected()
            "CHANGE" -> onActionChangeSelected()
            else -> onTokenTimeOut()
        }
    }

    private fun onTokenTimeOut() {
        if (isPinExist) doTokenRefresh()
        else wrongPin()
    }


    @SuppressLint("CheckResult")
    private fun doTokenRefresh() {
        val tokenBody = TokenBody(PrefsRepository.getValue(PrefsRepository.Keys.TOKEN.toString()))
        tokenService.refreshToken(tokenBody, "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view.writeLog("doTokenRefresh response: " + it.user_token)
                    updateTokenData(it.user_token)
                },
                {
                    view.writeLog("doTokenRefresh error", it)
                }
            )
    }

    private fun updateTokenData(userToken: String?) {
        PrefsRepository.putValue(PrefsRepository.Keys.PIN.toString(), userToken!!)
        onSuccessfulAuth()
    }

    private fun onActionChangeSelected() {
        when {
            isChangingPinCorrect -> checkNewPinCorrectness()
            isPinExist -> prepareToEnterNewPin()
            else -> wrongPin()
        }
    }

    private fun prepareToEnterNewPin() {
        view.showDescription(R.string.pin_concoct)
        view.showToast(R.string.pin_correct)
        clearPin()
        isChangingPinCorrect = true
    }

    private fun checkNewPinCorrectness() {
        if (isPinExist) {
            view.showToast(R.string.pin_enteredold)
            clearPin()
        } else {
            successfulPinChange()
        }
    }

    private fun successfulPinChange() {
        view.showToast(R.string.pin_changed_success)
        PrefsRepository.putValue(PrefsRepository.Keys.PIN.toString(), pin)
        onSuccessfulAuth()
    }

    private fun onActionDeleteSelected() {
        if (isPinExist) {
            view.showToast(R.string.pin_deleted)
            PrefsRepository.putValue(PrefsRepository.Keys.PIN.toString(), "")
            onSuccessfulAuth()
        } else {
            wrongPin()
        }
    }

    private fun onActionNewSelected() {
        view.showToast(R.string.pin_protected)
        PrefsRepository.putValue(PrefsRepository.Keys.PIN.toString(), pin)
        onSuccessfulAuth()
    }

    private fun wrongPin() {
        view.showToast(R.string.pin_wrong)
        clearPin()
    }

    private fun onSuccessfulAuth() {
        view.openMainActivity()
    }

    private fun clearPin() {
        enteredDigitsNumber = 1
        view.drawEmptyCircles()
        pin = ""
    }

    private val isPinExist: Boolean get() = pin == PrefsRepository.getValue(PrefsRepository.Keys.PIN.toString())
}