package com.example.wallto.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.wallto.R
import com.example.wallto.data.User
import com.example.wallto.data.Wallet
import com.example.wallto.data.body.TokenBody
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.TokenService
import com.example.wallto.network.services.WalletService
import com.example.wallto.ui.MainActivity
import com.example.wallto.ui.pin.PinCodeActivity
import com.example.wallto.ui.main.walletslist.WalletsAdapter
import com.example.wallto.utils.PrefsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class WalletsFragment : Fragment() {
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var progress: ProgressBar
    private lateinit var walletService: WalletService
    private lateinit var tokenService: TokenService
    private lateinit var recyclerView: android.support.v7.widget.RecyclerView
    private lateinit var prefs: SharedPreferences

    private var TAG = this.javaClass.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_wallets, container, false)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val retrofit = RestApi.getInstance()
        walletService = retrofit.create(WalletService::class.java)
        tokenService = retrofit.create(TokenService::class.java)

        recyclerView = v.findViewById(R.id.recyclerWallets)
        recyclerView.layoutManager = LinearLayoutManager(context)

        progress = v.findViewById(R.id.progressWallets)
        progress.visibility = ProgressBar.VISIBLE

        swipe = v.findViewById(R.id.swipeWallets)
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
        act.supportActionBar!!.title = "Счета"
    }

    @SuppressLint("CheckResult")
    private fun addItems() {
        walletService.getWallets(prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<ArrayList<Wallet>>() {
                override fun onSuccess(t: ArrayList<Wallet>) {
                    if (context != null) {
                        checkTokenValid()
                        displayData(t)
                        swipe.isRefreshing = false
                        progress.visibility = ProgressBar.INVISIBLE
                    }
                }

                override fun onError(e: Throwable) {
                    if (prefs.getString(PrefsRepository.Keys.PIN.toString(), "") == "") {
                        refreshToken()
                    } else {
                        val intent = Intent(context, PinCodeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    Log.e(TAG, "Ошибка wallets: ", e)
                }

            })
    }

    private fun checkTokenValid() {
        tokenService.checkValid(prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    if (t.message == "ok") {
                        Log.e(TAG, "Token is still valid")
                    }
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "Ошибка checkTokenValid: ", e)
                    if (prefs.getString(PrefsRepository.Keys.PIN.toString(), "") == "") {
                        refreshToken()
                    } else {
                        val intent = Intent(context, PinCodeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            })
    }

    private fun refreshToken() {
        val tokenBody = TokenBody(prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""))
        tokenService.refreshToken(tokenBody, "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<User>() {
                override fun onSuccess(t: User) {
                    Log.e(TAG, "Ответ на extend: " + t.user_token)
                    updateTokenData(t)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "Ошибка refreshToken: ", e)
                }
            })
    }

    private fun updateTokenData(user: User) {
        val ed = prefs.edit()
        ed.putString(PrefsRepository.Keys.TOKEN.toString(), user.user_token)
        ed.apply()
    }

    private fun displayData(t: ArrayList<Wallet>) {
        val adapter = WalletsAdapter(t, context!!, fragmentManager)
        recyclerView.adapter = adapter
    }
}