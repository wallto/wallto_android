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
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.model.Wallet
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.WalletService
import com.example.wallto.ui.main.walletslist.WalletsAdapter
import com.example.wallto.utils.PrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class WalletsFragment : Fragment() {
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var progress: ProgressBar
    private lateinit var walletService: WalletService
    private lateinit var recyclerView: android.support.v7.widget.RecyclerView
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_wallets, container, false)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val retrofit = RestApi.getInstance()
        walletService = retrofit.create(WalletService::class.java)

        recyclerView = v.findViewById(R.id.recyclerWallets)
        recyclerView.layoutManager = LinearLayoutManager(context)

        progress = v.findViewById(R.id.progressWallets)
        progress.visibility = ProgressBar.VISIBLE

        swipe = v.findViewById(R.id.swipeWallets)
        swipe.setOnRefreshListener {
            addItems()
        }

        addItems()



        return v
    }

    @SuppressLint("CheckResult")
    private fun addItems() {
        walletService.getWallets(prefs.getString(PrefsHelper.TOKEN, ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<ArrayList<Wallet>>() {
                override fun onSuccess(t: ArrayList<Wallet>) {
                    if (context != null) {
                        displayData(t)
                        swipe.isRefreshing = false
                        progress.visibility = ProgressBar.INVISIBLE
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "Ошибка при загрузке: " + e.message, Toast.LENGTH_SHORT).show()
                    System.out.println("Ошибка wallets: " + e.message)
                }

            })
    }

    private fun displayData(t: ArrayList<Wallet>) {
        val adapter = WalletsAdapter(t, context!!, fragmentManager)
        recyclerView.adapter = adapter
    }
}