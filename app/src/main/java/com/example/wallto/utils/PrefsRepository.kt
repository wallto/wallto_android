package com.example.wallto.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PrefsRepository {

    enum class Keys {
        TOKEN,
        LOGIN,
        EMAIL,
        PIN
    }

    companion object {
        private lateinit var prefs: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor

        fun init(context: Context) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        }

        fun putValue(key: String, value: String) {
            editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getValue(key: String): String? {
            return prefs.getString(key, "")
        }

        fun removeValue(key: String) {
            editor = prefs.edit()
            editor.remove(key)
            editor.apply()
        }
    }
}