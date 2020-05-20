package com.abs192.ticitacatoey.views.scenes

import android.app.Activity
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.MainActivity
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.ToastDialog
import com.abs192.ticitacatoey.bluetooth.BTManager
import com.abs192.ticitacatoey.views.AnimatorUtil

class BluetoothConnectScene(
    activity: MainActivity,
    btManager: BTManager,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout,
    var listener: BluetoothConnectClickListener
) : TTTScene(activity, layoutInflater, mainLayout, SceneType.NEW_GAME) {

    private var bluetoothConnectScene: View? = null
    private var bluetoothConnectHostButton: Button? = null
    private var bluetoothConnectSearchButton: Button? = null
    private var isBTEnabled = false

    init {
        if (!btManager.isSupportedOnDevice()) {
            ToastDialog(activity, "Bluetooth is not supported on this device").show()
            activity.backgroundCanvas?.showErrorTint()
        } else {
            activity.backgroundCanvas?.showBluetoothTint()
            btManager.requestPermissions(activity)
        }
        btManager.enableBT(activity)
    }

    override fun initScene() {
        bluetoothConnectScene = layoutInflater.inflate(R.layout.layout_bt_connect, null)
        addChildInMainLayout(bluetoothConnectScene!!)

        bluetoothConnectHostButton =
            bluetoothConnectScene?.findViewById(R.id.bluetoothConnectHostButton)
        bluetoothConnectSearchButton =
            bluetoothConnectScene?.findViewById(R.id.bluetoothConnectSearchButton)
        fadeIn()

        bluetoothConnectHostButton?.setOnClickListener {

        }

        bluetoothConnectHostButton?.setOnClickListener {

        }
    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(bluetoothConnectScene)
    }

    override fun fadeIn() {
        enableDisableViews(
            true,
            bluetoothConnectScene
        )
        animatorUtil.fadeIn(bluetoothConnectScene!!, AnimatorUtil.Duration.LONG, null)
    }


    override fun fadeInFast() {
        enableDisableViews(
            true,
            bluetoothConnectScene
        )
        animatorUtil.fadeIn(bluetoothConnectScene!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(
            false,
            bluetoothConnectScene
        )
        animatorUtil.fadeOut(bluetoothConnectScene!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    interface BluetoothConnectClickListener {
        fun onHostGame()
        fun onSearchGame()
    }

    interface BTManagerListener {
        fun onEnabled()
    }

}