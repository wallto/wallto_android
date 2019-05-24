package com.example.wallto.ui

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.example.wallto.R
import com.example.wallto.ui.main.PricesFragment
import com.example.wallto.ui.main.SettingsFragment
import com.example.wallto.ui.main.WalletsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var btv: BottomNavigationView
    private lateinit var prefs: SharedPreferences
    lateinit var toolbar: android.support.v7.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        btv = findViewById(R.id.bottom_navigation)
        btv.setOnNavigationItemSelectedListener(navListener)
        btv.setOnNavigationItemReselectedListener(navReListener)

        // Поддержка тулбара
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.hide()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, WalletsFragment())
            .commit()
    }

    private val navReListener = BottomNavigationView.OnNavigationItemReselectedListener {
        //TODO Обработка действий на перевыбранный пункт меню
        when (it.itemId) {
            R.id.nav_home -> {

            }
            R.id.nav_wallets -> {

            }
            R.id.nav_charts -> {

            }
            R.id.nav_settings -> {

            }
        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener {
        lateinit var selectedFragment: Fragment
        when (it.itemId) {
            R.id.nav_home -> {
                supportActionBar!!.hide()
                selectedFragment = SettingsFragment()
            }
            R.id.nav_wallets -> {
                supportActionBar!!.show()
                supportActionBar!!.title = "Счета"
                selectedFragment = WalletsFragment()
            }
            R.id.nav_charts -> {
                supportActionBar!!.show()
                supportActionBar!!.title = "Курсы"
                selectedFragment = PricesFragment()
            }
            R.id.nav_settings -> {
                supportActionBar!!.show()
                supportActionBar!!.title = "Настройки"
                selectedFragment = SettingsFragment()
            }
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, selectedFragment)
            .commit()

        true
    }
}
