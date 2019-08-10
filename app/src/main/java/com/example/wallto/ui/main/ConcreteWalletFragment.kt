package com.example.wallto.ui.main

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.wallto.R
import com.example.wallto.data.DataResponse
import com.example.wallto.data.Wallet
import com.example.wallto.network.RestApi
import com.example.wallto.network.services.WalletService
import com.example.wallto.ui.MainActivity
import com.example.wallto.utils.PrefsRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_concrete_wallet.*

class ConcreteWalletFragment : Fragment() {
    private lateinit var balance: TextView
    private lateinit var title: TextView
    private lateinit var address: TextView
    private lateinit var barcode: ImageView
    private lateinit var send: ImageButton
    private lateinit var history: ImageButton
    private lateinit var sendMoney: Button
    private lateinit var sendSum: EditText
    private lateinit var sendAddress: EditText

    private lateinit var scroll: ScrollView

    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var progressAddress: ProgressBar
    private lateinit var walletService: WalletService
    private lateinit var recyclerView: android.support.v7.widget.RecyclerView
    private lateinit var prefs: SharedPreferences

    private var addressToClip: String = ""


    private var id: Int? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_concrete_wallet, container, false)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val retrofit = RestApi.getInstance()
        walletService = retrofit.create(WalletService::class.java)

        //Прогресс бары
        progressAddress = v.findViewById(R.id.progressAddress)

        // Получение аргументов из списка счетов
        val titleString = arguments!!.getString("title")
        id = arguments!!.getInt("id")
        val balanceString = arguments!!.getString("balance")
        val typeString = arguments!!.getString("type")

        // Отображение названия кошелька
        title = v.findViewById(R.id.tvLoginMain)
        title.text = titleString

        balance = v.findViewById(R.id.tvBalance)
        balance.text = "$balanceString ${typeString!!.toUpperCase()}"

        barcode = v.findViewById(R.id.ivBarcode)
        address = v.findViewById(R.id.tvAddress)
        address.visibility = TextView.GONE
        address.setOnClickListener(onAddressClickListener)
        address.setOnLongClickListener(onAddressLongClickListener)

        scroll = v.findViewById(R.id.scrollWallet)

        send = v.findViewById(R.id.btnSend)
        send.setOnClickListener(onSendClickListener)

        history = v.findViewById(R.id.btnHistory)
        history.setOnClickListener(onHistoryClickListener)

        sendMoney = v.findViewById(R.id.btnSendMoney)
        sendMoney.setOnClickListener(onSendMoneyClickListener)

        sendSum = v.findViewById(R.id.etSum)
        sendAddress = v.findViewById(R.id.etAddress)

        getWalletInfo()

        return v
    }

    private val isInputsValid: Boolean get() = !TextUtils.isEmpty(sendSum.text) && !TextUtils.isEmpty(sendAddress.text)

    private val onSendMoneyClickListener = View.OnClickListener {
        if (isInputsValid) {
            sendSatoshi()
        } else {
            Toast.makeText(context, "Некорректные данные", Toast.LENGTH_SHORT).show()
        }
    }

    private val onHistoryClickListener = View.OnClickListener {
        scroll.smoothScrollTo(0, constraintHistory.top)
    }

    private val onSendClickListener = View.OnClickListener {
        scroll.smoothScrollTo(0, constraintSend.top)
    }

    private val onAddressClickListener = View.OnClickListener {
        Toast.makeText(context, "Долгое нажатие по адресу скопирует его в буфер", Toast.LENGTH_SHORT).show()
    }

    private val onAddressLongClickListener = View.OnLongClickListener {
        val clipboard = getSystemService(context!!, ClipboardManager::class.java)
        val clip = ClipData.newPlainText("Address", addressToClip)
        clipboard!!.primaryClip = clip
        Toast.makeText(context, "Скопировано в буфер", Toast.LENGTH_SHORT).show()
        true
    }

    private fun sendSatoshi() {
        walletService.send(id!!, sendAddress.text.toString(), sendSum.text.toString(), prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<DataResponse>() {
                override fun onSuccess(t: DataResponse) {
                    if (context != null) {
                        if (t.status == "success") {
                            tvSendTitle.visibility = TextView.GONE
                            tvSendSub.visibility = TextView.GONE
                            sendAddress.visibility = EditText.GONE
                            sendSum.visibility = EditText.GONE
                            sendMoney.visibility = Button.GONE
                            ivDone.visibility = ImageView.VISIBLE
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "Ошибка при отправке валюты: " + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    @SuppressLint("CheckResult")
    private fun getWalletInfo() {
        walletService.getWallet(id!!, prefs.getString(PrefsRepository.Keys.TOKEN.toString(), ""), "gnomes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<Wallet>() {
                override fun onSuccess(t: Wallet) {
                    if (context != null) {
                        // QR code
                        address.visibility = TextView.VISIBLE
                        address.text = t.address
                        addressToClip = t.address!!
                        paintBarcode(t.address)
                        progressAddress.visibility = ProgressBar.GONE


                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "Ошибка при загрузке: " + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun paintBarcode(s: String?) {
        val mfw = MultiFormatWriter()
        try {
            val bitMatrix = mfw.encode(s, BarcodeFormat.QR_CODE, 500, 500)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            barcode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Скрывает toolbar

        val act = activity as MainActivity
        act.supportActionBar!!.hide()
    }
}