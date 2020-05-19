package com.abs192.ticitacatoey.game

class ComputerLogic(private val difficulty: ComputerPlayer.Difficulty) {
    fun makeMove(board: ArrayList<String>): Pair<Int, Int>? {
        return when (difficulty) {
            ComputerPlayer.Difficulty.EASY ->
                makeMoveEasy(board)
            ComputerPlayer.Difficulty.HARD ->
                makeMoveEasy(board) //change
            ComputerPlayer.Difficulty.IMPOSSIBLE ->
                makeMoveEasy(board) // change
        }
    }

    private fun makeMoveEasy(board: ArrayList<String>): Pair<Int, Int>? {
//        val emptyCells = arrayListOf<Int>()
        val emptyCells = ArrayList<Int>(board.indices.filter { board[it] == "" })
//        board.indices.forEach { if (board[it] == "") emptyCells.add(it) }
        if (emptyCells.isNotEmpty()) {
            emptyCells.shuffle()
            val x = emptyCells[0] / 3
            val y = emptyCells[0] % 3
            return Pair(x, y)
        }
        return null
    }
}