package com.abs192.ticitacatoey.game

import android.os.Handler
import android.util.Log
import com.abs192.ticitacatoey.types.GameInfo
import com.abs192.ticitacatoey.views.canvas.GridCanvas

class GameManager(
    private val gameMode: GameMode,
    var game: Game,
    var gameInfo: GameInfo,
    private val gameGrid: GridCanvas
) :
    MoveInputListener {

    private var player1PostMoveInputListener: PostMoveEventListener? = null
    private var player2PostMoveInputListener: PostMoveEventListener? = null


    fun initialize() {
        // depending on the game type
        gameGrid.initColors(gameInfo.colorSet)


        when (gameMode) {
            GameMode.COMPUTER -> {
                gameGrid.registerMoveListener(this)
                player1PostMoveInputListener = gameGrid

                val computerPlayer = gameInfo.player2 as ComputerPlayer
                player2PostMoveInputListener = computerPlayer
                computerPlayer.registerMoveListener(this)
            }
            GameMode.HUMAN -> {
                gameGrid.registerMoveListener(this)
                player1PostMoveInputListener = gameGrid
                player2PostMoveInputListener = gameGrid
            }
        }
        startNewGame()
    }

    private fun resetPlayerXOs() {
        val xo = game.randomMove()
        val ox = if (xo == "x") "o" else "x"
        gameInfo.player1.assignXO(xo)
        gameInfo.player2.assignXO(ox)
        game.resetToMove()
    }

    private fun isItPlayersTurn(playerId: String): Boolean {
        if (gameInfo.player1.playerId == playerId) {
            return gameInfo.player1.xo == game.getToMove()
        } else if (gameInfo.player2.playerId == playerId) {
            return gameInfo.player2.xo == game.getToMove()
        }
        return false
    }

    override fun makeMove(playerId: String, x: Int, y: Int) {
        game.currentState = GameState.ONGOING
        Log.d(javaClass.simpleName, "toMove ${game.getToMove()}")

        if (!isItPlayersTurn(playerId)) {
            if (gameInfo.player1.playerId == playerId) {
                player1PostMoveInputListener?.moveRejected()
            } else if (gameInfo.player2.playerId == playerId) {
                player2PostMoveInputListener?.moveRejected()
            }
            return
        }

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val xo = game.getXO(i, j)
                Log.d(javaClass.simpleName, "$i $j $xo")
            }
        }

        game.currentState = game.checkEnd()
        if (game.currentState != GameState.ONGOING) {
            return
        }

        val xoToMove = game.getToMove()
        val moveDone: Boolean = if (xoToMove == "o") {
            game.makeMoveO(x, y)
        } else {
            game.makeMoveX(x, y)
        }
        if (moveDone) {
            player1PostMoveInputListener?.moveDone(x, y, xoToMove)
            player2PostMoveInputListener?.moveDone(x, y, xoToMove)
        } else {
            player1PostMoveInputListener?.moveRejected()
            player2PostMoveInputListener?.moveRejected()
        }

        game.currentState = game.checkEnd()
        if (game.currentState != GameState.ONGOING) {
            updateScore()
            player1PostMoveInputListener?.gameEnd(game.currentState)
            player2PostMoveInputListener?.gameEnd(game.currentState)
            // restart after 2 secs
            Handler().postDelayed({
                game.resetBoard()
                startNewGame()
            }, 2000)
        }
    }

    private fun updateScore() {
        when (game.currentState) {
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

    private fun startNewGame() {
        resetPlayerXOs()
        player1PostMoveInputListener?.gameStarting(game, gameInfo)
        player2PostMoveInputListener?.gameStarting(game, gameInfo)
    }

    fun endGame() {
    }

    fun shareScreenshot() {

    }

    enum class GameMode {
        COMPUTER,
        HUMAN
    }
}