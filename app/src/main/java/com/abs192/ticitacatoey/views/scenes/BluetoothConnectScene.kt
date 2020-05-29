package com.abs192.ticitacatoey.views.scenes

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.MainActivity
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.views.dialogs.ToastDialog
import com.abs192.ticitacatoey.bluetooth.BTService
import com.abs192.ticitacatoey.views.AnimatorUtil
import com.abs192.ticitacatoey.views.dialogs.BTHostDialog
import com.abs192.ticitacatoey.views.dialogs.BTSearchDialog

class BluetoothConnectScene(
    private val activity: MainActivity,
    private val btService: BTService,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout,
    var listener: BluetoothConnectClickListener
) : TTTScene(activity, layoutInflater, mainLayout, SceneType.NEW_GAME) {

    private var bluetoothConnectScene: View? = null
    private var bluetoothConnectHostButton: Button? = null
    private var bluetoothConnectSearchButton: Button? = null

    init {
        btService.onStart()
        if (!btService.isSupportedOnDevice()) {
            ToastDialog(
                activity,
                "Bluetooth is not supported on this device"
            ).show()
            activity.backgroundCanvas?.showErrorTint()
        } else {
            activity.backgroundCanvas?.showBluetoothTint()
            btService.requestPermissions()
        }
        btService.showEnableDialog()
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
            BTHostDialog(activity, btService).show()
        }

        bluetoothConnectSearchButton?.setOnClickListener {
            BTSearchDialog(activity, btService).show()
        }
    }

    override fun backPressed() {
        btService.onStop()
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