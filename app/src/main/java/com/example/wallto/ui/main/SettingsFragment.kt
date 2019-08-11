package com.example.wallto.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.data.User
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.AuthService
import com.example.wallto.ui.start.base.StartBaseActivity
import com.example.wallto.ui.MainActivity
import com.example.wallto.ui.PinCodeActivity
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class SettingsFragment : Fragment() {

    private lateinit var btnLogout: TextView
    private lateinit var btnSupport: TextView
    private lateinit var btnChangePin: TextView

    private lateinit var switchPin: Switch

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

        btnChangePin = v.findViewById(R.id.btnChangePin)
        btnChangePin.setOnClickListener(onChangePinClickListener)

        // Убрать или добавить пин код
        switchPin = v.findViewById(R.id.switchPin)
        if (prefs.getString(PrefsRepository.Keys.PIN.toString(), "") != "") switchPin.isChecked = true
        switchPin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val intent = Intent(context, PinCodeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("ACT", "NEW")
                startActivity(intent)
            } else {
                val intent = Intent(context, PinCodeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("ACT", "CANCEL")
                startActivity(intent)
            }

        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val act = activity as MainActivity
        act.supportActionBar!!.show()
        act.supportActionBar!!.title = "Настройки"
    }

    private val onChangePinClickListener = View.OnClickListener {
        if (prefs.getString(PrefsRepository.Keys.PIN.toString(), "") != "") {
            val intent = Intent(context, PinCodeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("ACT", "CHANGE")
            startActivity(intent)
        } else {
            Toast.makeText(context, "Вы не используете PIN", Toast.LENGTH_SHORT).show()
        }
    }

    private val onLogoutClickListener = View.OnClickListener {
        authService.logOut(prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""), "gnomes")
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
        e.putString(PrefsRepository.Keys.TOKEN.toString(), "")
        e.putString(PrefsRepository.Keys.LOGIN.toString(), "")
        e.apply()
        val intent = Intent(context, StartBaseActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showPersonalData() {
        email.text = prefs.getString(PrefsRepository.Keys.LOGIN.toString(), "")
    }

    private fun showVersion() {
        val pInfo: PackageInfo = context!!.applicationContext.packageManager.getPackageInfo(context!!.packageName, 0)
        val vCode = pInfo.versionCode
        val vName = pInfo.versionName
        version.text = "Версия: $vName ($vCode)"
    }




}