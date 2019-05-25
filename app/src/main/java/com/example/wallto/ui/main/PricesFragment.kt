package com.example.wallto.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class PricesFragment : Fragment() {
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var progress: ProgressBar
    private lateinit var infoService: InfoService
    private lateinit var recyclerView: android.support.v7.widget.RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_prices, container, false)

        val retrofit = PriceApi.getInstance()
        infoService = retrofit.create(InfoService::class.java)

        recyclerView = v.findViewById(R.id.recyclerPrices)
        recyclerView.layoutManager = LinearLayoutManager(context)

        progress = v.findViewById(R.id.progressPrices)
        progress.visibility = ProgressBar.VISIBLE

        swipe = v.findViewById(R.id.swipePrices)
        swipe.setOnRefreshListener {
            addItems()
        }

        addItems()

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val act = activity as MainActivity
        act.supportActionBar!!.show()
        act.supportActionBar!!.title = "Курсы"
    }

    @SuppressLint("CheckResult")
    private fun addItems() {
        infoService.prices
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<ArrayList<Currency>>() {
                override fun onSuccess(t: ArrayList<Currency>) {
                    if (context != null) {  // Сделано для того, чтобы пресечь попытку записать данные в уничтоженный фрагмент
                        val prices = ArrayList<Currency>()
                        for (i in 0..5) {
                            prices.add(t[i])
                        }
                        displayData(prices)
                        swipe.isRefreshing = false
                        progress.visibility = ProgressBar.INVISIBLE
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "Ошибка при загрузке: " + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun displayData(prices: ArrayList<Currency>) {
        val adapter = PriceAdapter(prices, context!!)
        recyclerView.adapter = adapter
    }
}