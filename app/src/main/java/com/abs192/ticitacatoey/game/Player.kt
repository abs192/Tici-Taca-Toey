package com.abs192.ticitacatoey.game

open class Player(var playerId: String, var name: String) {

    lateinit var xo: String

    fun assignXO(xo: String) {
        this.xo = xo
    }

}