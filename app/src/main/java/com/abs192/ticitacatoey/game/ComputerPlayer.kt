package com.abs192.ticitacatoey.game

import android.os.Handler
import android.util.Log
import com.abs192.ticitacatoey.types.GameInfo

class ComputerPlayer(difficulty: Difficulty) : Player(difficulty.name, "Computer"),
    PostMoveEventListener {

    private val tag = "BEEP BOOP"
    private var playerXO: String = ""
    private var isItYourMove = false

    private val myBoard = arrayListOf("", "", "", "", "", "", "", "", "")
    private var moveInputListener: MoveInputListener? = null
    private val computerLogic = ComputerLogic(difficulty)

    override fun gameStarting(
        game: Game,
        gameInfo: GameInfo
    ) {
        Log.d(tag, "starting")
        myBoard.indices.forEach {
            myBoard[it] = ""
        }
        playerXO = gameInfo.player2.xo
        isItYourMove = game.getToMove() == playerXO
        makeAMove(playerXO)
    }

    override fun moveDone(x: Int, y: Int, xo: String) {
        Log.d(tag, "move done")
        val pos = getPos(x, y)
        myBoard[pos] = xo
        isItYourMove = xo != playerXO
        makeAMove(playerXO)
    }

    private fun makeAMove(moveAsXO: String) {
        if (isItYourMove) {
            Handler().postDelayed({
                val pair = computerLogic.makeMove(myBoard, moveAsXO)
                pair?.let { moveInputListener?.makeMove(playerId, pair.first, pair.second) }

            }, 500)
        }
    }

    override fun moveRejected() {
        Log.d(tag, "move rejected. lul")
    }

    override fun gameEnd(checkEnd: GameState) {
        Log.d(tag, "game ENd")

    }

    private fun getPos(x: Int, y: Int): Int {
        return (x * 3) + (y % 3)
    }

    fun registerMoveListener(moveInputListener: MoveInputListener) {
        this.moveInputListener = moveInputListener
    }

    enum class Difficulty {
        EASY,
        HARD
    }

}