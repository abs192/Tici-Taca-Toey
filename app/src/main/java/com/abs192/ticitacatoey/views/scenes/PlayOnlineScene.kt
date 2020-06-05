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

class PlayOnlineScene(
    activity: MainActivity,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout
) : TTTScene(activity, layoutInflater, mainLayout, SceneType.NEW_GAME) {

    private var playOnlineScene: View? = null

    override fun initScene() {
        playOnlineScene = layoutInflater.inflate(R.layout.layout_play_online, null)
        addChildInMainLayout(playOnlineScene!!)
        fadeIn()
    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(playOnlineScene)
    }

    override fun fadeIn() {
        enableDisableViews(
            true,
            playOnlineScene
        )
        animatorUtil.fadeIn(playOnlineScene!!, AnimatorUtil.Duration.LONG, null)
    }


    override fun fadeInFast() {
        enableDisableViews(
            true,
            playOnlineScene
        )
        animatorUtil.fadeIn(playOnlineScene!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(
            false,
            playOnlineScene
        )
        animatorUtil.fadeOut(playOnlineScene!!, AnimatorUtil.Duration.MEDIUM, null)
    }
}