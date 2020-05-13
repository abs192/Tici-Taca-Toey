package com.abs192.ticitacatoey

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.types.GameInfo
import com.abs192.ticitacatoey.views.AnimatorUtil
import com.abs192.ticitacatoey.views.canvas.BackgroundCanvas
import com.abs192.ticitacatoey.views.scenes.*
import kotlinx.android.synthetic.main.layout_game.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var backgroundCanvas: BackgroundCanvas? = null
    private var mainLayout: ConstraintLayout? = null

    private lateinit var currentViewState: SceneType
    private var sceneStack: Stack<TTTScene> = Stack()

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
                    sceneStack.add(playGameComputerEasy())
                }

                override fun onHardClicked() {

                }
            })
        playComputerScene.initScene()
        return playComputerScene
    }

    private fun playGameComputerEasy(): GameScene {
        backgroundCanvas?.computerGameStart()
        val gameScene = GameScene(this, layoutInflater, mainLayout!!)
        gameScene.initGameInfo(GameInfo("a",1,2))
        gameScene.initScene()
        return gameScene
    }

    private fun playGame() {

    }

    private fun playHumanScene(): PlayHumanScene {
        val playHumanScene = PlayHumanScene(
            this,
            layoutInflater,
            mainLayout!!,
            object : PlayHumanScene.PlayHumanButtonClickListener {
                override fun onLocalClicked() {
                }

                override fun onBluetoothClicked() {

                }

                override fun onOnlineClicked() {

                }
            })
        playHumanScene.initScene()
        return playHumanScene
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
            backgroundCanvas?.computerGameEnd()
        }

    }

}
