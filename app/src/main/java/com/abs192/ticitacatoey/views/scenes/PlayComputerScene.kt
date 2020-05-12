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
    private var buttonPlayComputerDifficult: Button? = null

    override fun initScene() {
        newGameLayoutComputer = layoutInflater.inflate(R.layout.layout_new_game_computer, null)
        addChildInMainLayout(newGameLayoutComputer!!)
        fadeIn()
        buttonPlayComputerEasy = newGameLayoutComputer?.findViewById(R.id.newGameButtonComputer)
        buttonPlayComputerDifficult = newGameLayoutComputer?.findViewById(R.id.newGameButtonHuman)

        buttonPlayComputerEasy?.setOnClickListener {
            fadeOut()
            listener.onEasyClicked()
        }
        buttonPlayComputerDifficult?.setOnClickListener {
            fadeOut()
            listener.onDifficultClick()
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
            buttonPlayComputerEasy
        )
        animatorUtil.fadeIn(newGameLayoutComputer!!, AnimatorUtil.Duration.LONG, null)
    }


    override fun fadeInFast() {
        enableDisableViews(
            true,
            newGameLayoutComputer,
            buttonPlayComputerEasy,
            buttonPlayComputerEasy
        )
        animatorUtil.fadeIn(newGameLayoutComputer!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(
            false,
            newGameLayoutComputer,
            buttonPlayComputerEasy,
            buttonPlayComputerEasy
        )
        animatorUtil.fadeOut(newGameLayoutComputer!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    interface PlayComputerButtonClickListener {
        fun onEasyClicked()
        fun onDifficultClick()
    }

}