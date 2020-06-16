package com.abs192.ticitacatoey.views.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.game.online.WebsocketManager
import net.glxn.qrgen.android.QRCode

class PlayOnlineHostGameDialog(
    context: Context,
    var websocketManager: WebsocketManager
) : Dialog(context) {

    private var qrCodeImageView: ImageView? = null
    private var buttonShareLink: Button? = null
    private var progressBarDialogHostGameOnline: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_host_game_online)

        qrCodeImageView = findViewById(R.id.hostGameDialogQRCode)
        progressBarDialogHostGameOnline = findViewById(R.id.progressBarDialogHostGameOnline)
        buttonShareLink = findViewById(R.id.hostGameDialogShareLink)

        Log.d(javaClass.simpleName, "websocketManager.gameId ${websocketManager.gameId}")
        Handler().postDelayed({
            if (websocketManager.gameId != "") {
                progressBarDialogHostGameOnline?.visibility = View.INVISIBLE
                Log.d(javaClass.simpleName, "websocketManager.gameId ${websocketManager.gameId}")
                val bitmap = QRCode.from(websocketManager.gameId).bitmap()
                qrCodeImageView?.setImageBitmap(bitmap)
            }
        }, 1000)

        buttonShareLink?.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "https://ticitacatoey.com/joinGame?gameId=${websocketManager.gameId}"
                )
                putExtra(Intent.EXTRA_TITLE, "Join ${websocketManager.playerName} game")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }
}


