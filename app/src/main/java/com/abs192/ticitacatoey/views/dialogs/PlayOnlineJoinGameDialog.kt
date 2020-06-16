package com.abs192.ticitacatoey.views.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.game.online.QRCodeScanner
import com.abs192.ticitacatoey.game.online.WebsocketManager

class PlayOnlineJoinGameDialog(
    private val activity: Activity,
    private var websocketManager: WebsocketManager,
    private var qrCodeScanner: QRCodeScanner
) : Dialog(activity) {

    private var progressBarDialogJoinGameOnline: ProgressBar? = null
    private var buttonScan: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_join_game_online)

        progressBarDialogJoinGameOnline = findViewById(R.id.progressBarDialogJoinGameOnline)
        buttonScan = findViewById(R.id.joinGameDialogScanQRCode)

        buttonScan?.setOnClickListener {
            qrCodeScanner.initiateScan()
        }
    }


}
