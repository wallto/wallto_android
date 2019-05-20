package com.example.wallto.ui.main.walletslist

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.Image
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.wallto.R
import com.example.wallto.model.Currency
import com.example.wallto.model.Wallet
import java.text.DecimalFormat
import java.util.*

class WalletHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var image: ImageView = itemView.findViewById(R.id.ivIcon)
    private var name: TextView = itemView.findViewById(R.id.tvWalletName)
    private var symbol: TextView = itemView.findViewById(R.id.tvSymbol)
    private var price: TextView = itemView.findViewById(R.id.tvPrice)

    @SuppressLint("SetTextI18n")
    fun bind(wallet: Wallet) {
        val decimalFormat = DecimalFormat("##0.00")

        name.text = wallet.title
        symbol.text = wallet.type!!.toUpperCase()
        price.text = "${wallet.balance} ${wallet.type!!.toUpperCase()}"
        image.setImageDrawable(
            image.context.getDrawable(
                image.context.resources.getIdentifier(
                    wallet.type!!.toLowerCase(), "drawable", image.context.packageName
                )
            )
        )
    }
}