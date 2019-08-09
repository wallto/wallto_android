package com.example.wallto.ui.main.pricelist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.wallto.R
import com.example.wallto.data.Currency
import kotlin.collections.ArrayList

class PriceAdapter(private var prices: ArrayList<Currency>, private var context: Context) : RecyclerView.Adapter<PriceHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PriceHolder {
        val inflater = LayoutInflater.from(p0.context)
        val view = inflater.inflate(R.layout.li_price, p0, false)
        return PriceHolder(view)
    }

    override fun getItemCount(): Int {
        return prices.size
    }

    override fun onBindViewHolder(p0: PriceHolder, p1: Int) {
        p0.bind(prices[p1])
    }

}