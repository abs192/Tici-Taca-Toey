package com.abs192.ticitacatoey.views.scenes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.views.AnimatorUtil

class NewGameScene(
    context: Context,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout,
    var listener: NewGameButtonClickListener
) : TTTScene(context, layoutInflater, mainLayout, SceneType.NEW_GAME) {

    private var newGameLayout: View? = null
    private var buttonPlayComputer: Button? = null
    private var buttonPlayHuman: Button? = null
    private var buttonSettings: Button? = null

    override fun initScene() {
        newGameLayout = layoutInflater.inflate(R.layout.layout_new_game, null)
        addChildInMainLayout(newGameLayout!!)
        fadeIn()
        buttonPlayComputer = newGameLayout?.findViewById(R.id.newGameButtonComputer)
        buttonPlayHuman = newGameLayout?.findViewById(R.id.newGameButtonHuman)
        buttonSettings = newGameLayout?.findViewById(R.id.newGameButtonSettings)

        buttonPlayComputer?.setOnClickListener {
            fadeOut()
            listener.onComputerClicked()
        }
        buttonPlayHuman?.setOnClickListener {
            fadeOut()
            listener.onHumanClicked()
        }
        buttonSettings?.setOnClickListener {
            fadeOut()
            listener.onSettingsClicked()
        }
    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(newGameLayout)
    }

    override fun fadeIn() {
        enableDisableViews(true, newGameLayout, buttonPlayComputer, buttonPlayHuman, buttonSettings)
        animatorUtil.fadeIn(newGameLayout!!, AnimatorUtil.Duration.LONG, null)
    }

    override fun fadeInFast() {
        enableDisableViews(true, newGameLayout, buttonPlayComputer, buttonPlayHuman, buttonSettings)
        animatorUtil.fadeIn(newGameLayout!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(
            false,
            newGameLayout,
            buttonPlayComputer,
            buttonPlayHuman,
            buttonSettings
        )
        animatorUtil.fadeOut(newGameLayout!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    interface NewGameButtonClickListener {
        fun onComputerClicked()
        fun onHumanClicked()
        fun onSettingsClicked()
    }

}