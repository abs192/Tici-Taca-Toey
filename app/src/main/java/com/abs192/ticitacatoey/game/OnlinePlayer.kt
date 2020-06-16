package com.abs192.ticitacatoey.game

import com.abs192.ticitacatoey.game.online.WebsocketManager
import com.abs192.ticitacatoey.types.GameInfo

class OnlinePlayer(playerId: String, var websocketManager: WebsocketManager) : Player(playerId, ""),
    PostMoveEventListener {

    private var moveInputListener: MoveInputListener? = null

    fun makeMove(x: Int, y: Int) {
        websocketManager.makeMove(x, y)
    }

    override fun gameStarting(game: Game, gameInfo: GameInfo) {
    }

    override fun moveDone(x: Int, y: Int, xo: String) {
    }

    override fun moveRejected() {
    }

    override fun gameEnd(checkEnd: GameState) {
    }

    fun registerMoveListener(moveInputListener: MoveInputListener) {
        this.moveInputListener = moveInputListener
    }
}
