/*
 * Created by Mark Abramenko on 13.08.19 14:08
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 13.08.19 14:08
 */

package com.example.wallto.ui.start.auth

import com.example.wallto.base.BaseView

interface AuthView : BaseView {
    fun showError(string: String?)
    fun showToast(string: String?)
    fun openMainActivity()
}