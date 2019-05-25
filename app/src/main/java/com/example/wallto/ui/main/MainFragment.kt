package com.example.wallto.ui.main

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import carbon.widget.ExpandableRecyclerView
import carbon.widget.RecyclerView
import com.example.wallto.R
import com.example.wallto.model.Currency
import com.example.wallto.model.PriceResponse
import com.example.wallto.network.PriceApi
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.AuthService
import com.example.wallto.network.services.InfoService
import com.example.wallto.ui.MainActivity
import com.example.wallto.ui.main.pricelist.PriceAdapter
import com.example.wallto.utils.PrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

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