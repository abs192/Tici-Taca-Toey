package com.abs192.ticitacatoey

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.bluetooth.BTManager
import com.abs192.ticitacatoey.game.ComputerPlayer
import com.abs192.ticitacatoey.game.DefaultColorSets
import com.abs192.ticitacatoey.game.GameManager
import com.abs192.ticitacatoey.game.Player
import com.abs192.ticitacatoey.types.GameInfo
import com.abs192.ticitacatoey.views.canvas.BackgroundCanvas
import com.abs192.ticitacatoey.views.scenes.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var backgroundCanvas: BackgroundCanvas? = null
    private var mainLayout: ConstraintLayout? = null

    private lateinit var currentViewState: SceneType
    private var sceneStack: Stack<TTTScene> = Stack()

    private var player = Player("", "")
    private val btManager = BTManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main_layout)
        backgroundCanvas = findViewById(R.id.backgroundCanvas)

        // check scenes
        if (sceneStack.isEmpty()) {
            sceneStack.add(newGameScene())
        } else {
            //loadScene
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
            })
        newGameScene.initScene()
        return newGameScene
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

                override fun onImpossibleClicked() {
                    sceneStack.add(playGameComputer(ComputerPlayer.Difficulty.IMPOSSIBLE))
                }
            })
        playComputerScene.initScene()
        return playComputerScene
    }

    private fun playGameComputer(difficulty: ComputerPlayer.Difficulty): GameScene {
        backgroundCanvas?.computerGameStart()
        val gameScene = GameScene(GameManager.GameMode.COMPUTER, this, layoutInflater, mainLayout!!)
        gameScene.initGameInfo(
            GameInfo(
                "a",
                player,
                ComputerPlayer(difficulty),
                DefaultColorSets().computerColorSet
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

                override fun onBluetoothClicked() {
                    sceneStack.add(bluetoothConnectScene())
                }

                override fun onOnlineClicked() {

                }
            })
        playHumanScene.initScene()
        return playHumanScene
    }


    private fun bluetoothConnectScene(): BluetoothConnectScene {
        val bluetoothConnectScene = BluetoothConnectScene(
            this,
            btManager,
            layoutInflater,
            mainLayout!!,
            object : BluetoothConnectScene.BluetoothConnectClickListener {
                override fun onHostGame() {
                }

                override fun onSearchGame() {
                }
            })
        bluetoothConnectScene.initScene()
        return bluetoothConnectScene
    }

    override fun onDestroy() {
        super.onDestroy()
        btManager.stopDiscovery(this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            btManager.requestCodeEnableBT -> {
                if (resultCode == RESULT_OK) {
                    Log.d("bt", "its on now")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            btManager.requestCodePermissionBT -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    ToastDialog(this, "Yay").show()
                    backgroundCanvas?.showErrorTint()
                } else {
                    ToastDialog(this, "Bluetooth permission denied").show()
                    backgroundCanvas?.showErrorTint()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    private fun playGameHuman(): GameScene {
        backgroundCanvas?.computerGameStart()
        val gameScene = GameScene(GameManager.GameMode.HUMAN, this, layoutInflater, mainLayout!!)
        gameScene.initGameInfo(
            GameInfo(
                "a",
                player,
                Player("p2", "p2"),
                DefaultColorSets().computerColorSet
            )
        )
        gameScene.initScene()
        return gameScene
    }

    override fun onPause() {
        super.onPause()
        backgroundCanvas?.pause()
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
