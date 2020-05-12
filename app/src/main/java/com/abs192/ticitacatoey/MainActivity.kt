package com.abs192.ticitacatoey

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.views.AnimatorUtil
import com.abs192.ticitacatoey.views.canvas.BackgroundCanvas
import com.abs192.ticitacatoey.views.scenes.NewGameScene
import com.abs192.ticitacatoey.views.scenes.PlayComputerScene
import com.abs192.ticitacatoey.views.scenes.SceneType
import com.abs192.ticitacatoey.views.scenes.TTTScene
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

                }

                override fun onDifficultClick() {

                }
            })
        playComputerScene.initScene()
        return playComputerScene
    }

    override fun onBackPressed() {
        Log.d(javaClass.name, "SceneStack: ${sceneStack.size}")
        Log.d(javaClass.name, "MainLayout childCount: ${mainLayout?.childCount}")
        if (!sceneStack.isEmpty() && sceneStack.size != 1) {
            sceneStack.pop().backPressed()
            sceneStack.peek().fadeInFast()
        } else
            super.onBackPressed()
    }

//
//    private fun buttonPressedPlayHuman() {
//        enableDisableViews(false, newGameLayout, buttonPlayComputer, buttonPlayHuman)
//        animatorUtil.fadeOut(
//            newGameLayout,
//            AnimatorUtil.Duration.MEDIUM,
//            object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//
//                }
//            })
//    }
//
//    private fun buttonPressedPlayComputer() {
//        enableDisableViews(false, newGameLayout, buttonPlayComputer, buttonPlayHuman)
//        animatorUtil.fadeOut(
//            newGameLayout,
//            AnimatorUtil.Duration.MEDIUM,
//            object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    showNewGameComputerLayout()
//                }
//            })
//    }
//
//
//    private fun showNewGameComputerLayout() {
//        newGameLayoutComputer = layoutInflater.inflate(R.layout.layout_new_game_computer, null)
//        setConstraintCenterInMainLayout(newGameLayoutComputer!!)
//
//        newGameLayoutComputer = findViewById(R.id.newGameLayoutComputer)
//        buttonPlayComputerEasy = findViewById(R.id.newGameButtonComputerEasy)
//        buttonPlayComputerDifficult = findViewById(R.id.newGameButtonComputerDifficult)
//        animatorUtil.fadeIn(newGameLayoutComputer, AnimatorUtil.Duration.LONG, null)
//
//
//        buttonPlayComputerEasy?.setOnClickListener { buttonPressedPlayComputerEasy() }
//        buttonPlayComputerDifficult?.setOnClickListener { buttonPressedPlayComputerDifficult() }
//    }
//
//    private fun buttonPressedPlayComputerDifficult() {
//
//    }
//
//    private fun buttonPressedPlayComputerEasy() {
//
//    }

}
