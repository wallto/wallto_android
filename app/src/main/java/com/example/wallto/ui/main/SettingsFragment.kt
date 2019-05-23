package com.example.wallto.ui.main

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.model.User
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.AuthService
import com.example.wallto.ui.AuthActivity
import com.example.wallto.utils.PrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class SettingsFragment : Fragment() {

    private lateinit var btnLogout: TextView
    private lateinit var btnSupport: TextView

    private lateinit var version: TextView
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var authService: AuthService
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val retrofit = RestApi.getInstance()
        authService = retrofit.create(AuthService::class.java)

        version = v.findViewById(R.id.tvVersion)
        showVersion()

        username = v.findViewById(R.id.tvUsername)
        email = v.findViewById(R.id.tvEmail)
        showPersonalData()

        btnLogout = v.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener(onLogoutClickListener)

        return v
    }

    private val onLogoutClickListener = View.OnClickListener {
        authService.logOut(prefs.getString(PrefsHelper.TOKEN, ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    if (context != null) {
                        Toast.makeText(context, "Logout: " + t.message, Toast.LENGTH_SHORT).show()
                        logout()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "Ошибка: " + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun logout() {
        val e = prefs.edit()
        e.putString(PrefsHelper.TOKEN, "")
        e.putString(PrefsHelper.LOGIN, "")
        e.apply()
        val intent = Intent(context, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showPersonalData() {
        username.text = prefs.getString(PrefsHelper.LOGIN, "")
    }

    private fun showVersion() {
        val pInfo: PackageInfo = context!!.applicationContext.packageManager.getPackageInfo(context!!.packageName, 0)
        val vCode = pInfo.versionCode
        val vName = pInfo.versionName
        version.text = "Версия: $vName ($vCode)"
    }




}