package com.example.wallto.ui.main.historylist

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.wallto.R
import com.example.wallto.data.History
import kotlin.collections.ArrayList

class HistoryAdapter(
    private var historys: ArrayList<History>,
    private var context: Context
) : RecyclerView.Adapter<HistoryHolder>() {

    @SuppressLint("PrivateResource")
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HistoryHolder {
        val inflater = LayoutInflater.from(p0.context)
        val view = inflater.inflate(R.layout.li_wallet, p0, false)

        return HistoryHolder(view)
    }

    override fun getItemCount(): Int {
        return historys.size
    }

    override fun onBindViewHolder(p0: HistoryHolder, p1: Int) {
        p0.bind(historys[p1])
    }

}