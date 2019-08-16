/*
 * Created by Mark Abramenko on 15.08.19 20:40
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 15.08.19 20:40
 */

package com.example.wallto.ui.pin

import com.example.wallto.base.BasePresenter

interface PinCodePresenter : BasePresenter {
    fun chooseDescription()
    fun setAction(action: String)
}