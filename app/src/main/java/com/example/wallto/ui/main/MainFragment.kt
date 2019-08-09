package com.example.wallto.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.wallto.R
import com.example.wallto.network.PriceApi
import com.example.wallto.network.services.InfoService
import com.example.wallto.ui.MainActivity
import com.example.wallto.utils.PrefsHelper

class MainFragment : Fragment() {
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var progress: ProgressBar
    private lateinit var infoService: InfoService
    private lateinit var recyclerView: android.support.v7.widget.RecyclerView
    private lateinit var prefs: SharedPreferences

    private lateinit var login: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val retrofit = PriceApi.getInstance()
        infoService = retrofit.create(InfoService::class.java)

        login = v.findViewById(R.id.tvLoginMain)
        login.text = prefs.getString(PrefsHelper.LOGIN, "")

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val act = activity as MainActivity
        act.supportActionBar!!.hide()
    }
}