package com.example.wallto.ui.main.walletslist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.example.wallto.R
import com.example.wallto.model.Wallet
import com.example.wallto.ui.main.ConcreteWalletFragment
import kotlin.collections.ArrayList

class WalletsAdapter(
    private var wallets: ArrayList<Wallet>,
    private var context: Context,
    private var fragmentManager: FragmentManager?
) : RecyclerView.Adapter<WalletHolder>() {

    @SuppressLint("PrivateResource")
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WalletHolder {
        val inflater = LayoutInflater.from(p0.context)
        val view = inflater.inflate(R.layout.li_wallet, p0, false)
        val walletHolder = WalletHolder(view)

        view.setOnClickListener {
            val position = walletHolder.adapterPosition
            val wallet = wallets[position]
            val fragment = ConcreteWalletFragment()
            val args = Bundle()
            args.putString("title", wallet.title)
            args.putInt("id", wallet.id!!)
            fragment.arguments = args

            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.mainContainer, fragment)
                ?.addToBackStack("")
                ?.commit()
        }

        return walletHolder
    }

    override fun getItemCount(): Int {
        return wallets.size
    }

    override fun onBindViewHolder(p0: WalletHolder, p1: Int) {
        p0.bind(wallets[p1])
    }

}