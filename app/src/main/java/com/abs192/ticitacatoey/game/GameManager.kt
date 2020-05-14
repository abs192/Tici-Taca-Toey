package com.abs192.ticitacatoey.game

import android.os.Handler
import android.util.Log
import com.abs192.ticitacatoey.types.GameInfo
import com.abs192.ticitacatoey.views.canvas.GridCanvas

class GameManager(var game: Game, var gameInfo: GameInfo, var gameGrid: GridCanvas) :
    MoveInputListener {

    private var postMoveInputListener: PostMoveEventListener? = null


    init {
        gameGrid.registerMoveListener(this)
        postMoveInputListener = gameGrid
        startNewGame()
    }

    private fun resetPlayerXOs() {
        val xo = game.randomMove()
        val ox = if (xo == "x") "o" else "x"
        gameInfo.player1.assignXO(xo)
        gameInfo.player2.assignXO(ox)
    }

    override fun makeMove(x: Int, y: Int) {
        Log.d(javaClass.simpleName, "toMove ${game.getToMove()}")
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val xo = game.getXO(i, j)
                Log.d(javaClass.simpleName, "$i $j $xo")
            }
        }

        var checkEnd = game.checkEnd()
        if (checkEnd != GameState.ONGOING) {
            return
        }

        val moveDone: Boolean
        if (game.getToMove() == "o") {
            moveDone = game.makeMoveO(x, y)
        } else {
            moveDone = game.makeMoveX(x, y)
        }
        if (moveDone)
            postMoveInputListener?.moveDone()
        else
            postMoveInputListener?.moveRejected()

        checkEnd = game.checkEnd()
        if (checkEnd != GameState.ONGOING) {
            updateScore(checkEnd)
            postMoveInputListener?.gameEnd(checkEnd)
            // restart after 2 secs
            Handler().postDelayed({
                game.resetBoard()
                startNewGame()
            }, 2000)
        }
    }

    private fun updateScore(checkEnd: GameState) {
        when (checkEnd) {
            GameState.X_WIN ->
                if (gameInfo.player1.xo == "x")
                    gameInfo.countWinsPlayer1++
                else if (gameInfo.player2.xo == "x")
                    gameInfo.countWinsPlayer2++
            GameState.O_WIN ->
                if (gameInfo.player1.xo == "o")
                    gameInfo.countWinsPlayer1++
                else if (gameInfo.player2.xo == "o")
                    gameInfo.countWinsPlayer2++
            GameState.DRAW ->
                gameInfo.countDraws++
            else -> {

            }
        }
    }


    fun startNewGame() {
        resetPlayerXOs()
        gameGrid.startGame(game, gameInfo)
    }

    fun endGame() {
    }

    fun shareScreenshot() {

    }

}