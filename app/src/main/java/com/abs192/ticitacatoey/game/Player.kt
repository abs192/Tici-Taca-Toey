package com.abs192.ticitacatoey.game

class Player(var playerId: String, var name: String) {

    lateinit var xo: String

    public fun assignXO(xo: String) {
        this.xo = xo
    }

}