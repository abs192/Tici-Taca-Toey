package com.abs192.ticitacatoey

import kotlin.random.Random

class Game {

    private val board = arrayListOf("", "", "", "", "", "", "", "", "")

    private var toMove: String = if (Random(1231L).nextInt(2) == 1)
        "x"
    else "y"

    fun getToMove(): String {
        return toMove
    }

    fun makeMoveX(x: Int, y: Int): Boolean {
        if (toMove == "o")
            return false
        board[(x * 3) + (y / 3)] = "x"
        switchMove()
        return true
    }

    private fun switchMove() {
        toMove = if (toMove == "x")
            "o"
        else
            "x"
    }


    fun makeMoveO(x: Int, y: Int): Boolean {
        if (toMove == "x")
            return false
        board[(x * 3) + (y / 3)] = "o"
        switchMove()
        return true
    }


    fun getXO(x: Int, y: Int): String {
        return board[(x / 3) + (y % 3)]
    }
}