package com.abs192.ticitacatoey.views.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import com.abs192.ticitacatoey.MainActivity
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.bluetooth.BTService


class BTHostDialog(private val activity: MainActivity, private val btService: BTService) :
    Dialog(activity) {

    private var textView: TextView? = null
    private var textViewDeviceName: TextView? = null
    private var textViewTimer: TextView? = null
    private var progressBar: ProgressBar? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setCanceledOnTouchOutside(false)
        setCancelable(false)
        setContentView(R.layout.layout_bt_host)

        textView = findViewById(R.id.bt_host_text)
        textViewDeviceName = findViewById(R.id.bt_host_device_name)
        textViewTimer = findViewById(R.id.bt_host_timer_text)
        progressBar = findViewById(R.id.bt_host_progressBar)
        progressBar?.visibility = View.INVISIBLE

        textViewDeviceName?.text = "Device name: ${btService.getDeviceName()}"

        btService.makeDiscoverable(object : BTService.MakeDiscoverableCallback {
            override fun onStarted() {
                activity.backgroundCanvas?.showBluetoothTint()
                progressBar?.visibility = View.VISIBLE
                countDownTimer.start()
            }

            override fun onCancelled() {
                activity.backgroundCanvas?.showErrorTint()
                dismiss()
                ToastDialog(activity, "Can't host without making phone discoverable").show()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        btService.stop()
        this.dismiss()
    }


    val countDownTimer = object : CountDownTimer(300000, 1000) {
        override fun onFinish() {
            progressBar?.visibility = View.INVISIBLE
        }

        override fun onTick(millisUntilFinished: Long) {
            val secs = millisUntilFinished / 1000
            Log.d(javaClass.simpleName, "$secs secs")
            activity.runOnUiThread {
                textViewTimer?.text = "${secs} secs"
            }
        }

    }
}
