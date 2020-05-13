package com.abs192.ticitacatoey.types

class GameInfo(
    var gameId: String,
    var player1Id: Long,
    var player2Id: Long
) {

    var countWinsPlayer1 = 0
    var countWinsPlayer2 = 0
    var countDraws = 0

}