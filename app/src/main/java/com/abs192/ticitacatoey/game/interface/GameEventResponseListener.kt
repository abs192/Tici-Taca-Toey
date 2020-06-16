package com.abs192.ticitacatoey.game.`interface`

import com.abs192.ticitacatoey.game.online.WebsocketManager

interface GameEventResponseListener {

    fun onFailedToConnect()

    fun onRegisterPlayerSuccess()
    fun onRegisterPlayerFailed()

    fun onGameJoinSuccess()
    fun onGameJoinFailed(e: GameJoinFailedError)

    fun onGameStarted(
        playerId: String,
        websocketManager: WebsocketManager,
        gameStartedResponseMessage: WebsocketManager.GameStartedResponseMessage
    )    fun onStartGameFailed()

    fun onMoveMadeSuccess()
    fun onMoveMadeFailed()

}