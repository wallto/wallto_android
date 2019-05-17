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
    private lateinit var btnAuth: Button
    private lateinit var btnRegister: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_start, container, false)

        btnAuth = v.findViewById(R.id.btnAuth)
        btnRegister = v.findViewById(R.id.btnRegister)

        btnAuth.setOnClickListener(onAuthClickListener)
        btnRegister.setOnClickListener(onRegisterClickListener)

        return v
    }

    private val onAuthClickListener = View.OnClickListener {
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authContainer, AuthFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    private  val onRegisterClickListener = View.OnClickListener {
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authContainer, RegistrationFragment())
            ?.addToBackStack(null)
            ?.commit()
    }
}