package com.abs192.ticitacatoey.views.scenes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.views.AnimatorUtil

class PlayComputerScene(
    context: Context,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout,
    var listener: PlayComputerButtonClickListener
) : TTTScene(context, layoutInflater, mainLayout, SceneType.NEW_GAME) {

    private var newGameLayoutComputer: View? = null
    private var buttonPlayComputerEasy: Button? = null
    private var buttonPlayComputerHard: Button? = null

    override fun initScene() {
        newGameLayoutComputer = layoutInflater.inflate(R.layout.layout_play_computer, null)
        addChildInMainLayout(newGameLayoutComputer!!)
        fadeIn()
        buttonPlayComputerEasy = newGameLayoutComputer?.findViewById(R.id.newGameButtonComputerEasy)
        buttonPlayComputerHard = newGameLayoutComputer?.findViewById(R.id.newGameButtonComputerHard)

        buttonPlayComputerEasy?.setOnClickListener {
            fadeOut()
            listener.onEasyClicked()
        }
        buttonPlayComputerHard?.setOnClickListener {
            fadeOut()
            listener.onHardClicked()
        }

    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(newGameLayoutComputer)
    }

    override fun fadeIn() {
        enableDisableViews(
            true,
            newGameLayoutComputer,
            buttonPlayComputerEasy,
            buttonPlayComputerHard
        )
        animatorUtil.fadeIn(newGameLayoutComputer!!, AnimatorUtil.Duration.LONG, null)
    }


    override fun fadeInFast() {
        enableDisableViews(
            true,
            newGameLayoutComputer,
            buttonPlayComputerEasy,
            buttonPlayComputerHard
        )
        animatorUtil.fadeIn(newGameLayoutComputer!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(
            false,
            newGameLayoutComputer,
            buttonPlayComputerEasy,
            buttonPlayComputerHard
        )
        animatorUtil.fadeOut(newGameLayoutComputer!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    interface PlayComputerButtonClickListener {
        fun onEasyClicked()
        fun onHardClicked()
    }

}