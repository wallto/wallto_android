package com.example.wallto.ui.auth

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.wallto.R
import com.example.wallto.ui.auth.AuthFragment

class StartFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_start, container, false)

        val button = v.findViewById<Button>(R.id.btnAuth)

        button.setOnClickListener {
            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.authContainer, AuthFragment())
                ?.commit()
        }

        return v
    }
}