package com.abs192.ticitacatoey.game

class EndGameMsg {

    private val winArray =
        arrayListOf<String>(
            "You win!",
            "You won!!",
            "Yay winner",
            "Nice! That's a win",
            "Damn you're good at this",
            "WIN :)"
        )

    private val loseArray =
        arrayListOf<String>(
            "You lost :(",
            "Aww! That's a loss",
            "Hmmm tough luck!"
        )


    private val drawArray = arrayListOf<String>(
        "Draw!",
        "That's a draw folks",
        "Aww, a draw!"
    )

    fun win(): String {
        return random(winArray)
    }

    fun draw(): String {
        return random(drawArray)
    }

    fun loss(): String {
        return random(loseArray)
    }


    private fun random(arrayList: ArrayList<String>): String {
        arrayList.shuffle()
        return arrayList[0]
    }
}