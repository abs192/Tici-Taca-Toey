package com.abs192.ticitacatoey.game

import com.abs192.ticitacatoey.types.GameInfo

interface PostMoveEventListener {
    fun gameStarting(
        game: Game,
        gameInfo: GameInfo
    )

    fun moveDone(x: Int, y: Int, xo: String)
    fun moveRejected()
    fun gameEnd(checkEnd: GameState)
}