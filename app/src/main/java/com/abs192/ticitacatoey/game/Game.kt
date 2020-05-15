package com.abs192.ticitacatoey.game

import android.util.Log
import kotlin.random.Random

class Game {

    private val board = arrayListOf("", "", "", "", "", "", "", "", "")
    private val rnd = Random(1231L)
    private var toMove = randomMove()

    fun getToMove(): String {
        return toMove
    }

    fun makeMoveX(x: Int, y: Int): Boolean {
        if (toMove == "o")
            return false
        val pos = getPos(x, y)
        if (board[pos] != "")
            return false
        board[pos] = "x"
        switchMove()
        return true
    }

    fun makeMoveO(x: Int, y: Int): Boolean {
        if (toMove == "x")
            return false
        val pos = getPos(x, y)
        if (board[pos] != "")
            return false
        board[pos] = "o"
        switchMove()
        return true
    }

    private fun switchMove() {
        val win = checkWin()
        Log.d(javaClass.simpleName, "win $win")
        Log.d(javaClass.simpleName, "board $board")
        toMove = if (toMove == "x")
            "o"
        else
            "x"
        Log.d(javaClass.simpleName, "new move $toMove")
    }

    private fun getPos(x: Int, y: Int): Int {
        return (x * 3) + (y % 3)
    }

    fun getXO(x: Int, y: Int): String {
        return board[getPos(x, y)]
    }

    fun checkEnd(): GameState {
        Log.d(javaClass.simpleName, "$board")
        if (!board.contains("") && !checkWin()) {
            return GameState.DRAW
        }
        if (checkWin()) {
            //coz of switch move called after
            if (toMove == "x")
                return GameState.O_WIN
            else
                return GameState.X_WIN
        }
        return GameState.ONGOING
    }

    fun checkWin(): Boolean {
        //horiz
        for (i in 0..2)
            if (checkSame(
                    getXO(i, 0),
                    getXO(i, 1),
                    getXO(i, 2)
                )
            )
                return true

        //vert
        for (i in 0..2)
            if (checkSame(
                    getXO(0, i),
                    getXO(1, i),
                    getXO(2, i)
                )
            )
                return true

        //diags
        if (checkSame(getXO(0, 0), getXO(1, 1), getXO(2, 2)))
            return true
        if (checkSame(getXO(0, 2), getXO(1, 1), getXO(2, 0)))
            return true

        return false
    }

    private fun checkSame(a: String, b: String, c: String): Boolean {
        if (a == "" || b == "" || c == "")
            return false
        return a == b && b == c
    }

    fun resetBoard() {
        for (index in board.indices) {
            board[index] = ""
        }
        toMove = randomMove()
    }

    fun randomMove(): String {
        return if (rnd.nextInt(2) == 1)
            "x"
        else "o"
    }

    fun resetToMove() {
        toMove = randomMove()
    }
}