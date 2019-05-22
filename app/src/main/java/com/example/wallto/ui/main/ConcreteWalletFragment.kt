package com.example.wallto.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wallto.R
import com.example.wallto.ui.MainActivity

class ConcreteWalletFragment : Fragment() {
    private lateinit var title: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_concrete_wallet, container, false)
        val string = arguments!!.getString("title")
        title = v.findViewById(R.id.tvTitle)
        title.text = string

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Скрывает toolbar

        val act = activity as MainActivity
        act.supportActionBar!!.hide()
    }
}