package com.abs192.ticitacatoey

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.bluetooth.BTService
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

    private var sceneStack: Stack<TTTScene> = Stack()

    private var btService: BTService? = null

    private var player = Player("", "")

    private var discoveryCallback: BTService.DiscoveryCallback? = null

    private val store = Store(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (store.getBackgroundTheme() == Store.Theme.LIGHT) {
            setTheme(R.style.LightAppTheme)
        } else {

            setTheme(R.style.DarkAppTheme)
        }
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main_layout)
        backgroundCanvas = findViewById(R.id.backgroundCanvas)

        backgroundCanvas?.setTheme(store.getBackgroundTheme())

        // check scenes
        if (sceneStack.isEmpty()) {
            sceneStack.add(newGameScene())
        } else {
            //loadScene
        }
        btService = BTService(this)
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
                override fun onThemeLightClicked() {
                    recreate()
                    backgroundCanvas?.setTheme(Store.Theme.LIGHT)
                }

                override fun onThemeDarkClicked() {
                    recreate()
                    backgroundCanvas?.setTheme(Store.Theme.DARK)
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
            btService!!,
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(javaClass.simpleName, "activity result $requestCode $resultCode")
        btService!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        btService!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun playGameHuman(): GameScene {
        backgroundCanvas?.computerGameStart()
        val gameScene = GameScene(GameManager.GameMode.HUMAN, this, layoutInflater, mainLayout!!)
        gameScene.initGameInfo(
            GameInfo(
                "a",
                player,
                Player("p2", "p2"),
                DefaultColorSets(this).computerColorSet
            )
        )
        gameScene.initScene()
        return gameScene
    }

    override fun onPause() {
        super.onPause()
        backgroundCanvas?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: Unregister when bt mode is fixed
//        unregisterReceiver(bluetoothScanReceiver)
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

    fun setDiscoveryCallback(discoveryCallback: BTService.DiscoveryCallback) {
        this.discoveryCallback = discoveryCallback
    }

    val bluetoothScanReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            val action = intent!!.action
            Log.d(javaClass.simpleName, "scan receiver onReceive")
            Log.d(javaClass.simpleName, "action " + action)
            if (action != null) {
                when (action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state =
                            intent.getIntExtra(
                                BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.ERROR
                            )
                        if (state == BluetoothAdapter.STATE_OFF) {
                            if (discoveryCallback != null) {
//                                discoveryCallback?.onError(DiscoveryError.BLUETOOTH_DISABLED)
                                discoveryCallback?.onError()
                            }
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED ->
                        discoveryCallback?.onDiscoveryStarted()
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        context!!.unregisterReceiver(this)
                        discoveryCallback?.onDiscoveryFinished()
                    }
                    BluetoothDevice.ACTION_FOUND -> {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        device?.let { discoveryCallback?.onDeviceFound(device) }
                    }
                }
            }

        }
    }
}
