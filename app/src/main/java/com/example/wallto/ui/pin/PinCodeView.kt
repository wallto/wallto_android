/*
 * Created by Mark Abramenko on 15.08.19 20:39
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 15.08.19 20:39
 */

package com.example.wallto.ui.pin

import com.example.wallto.base.BaseView

interface PinCodeView : BaseView {
    fun showDescription(res: Int)
    fun showToast(res: Int)
    fun drawFillCircle(circleNumber: Int)
    fun drawEmptyCircles()
    fun openMainActivity()
}