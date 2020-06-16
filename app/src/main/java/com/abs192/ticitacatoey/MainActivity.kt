package com.abs192.ticitacatoey

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.audio.AudioManager
import com.abs192.ticitacatoey.game.ComputerPlayer
import com.abs192.ticitacatoey.game.DefaultColorSets
import com.abs192.ticitacatoey.game.GameManager
import com.abs192.ticitacatoey.game.Player
import com.abs192.ticitacatoey.game.online.QRCodeScanner
import com.abs192.ticitacatoey.game.online.WebsocketManager
import com.abs192.ticitacatoey.types.GameInfo
import com.abs192.ticitacatoey.views.canvas.BackgroundCanvas
import com.abs192.ticitacatoey.views.scenes.*
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*


class MainActivity : AppCompatActivity() {

    var backgroundCanvas: BackgroundCanvas? = null
    private var mainLayout: ConstraintLayout? = null

    private var sceneStack: Stack<TTTScene> = Stack()

    private var player = Player("p1", "")

    private val store = Store(this)

    private var audioManager: AudioManager? = null

    private var qrCodeScanner: QRCodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setThemeAndDarkMode()
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main_layout)
        backgroundCanvas = findViewById(R.id.backgroundCanvas)

        backgroundCanvas?.setDarkMode(store.getDarkMOde())

        audioManager = AudioManager(this)
        qrCodeScanner = QRCodeScanner(this)

        sceneStack.add(newGameScene())

        val intent = intent
        if (Intent.ACTION_VIEW == intent.action) {
            val uri: Uri? = intent.data
            if (uri.toString().contains("joinGame", false)) {
                val gameId: String? = uri?.getQueryParameter("gameId")
                sceneStack.peek().fadeOut()
                gameId?.let {
                    sceneStack.add(playOnlineScene(it))
                }
            }
        }
    }

    private fun setThemeAndDarkMode() {
        when (store.getTheme()) {
            Store.Theme.PINK -> if (store.getDarkMOde() == Store.DarkMode.OFF) {
                setTheme(R.style.LightAppTheme)
            } else {
                setTheme(R.style.DarkAppTheme)
            }
            Store.Theme.BLUE -> if (store.getDarkMOde() == Store.DarkMode.OFF) {
                setTheme(R.style.LightAppTheme_Blue)
            } else {
                setTheme(R.style.DarkAppTheme_Blue)
            }
        }
    }

    private fun newGameScene(): NewGameScene {
        val newGameScene = NewGameScene(
            this,
            layoutInflater,
            mainLayout!!,
            object : NewGameScene.NewGameButtonClickListener {
                override fun onComputerClicked() {
                    sceneStack.add(playComputerScene())
                }

                override fun onHumanClicked() {
                    sceneStack.add(playHumanScene())
                }

                override fun onSettingsClicked() {
                    sceneStack.add(settingsScene())
                }
            })
        newGameScene.initScene()
        return newGameScene
    }

    private fun settingsScene(): SettingsScene {
        val settingsScene = SettingsScene(
            this,
            store,
            layoutInflater,
            mainLayout!!,
            object : SettingsScene.SettingsClickListener {
                override fun onDarkModeOffClicked() {
                    recreate()
                    backgroundCanvas?.setDarkMode(Store.DarkMode.OFF)
                }

                override fun onDarkModeOnClicked() {
                    recreate()
                    backgroundCanvas?.setDarkMode(Store.DarkMode.ON)
                }

                override fun OnThemePinkClicked() {
                    recreate()
                }

                override fun OnThemeBlueClicked() {
                    recreate()
                }
            })
        settingsScene.initScene()
        return settingsScene
    }

    private fun playComputerScene(): PlayComputerScene {
        val playComputerScene = PlayComputerScene(
            this,
            layoutInflater,
            mainLayout!!,
            object : PlayComputerScene.PlayComputerButtonClickListener {
                override fun onEasyClicked() {
                    sceneStack.add(playGameComputer(ComputerPlayer.Difficulty.EASY))
                }

                override fun onHardClicked() {
                    sceneStack.add(playGameComputer(ComputerPlayer.Difficulty.HARD))
                }
            })
        playComputerScene.initScene()
        return playComputerScene
    }

    private fun playGameComputer(difficulty: ComputerPlayer.Difficulty): GameScene {
        backgroundCanvas?.computerGameStart()
        val gameScene = GameScene(
            GameManager.GameMode.COMPUTER,
            audioManager,
            this,
            layoutInflater,
            mainLayout!!
        )
        gameScene.initGameInfo(
            GameInfo(
                "a",
                player,
                ComputerPlayer(difficulty),
                DefaultColorSets(this).computerColorSet
            )
        )
        gameScene.initScene()
        return gameScene
    }

    private fun playHumanScene(): PlayHumanScene {
        val playHumanScene = PlayHumanScene(
            this,
            layoutInflater,
            mainLayout!!,
            object : PlayHumanScene.PlayHumanButtonClickListener {
                override fun onLocalClicked() {
                    sceneStack.add(playGameHuman())
                }

                override fun onOnlineClicked() {
                    sceneStack.add(playOnlineScene())
                }
            })
        playHumanScene.initScene()
        return playHumanScene
    }

    private fun playOnlineScene(gameId: String = ""): PlayOnlineScene {
        val playOnlineScene = PlayOnlineScene(
            this,
            qrCodeScanner!!,
            layoutInflater,
            mainLayout!!,
            object : PlayOnlineScene.OnPlayOnlineButtonClickListener {
                override fun onHostGameClicked() {
                }

                override fun onJoinGameClicked() {
                }
            }
        )
        playOnlineScene.initScene()
        if (gameId.isNotEmpty()) {
            playOnlineScene.joinGame(gameId)
        }
        return playOnlineScene
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(javaClass.simpleName, "onActivityResult  $requestCode resultCode $resultCode")
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result =
                    IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    Log.d(javaClass.simpleName, "qrcode contents ${result.contents}")
                    qrCodeScanner?.onScanned(result.contents)
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(javaClass.simpleName, "qrcode cancelled")
                qrCodeScanner?.onScanned("")
            }
        } else {
            Log.d(javaClass.simpleName, "activity result $requestCode $resultCode")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // add internet permission callbacks
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun playGameHuman(): GameScene {
        val gameScene =
            GameScene(
                GameManager.GameMode.HUMAN,
                audioManager,
                this,
                layoutInflater,
                mainLayout!!
            )
        gameScene.initGameInfo(
            GameInfo(
                "",
                player,
                Player("p2", "You are"),
                DefaultColorSets(this).computerColorSet,
                isHumanLocalGame = true
            )
        )
        gameScene.initScene()
        return gameScene
    }

    fun playGameOnlineScene(
        playerId: String,
        websocketManager: WebsocketManager,
        gameStartedResponseMessage: WebsocketManager.GameStartedResponseMessage
    ) {
        if (sceneStack.peek() is GameScene) {
            sceneStack.pop().backPressed()
        }
        val gameScene =
            GameScene(
                GameManager.GameMode.ONLINE,
                audioManager,
                this,
                layoutInflater,
                mainLayout!!
            )
        gameScene.onlineGameInit(playerId, websocketManager, gameStartedResponseMessage)
        gameScene.initScene()
        sceneStack.add(gameScene)
    }

    override fun onPause() {
        super.onPause()
        backgroundCanvas?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager?.destroy()
    }

    override fun onResume() {
        super.onResume()
        backgroundCanvas?.resume()
    }

    override fun onBackPressed() {
        Log.d(javaClass.name, "SceneStack: ${sceneStack.size}")
        Log.d(javaClass.name, "MainLayout childCount: ${mainLayout?.childCount}")
        if (!sceneStack.isEmpty() && sceneStack.size != 1) {

            if (sceneStack.peek().sceneType == SceneType.GAME) {
                // are you sure dialog
                Log.d(javaClass.name, "Game ongoing")
            }

            sceneStack.pop().backPressed()
            sceneStack.peek().fadeInFast()
            correctBackgroundTint()
        } else
            super.onBackPressed()
    }

    private fun correctBackgroundTint() {
        if (sceneStack.peek().sceneType != SceneType.GAME) {
            // TODO: handle better based on game type
            backgroundCanvas?.normalTint()
        }
    }
}
