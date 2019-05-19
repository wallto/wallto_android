package com.example.wallto.utils

import android.annotation.SuppressLint
import com.example.wallto.ui.main.PricesFragment

object PricesSingle {
    @SuppressLint("StaticFieldLeak")
    private var ourInstance: PricesFragment? = null


    val instance: PricesFragment
    get() {
        if (ourInstance == null) {
            ourInstance = PricesFragment()
        }
        return ourInstance!!
    }
}
