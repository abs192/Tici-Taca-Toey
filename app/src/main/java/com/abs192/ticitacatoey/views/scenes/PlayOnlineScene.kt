package com.abs192.ticitacatoey.views.scenes

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.abs192.ticitacatoey.MainActivity
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.game.`interface`.GameEventResponseListener
import com.abs192.ticitacatoey.game.`interface`.GameJoinFailedError
import com.abs192.ticitacatoey.game.online.QRCodeScanner
import com.abs192.ticitacatoey.game.online.WebsocketManager
import com.abs192.ticitacatoey.views.AnimatorUtil
import com.abs192.ticitacatoey.views.dialogs.PlayOnlineHostGameDialog
import com.abs192.ticitacatoey.views.dialogs.PlayOnlineJoinGameDialog

class PlayOnlineScene(
    private val activity: MainActivity,
    private val qrCodeScanner: QRCodeScanner,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout,
    clickListener: OnPlayOnlineButtonClickListener
) : TTTScene(activity, layoutInflater, mainLayout, SceneType.NEW_GAME),
    GameEventResponseListener, QRCodeScanner.QRCodeScanListener {

    private var playOnlineScene: View? = null

    private var buttonHost: Button? = null
    private var buttonJoin: Button? = null

    private var activeDialog: Dialog? = null

    private var websocketManager: WebsocketManager? = null

    override fun initScene() {
        playOnlineScene = layoutInflater.inflate(R.layout.layout_play_online, null)
        addChildInMainLayout(playOnlineScene!!)
        fadeIn()

        buttonHost = playOnlineScene?.findViewById(R.id.onlineButtonHost)
        buttonJoin = playOnlineScene?.findViewById(R.id.onlineButtonJoin)

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_DENIED
        ) {
            Log.d("permission", "denied. requesting")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.INTERNET),
                223
            )
        } else {
            Log.d("permission", "granted already")
        }

        websocketManager = WebsocketManager(activity, "abs192", this)
        websocketManager?.connect()
        qrCodeScanner.setOnScanListener(this)

        buttonHost?.setOnClickListener {
            if (websocketManager?.startGame()!!) {
                activeDialog?.dismiss()
                activeDialog = PlayOnlineHostGameDialog(activity, websocketManager!!)
                activeDialog?.show()
            }
        }

        buttonJoin?.setOnClickListener {
            activeDialog?.dismiss()

            activeDialog = PlayOnlineJoinGameDialog(activity, websocketManager!!, qrCodeScanner)
            activeDialog?.show()
        }
    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(playOnlineScene)
    }

    override fun fadeIn() {
        enableDisableViews(
            true,
            playOnlineScene,
            buttonHost,
            buttonJoin
        )
        animatorUtil.fadeIn(playOnlineScene!!, AnimatorUtil.Duration.LONG, null)
    }


    override fun fadeInFast() {
        enableDisableViews(
            true,
            playOnlineScene,
            buttonHost,
            buttonJoin
        )
        animatorUtil.fadeIn(playOnlineScene!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(
            false,
            playOnlineScene,
            buttonHost,
            buttonJoin
        )
        animatorUtil.fadeOut(playOnlineScene!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    interface OnPlayOnlineButtonClickListener {
        fun onHostGameClicked()
        fun onJoinGameClicked()
    }

    override fun onScanned(contents: String?) {
        Log.d(javaClass.simpleName, "on scanned $contents")
        websocketManager?.joinGame(contents!!.trim())
    }

    fun joinGame(gameId: String) {
        websocketManager?.joinGame(gameId)
    }


    override fun onRegisterPlayerSuccess() {
    }

    override fun onRegisterPlayerFailed() {
    }

    override fun onGameJoinSuccess() {
    }

    override fun onGameJoinFailed(e: GameJoinFailedError) {
    }

    override fun onGameStarted(
        playerId: String,
        websocketManager: WebsocketManager,
        gameStartedResponseMessage: WebsocketManager.GameStartedResponseMessage
    ) {
        activity.backgroundCanvas?.normalTint()
        activity.playGameOnlineScene(playerId, websocketManager, gameStartedResponseMessage)
        fadeOut()
        activeDialog?.dismiss()
    }

    override fun onStartGameFailed() {
    }

    override fun onMoveMadeSuccess() {
    }

    override fun onMoveMadeFailed() {
    }

    override fun onFailedToConnect() {
        activity.backgroundCanvas?.showErrorTint()
    }
}