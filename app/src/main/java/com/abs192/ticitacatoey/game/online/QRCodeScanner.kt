package com.abs192.ticitacatoey.game.online

import android.app.Activity
import com.google.zxing.integration.android.IntentIntegrator

class QRCodeScanner(activity: Activity) {

    private var scanner = IntentIntegrator(activity)

    init {
        scanner.setOrientationLocked(true)
    }

    private var qrCodeScanListener: QRCodeScanListener? = null

    fun setOnScanListener(qrCodeScanListener: QRCodeScanListener) {
        this.qrCodeScanListener = qrCodeScanListener
    }

    fun initiateScan() {
        scanner.initiateScan()
    }

    fun onScanned(contents: String?) {
        qrCodeScanListener?.onScanned(contents)
    }

    interface QRCodeScanListener {
        fun onScanned(contents: String?)
    }
}