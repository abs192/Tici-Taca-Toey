package com.abs192.ticitacatoey.views.scenes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.views.AnimatorUtil

class PlayHumanScene(
    context: Context,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout,
    var listener: PlayHumanButtonClickListener
) : TTTScene(context, layoutInflater, mainLayout, SceneType.NEW_GAME) {

    private var newGameLayoutHuman: View? = null
    private var buttonPlayHumanLocal: Button? = null
    private var buttonPlayHumanBluetooth: Button? = null
    private var buttonPlayHumanOnline: Button? = null

    override fun initScene() {
        newGameLayoutHuman = layoutInflater.inflate(R.layout.layout_play_human, null)
        addChildInMainLayout(newGameLayoutHuman!!)
        fadeIn()
        buttonPlayHumanLocal = newGameLayoutHuman?.findViewById(R.id.newGameButtonHumanLocal)
        buttonPlayHumanBluetooth =
            newGameLayoutHuman?.findViewById(R.id.newGameButtonHumanBluetooth)
        buttonPlayHumanOnline = newGameLayoutHuman?.findViewById(R.id.newGameButtonHumanOnline)

        //TODO: Remove when bt mode is fixed
        buttonPlayHumanBluetooth?.visibility = View.GONE

        buttonPlayHumanLocal?.setOnClickListener {
            fadeOut()
            listener.onLocalClicked()
        }
        buttonPlayHumanBluetooth?.setOnClickListener {
            fadeOut()
            listener.onBluetoothClicked()
        }
        buttonPlayHumanOnline?.setOnClickListener {
            fadeOut()
            listener.onOnlineClicked()
        }

    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(newGameLayoutHuman)
    }

    override fun fadeIn() {
        enableDisableViews(
            true,
            newGameLayoutHuman,
            buttonPlayHumanLocal,
            buttonPlayHumanBluetooth,
            buttonPlayHumanOnline
        )
        animatorUtil.fadeIn(newGameLayoutHuman!!, AnimatorUtil.Duration.LONG, null)
    }


    override fun fadeInFast() {
        enableDisableViews(
            true,
            newGameLayoutHuman,
            buttonPlayHumanLocal,
            buttonPlayHumanBluetooth,
            buttonPlayHumanOnline
        )
        animatorUtil.fadeIn(newGameLayoutHuman!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(
            false,
            newGameLayoutHuman,
            buttonPlayHumanLocal,
            buttonPlayHumanBluetooth,
            buttonPlayHumanOnline
        )
        animatorUtil.fadeOut(newGameLayoutHuman!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    interface PlayHumanButtonClickListener {
        fun onLocalClicked()
        fun onBluetoothClicked()
        fun onOnlineClicked()
    }

}