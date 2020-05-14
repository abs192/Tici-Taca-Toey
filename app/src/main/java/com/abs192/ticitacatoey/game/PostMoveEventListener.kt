package com.abs192.ticitacatoey.game

interface PostMoveEventListener {
    fun moveDone()
    fun moveRejected()
    fun gameEnd(checkEnd: GameState)
}