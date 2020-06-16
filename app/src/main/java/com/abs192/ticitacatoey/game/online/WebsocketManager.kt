package com.abs192.ticitacatoey.game.online

import android.app.Activity
import android.os.Handler
import android.util.Log
import com.abs192.ticitacatoey.game.`interface`.GameEventResponseListener
import com.abs192.ticitacatoey.types.OnlineGameStatus
import com.google.gson.Gson
import okhttp3.*

class WebsocketManager(
    private val activity: Activity,
    public val playerName: String,
    private var listener: GameEventResponseListener
) :
    WebSocketListener() {

    private val serverUrlString = "ws://192.168.0.104:80/app"
    private val tag = javaClass.simpleName
    private var webSocket: WebSocket? = null
    private var okHttpClient = OkHttpClient()

    private var gameStatus = OnlineGameStatus.WAITING_FOR_PLAYERS
    var gameId = ""
    private var playerId = ""

    private val gson = Gson()

    private var onMoveFromOnlineListener: MoveFromOnlineListener? = null

    fun setOnMoveFromOnlineListener(onMoveFromOnlineListener: MoveFromOnlineListener) {
        this.onMoveFromOnlineListener = onMoveFromOnlineListener
    }

    fun connect() {
        val request = Request.Builder().url(serverUrlString).build()
        webSocket = okHttpClient.newWebSocket(request, this)
        registerPlayer()
    }

    fun startGame(): Boolean {
        if (!isRegistered()) {
            registerPlayer()
        }
        var done = false
        val jsonString = gson.toJson(StartGameRequestMessage("dummyGameName", 3, 2))
        Log.d(tag, "starting: $jsonString")
        webSocket?.let { done = it.send(jsonString) }
        return done
    }

    fun joinGame(gameId: String): Boolean {
        if (!isRegistered()) {
            registerPlayer()
        }
        var done = false
        val jsonString = gson.toJson(JoinGameRequestMessage(gameId))
        Log.d(tag, "joining game: $jsonString")
        webSocket?.let { done = it.send(jsonString) }
        return done
    }

    private fun isRegistered(): Boolean {
        return playerId.isNotEmpty()
    }

    private fun registerPlayer() {
        val jsonString = gson.toJson(RegisterRequestMessage(playerName))
        Log.d(tag, "registering player: $jsonString")
        jsonString?.let { webSocket?.send(jsonString) }
    }

    fun destroy() {
        okHttpClient.dispatcher.executorService.shutdown()
    }

    /** Web socket listener methods
     */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d(tag, "open socket")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d(tag, "message from socket: $text")
        // parse responses

        val message = gson.fromJson(text, Message::class.java)
//        if(message?.type ==)
        Log.d(tag, "message type : ${message.type}")
        when (message?.type?.let { MessageTypes.valueOf(it) }) {
            MessageTypes.REGISTER_PLAYER -> {
                val response = gson.fromJson(text, RegisterResponseMessage::class.java)
                playerId = response.playerId
            }
            MessageTypes.START_GAME -> {
                val response = gson.fromJson(text, StartGameResponseMessage::class.java)
                gameId = response.gameId
            }
            MessageTypes.GAME_STARTED -> {
                val response = gson.fromJson(text, GameStartedResponseMessage::class.java)
                gameId = response.gameId
                activity.runOnUiThread {
                    listener.onGameStarted(playerId, this, response)
                }
            }
            MessageTypes.MAKE_MOVE -> {
                val response = gson.fromJson(text, MakeMoveResponseMessage::class.java)
                if (response.turn == playerId) {
                    onMoveFromOnlineListener?.moveMade(response)
                }
            }
            null -> {

            }
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d(tag, "failure from socket ")//${t.message} $response")
        Log.d(tag, "${t.message}")
//        Log.d(tag, "${response?.message}")
        t.message?.let {
            if (it.contains("Failed to connect")) {
                listener.onFailedToConnect()
            }
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d(tag, "closed socket")
    }

    fun makeMove(x: Int, y: Int) {
        val jsonString = gson.toJson(MakeMoveRequestMessage(gameId, x, y))
        Log.d(tag, "making move: $jsonString")
        jsonString?.let { webSocket?.send(jsonString) }
    }

    /** Message objects
     *
     */
    open class Message(
        var type: String
    )

    enum class MessageTypes {
        REGISTER_PLAYER,
        START_GAME,
        GAME_STARTED,
        MAKE_MOVE,
        GAME_COMPLETE
    }

    class RegisterRequestMessage(var name: String) : Message("REGISTER_PLAYER")
    class RegisterResponseMessage(var playerId: String, var name: String) :
        Message("REGISTER_PLAYER")

    class StartGameRequestMessage(
        var name: String, var boardSize: Int = 3, var playerCount: Int = 2
    ) :
        Message("START_GAME")

    class StartGameResponseMessage(
        var gameId: String,
        var name: String,
        var positions: Array<Array<String>>,
        var playerCount: Int,
        var players: Array<String>,
        var status: String
    ) : Message("START_GAME")

    class JoinGameRequestMessage(var gameId: String) : Message("JOIN_GAME")
    class MakeMoveRequestMessage(var gameId: String, var coordinateX: Int, var coordinateY: Int) :
        Message("MAKE_MOVE")

    class GameStartedResponseMessage(
        var gameId: String,
        var name: String,
        var boardSize: Int,
        var positions: Array<Array<String>>,
        var playerCount: Int,
        var players: ArrayList<String>,
        var status: String,
        var turn: String
    ) : Message(type = "GAME_STARTED")

    class MakeMoveResponseMessage(
        var gameId: String,
        var name: String,
        var boardSize: Int,
        var positions: Array<Array<String>>,
        var playerCount: Int,
        var players: ArrayList<String>,
        var status: String,
        var turn: String
    ) : Message(type = "MAKE_MOVE")


    interface MoveFromOnlineListener {
        fun moveMade(makeMoveRequestMessage: MakeMoveResponseMessage)
    }

}