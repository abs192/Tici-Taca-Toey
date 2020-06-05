package com.abs192.ticitacatoey.types

import com.abs192.ticitacatoey.game.ColorSet
import com.abs192.ticitacatoey.game.Player

class GameInfo(
    var gameId: String,
    var player1: Player,
    var player2: Player,
    var colorSet: ColorSet,
    val isHumanLocalGame: Boolean = false
) {
    var countWinsPlayer1 = 0
    var countWinsPlayer2 = 0
    var countDraws = 0
}