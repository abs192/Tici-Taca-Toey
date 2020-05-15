package com.abs192.ticitacatoey.game

interface MoveInputListener {
    fun makeMove(playerId: String, x: Int, y: Int)
}