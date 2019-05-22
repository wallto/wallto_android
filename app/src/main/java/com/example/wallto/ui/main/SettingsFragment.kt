package com.example.wallto.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.wallto.R

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        val text: TextView = v.findViewById(R.id.textView3)
        text.setOnClickListener {
            Toast.makeText(context, "dsds", Toast.LENGTH_SHORT).show()
        }
        return v
    }
}