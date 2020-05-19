package com.abs192.ticitacatoey.views.scenes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.game.*
import com.abs192.ticitacatoey.types.GameInfo
import com.abs192.ticitacatoey.views.AnimatorUtil
import com.abs192.ticitacatoey.views.canvas.GridCanvas

class GameScene(
    private val gameMode: GameManager.GameMode,
    context: Context,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout
) : TTTScene(context, layoutInflater, mainLayout, SceneType.NEW_GAME) {

    private var gameLayout: View? = null
    private var gridCanvas: GridCanvas? = null
    private lateinit var gameInfo: GameInfo

    override fun initScene() {
        gameLayout = layoutInflater.inflate(R.layout.layout_game, null)
        addChildInMainLayout(gameLayout!!)
        fadeIn()

        gridCanvas = gameLayout?.findViewById(R.id.gridCanvas)
        val game = Game()
        val gameManager = GameManager(gameMode, game, gameInfo, gridCanvas!!)
        gameManager.initialize()
    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(gameLayout)
    }

    override fun fadeIn() {
        enableDisableViews(
            true,
            gameLayout
        )
        animatorUtil.fadeIn(gameLayout!!, AnimatorUtil.Duration.LONG, null)
    }


    override fun fadeInFast() {
        enableDisableViews(
            true,
            gameLayout
        )
        animatorUtil.fadeIn(gameLayout!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(
            false,
            gameLayout
        )
        animatorUtil.fadeOut(gameLayout!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    fun initGameInfo(gameInfo: GameInfo) {
        this.gameInfo = gameInfo
    }

}