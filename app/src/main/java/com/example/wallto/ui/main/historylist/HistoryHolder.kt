package com.example.wallto.ui.main.historylist

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.wallto.R
import com.example.wallto.data.History

class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var value: TextView = itemView.findViewById(R.id.tvValue)
    private var status: TextView = itemView.findViewById(R.id.tvStatus)

    @SuppressLint("SetTextI18n")
    fun bind(history: History) {

        value.text = history.value
        status.text = history.send_colour

    }
}