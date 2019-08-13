/*
 * Created by Mark Abramenko on 13.08.19 14:08
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 13.08.19 14:08
 */

package com.example.wallto.ui.start.auth

import com.example.wallto.base.BasePresenter

interface AuthPresenter : BasePresenter {
    fun sendData(login: String, password: String)
    fun checkDataValid()
}