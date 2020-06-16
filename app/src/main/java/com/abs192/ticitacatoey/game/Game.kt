package com.abs192.ticitacatoey.game

import android.util.Log
import kotlin.random.Random

class Game {

    private var board = arrayListOf("", "", "", "", "", "", "", "", "")
    private val rnd1 = Random(1231L)
    private val rnd2 = Random(1419L)
    private var toMove = randomMove()
    val highlightedIndices = arrayListOf<Int>()
    var currentState: GameState = GameState.STARTING

    constructor()

    constructor(board: ArrayList<String>, toMove: String) {
        board.forEachIndexed { i, it ->
            this.board[i] = it
        }
        this.toMove = toMove
        this.currentState = GameState.ONGOING
    }

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
            ) {
                highlightedIndices.add(getPos(0, i))
                highlightedIndices.add(getPos(1, i))
                highlightedIndices.add(getPos(2, i))
                return true
            }
        //vert
        for (i in 0..2)
            if (checkSame(
                    getXO(0, i),
                    getXO(1, i),
                    getXO(2, i)
                )
            ) {
                highlightedIndices.add(getPos(i, 0))
                highlightedIndices.add(getPos(i, 1))
                highlightedIndices.add(getPos(i, 2))
                return true
            }
        //diags
        if (checkSame(getXO(0, 0), getXO(1, 1), getXO(2, 2))) {
            highlightedIndices.add(getPos(0, 0))
            highlightedIndices.add(getPos(1, 1))
            highlightedIndices.add(getPos(2, 2))
            return true
        }
        if (checkSame(getXO(0, 2), getXO(1, 1), getXO(2, 0))) {
            highlightedIndices.add(getPos(0, 2))
            highlightedIndices.add(getPos(1, 1))
            highlightedIndices.add(getPos(2, 0))
            return true
        }

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
        currentState = GameState.STARTING
        highlightedIndices.clear()
    }

    fun randomMove(): String {
        return if (rnd1.nextInt(2) == 1)
            "o"
        else "x"
    }

    fun printBoard(): ArrayList<String> {
        return this.board
    }

    fun resetToMove() {
        toMove = if (rnd2.nextInt(2) == 1)
            "o"
        else "x"
    }

    fun makeItHisMove(toMove: String) {
        this.toMove = toMove
    }

    fun updateBoard(aL: java.util.ArrayList<String>, turn: String) {
        board = aL
        toMove = turn
    }
}