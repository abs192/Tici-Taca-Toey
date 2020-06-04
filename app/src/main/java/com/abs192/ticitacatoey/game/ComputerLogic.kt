package com.abs192.ticitacatoey.game

class ComputerLogic(private val difficulty: ComputerPlayer.Difficulty) {
    fun makeMove(board: ArrayList<String>, youAre: String): Pair<Int, Int>? {
        return when (difficulty) {
            ComputerPlayer.Difficulty.EASY ->
                makeMoveEasy(board)
            ComputerPlayer.Difficulty.HARD ->
                makeMoveHard(board, youAre)
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


    private fun makeMoveHard(board: ArrayList<String>, youAre: String): Pair<Int, Int>? {
        val availableMoves = getAvailableMoves(board)
        if (availableMoves.isEmpty()) {
            return null
        }
        return if (availableMoves.count() <= 6) {
            getBestMove(availableMoves, board, youAre)
        } else {
            makeMoveEasy(board)
        }
    }

    private fun getBestMove(
        availableMoves: ArrayList<Int>,
        board: java.util.ArrayList<String>,
        youAre: String
    ): Pair<Int, Int>? {

        var selectedMove = availableMoves[0]
        var score = -1

        if (availableMoves.size != 1) {
            for (move in availableMoves) {
                val copyGame = Game(board, youAre)
                val moveScore = makeMoveAndEvaluate(copyGame, move, youAre)
                if (moveScore == 1) {
                    selectedMove = move
                    break
                }

                var lookAheadScore = -1
                val lookAheadAvailableMoves = ArrayList<Int>(availableMoves)
                lookAheadAvailableMoves.remove(move)
                for (lookAheadMove in lookAheadAvailableMoves) {

                    val theyAre = if (youAre == "x") "o" else "x"
                    val copyLookAheadGame = Game(copyGame.printBoard(), theyAre)
                    val lookAheadMoveScore =
                        makeMoveAndEvaluate(
                            copyLookAheadGame, lookAheadMove, theyAre
                        )
                    val newScore = moveScore + (-1 * lookAheadMoveScore)
                    if (newScore == -1) {
                        lookAheadScore = -1
                        break
                    } else if (newScore > lookAheadScore) {
                        lookAheadScore = newScore
                    }
                }

                if (lookAheadScore > score) {
                    selectedMove = move
                    score = lookAheadScore
                }
            }
        }
        val p = Pair(selectedMove / 3, selectedMove % 3)
        return p
    }

    /**
     * winning 1
     * losing -1
     * draw 0
     */
    private fun makeMoveAndEvaluate(game: Game, movePos: Int, youAre: String): Int {
        val x = movePos / 3
        val y = movePos % 3

        if (youAre == "o") {
            game.makeMoveO(x, y)
        } else {
            game.makeMoveX(x, y)
        }

        val e = game.checkEnd()
        val result = when (e) {
            GameState.X_WIN -> {
                if (youAre == "x") 1 else -1
            }
            GameState.O_WIN -> {
                if (youAre == "o") 1 else -1
            }
            GameState.DRAW -> 0
            else -> 0
        }

        return result
    }


    private fun getAvailableMoves(board: ArrayList<String>): ArrayList<Int> {
        val emptyCells = ArrayList<Int>(board.indices.filter { board[it] == "" })
        if (emptyCells.isNotEmpty()) {
            emptyCells.shuffle()
            return emptyCells

        }
        return arrayListOf()
    }
}