/*
 * Created by Mark Abramenko on 12.08.19 20:11
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 12.08.19 20:11
 */

package com.example.wallto.ui.start.register

import com.example.wallto.base.BasePresenter

interface RegisterPresenter : BasePresenter {
    fun checkDataValid()
    fun sendData(login: String, password: String, passwordAgain: String)
}
