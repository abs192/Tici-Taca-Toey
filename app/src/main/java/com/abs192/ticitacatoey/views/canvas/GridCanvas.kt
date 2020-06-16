package com.abs192.ticitacatoey.views.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import com.abs192.ticitacatoey.audio.AudioManager
import com.abs192.ticitacatoey.game.*
import com.abs192.ticitacatoey.types.GameInfo
import com.abs192.ticitacatoey.views.canvas.grid.GridSection
import com.abs192.ticitacatoey.views.canvas.grid.OpponentBanner
import com.abs192.ticitacatoey.views.canvas.grid.YourBanner

class GridCanvas(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet),
    PostMoveEventListener {

    private val canvasHelper = CanvasHelper(context)
    private var audioManager: AudioManager? = null

    private lateinit var game: Game
    private lateinit var gameInfo: GameInfo

    private lateinit var moveInputListener: MoveInputListener

    private var selectedSquare = Point(-1, -1)

    private val opponentStatusMsg: String = ""
    private val yourStatusMsg: String = ""

    private var bannerSubText = ""
    private var isAgainstComputer = false

    private var didYouWin = false
    private var didYouLose = false
    private var didYouDraw = false

    private val opponentBanner = OpponentBanner(canvasHelper)
    private val yourBanner = YourBanner(canvasHelper)
    private val gridSection = GridSection(canvasHelper)


    /**
     * Rect objects
     */
    private var topRect = Rect()
    private var outerRect = Rect()
    private var bottomRect = Rect()

    private var scoreBoardRect = Rect()

    init {
        topRect = Rect(
            0,
            0, canvasHelper.displayWidth,
            (canvasHelper.displayHeight / 2) - (canvasHelper.displayWidth / 2)
        )
        outerRect = Rect(
            0,
            (canvasHelper.displayHeight / 2) - (canvasHelper.displayWidth / 2),
            canvasHelper.displayWidth,
            (canvasHelper.displayHeight / 2) + (canvasHelper.displayWidth / 2)
        )
        bottomRect = Rect(
            0,
            (canvasHelper.displayHeight / 2) + (canvasHelper.displayWidth / 2),
            canvasHelper.displayWidth,
            canvasHelper.displayHeight
        )

        gridSection.initRects()
        yourBanner.initRects()
        opponentBanner.initRects()
    }

    fun initColors(colorSet: ColorSet) {
        gridSection.initColors(colorSet)
        yourBanner.initColors(colorSet)
        opponentBanner.initColors(colorSet)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBoard(canvas)
//        drawBanner(canvas)
//        drawBottomBanners(canvas)
        drawYourBanner(canvas)
        drawOpponentBanner(canvas)
    }

    private fun drawBoard(canvas: Canvas?) {
        gridSection.draw(canvas, selectedSquare, game)
    }

    private fun drawOpponentBanner(canvas: Canvas?) {
        opponentBanner.draw(
            canvas,
            game.getToMove() == gameInfo.player2.xo,
            gameInfo.player2.name,
            gameInfo.player2.playerId,
            gameInfo.player2.xo,
            game.currentState,
            opponentStatusMsg,
            gameInfo.isHumanLocalGame
        )
    }

    private fun drawYourBanner(canvas: Canvas?) {
        yourBanner.draw(
            canvas,
            game.getToMove() == gameInfo.player1.xo,
            gameInfo.player1.xo,
            game.currentState,
            yourStatusMsg
        )

    }
//
//    private fun drawBottomBanners(canvas: Canvas?) {
//
//        val rect1 = Rect(squareRects[2])
//        rect1.offset(0, canvasHelper.squareSize)
//        canvas?.drawText(
//            "Wins", rect1.centerX().toFloat(),
//            rect1.centerY().toFloat(), bannerBottomTextWinPaint
//        )
//        rect1.offset(0, bannerSubTextNormalPaint.textSize.toInt())
//        canvas?.drawText(
//            gameInfo.countWinsPlayer1.toString(), rect1.centerX().toFloat(),
//            rect1.centerY().toFloat(), bannerBottomTextWinPaint
//        )
//
//        val rect2 = Rect(squareRects[5])
//        rect2.offset(0, canvasHelper.squareSize)
//        canvas?.drawText(
//            "Draw", rect2.centerX().toFloat(),
//            rect2.centerY().toFloat(), bannerBottomTextDrawPaint
//        )
//        rect2.offset(0, bannerBottomTextDrawPaint.textSize.toInt())
//        canvas?.drawText(
//            gameInfo.countDraws.toString(), rect2.centerX().toFloat(),
//            rect2.centerY().toFloat(), bannerBottomTextDrawPaint
//        )
//        val rect3 = Rect(squareRects[8])
//        rect3.offset(0, canvasHelper.squareSize)
//        canvas?.drawText(
//            "Loss", rect3.centerX().toFloat(),
//            rect3.centerY().toFloat(), bannerBottomTextLosePaint
//        )
//        rect3.offset(0, bannerBottomTextLosePaint.textSize.toInt())
//        canvas?.drawText(
//            gameInfo.countWinsPlayer2.toString(), rect3.centerX().toFloat(),
//            rect3.centerY().toFloat(), bannerBottomTextLosePaint
//        )
//
//        canvas?.drawLine(
//            canvasHelper.displayWidth / 3F + canvasHelper.dividerSize / 2F,
//            rect1.top.toFloat() + bannerBottomTextDrawPaint.textSize,
//            canvasHelper.displayWidth / 3F + canvasHelper.dividerSize / 2F,
//            rect1.bottom.toFloat() - 2 * bannerBottomTextDrawPaint.textSize,
//            bannerBottomTextDrawPaint
//        )
//        canvas?.drawLine(
//            2 * canvasHelper.displayWidth / 3F + canvasHelper.dividerSize,
//            rect2.top.toFloat() + bannerBottomTextDrawPaint.textSize,
//            2 * canvasHelper.displayWidth / 3F + canvasHelper.dividerSize,
//            rect2.bottom.toFloat() - 2 * bannerBottomTextDrawPaint.textSize,
//            bannerBottomTextDrawPaint
//        )
//    }
//
//    private fun drawBanner(canvas: Canvas?) {
//        val rect = Rect(topRect)
//        canvas?.drawText(
//            bannerText, rect.centerX().toFloat(),
//            rect.centerY().toFloat(), bannerTextNormalPaint
//        )
//        rect.offset(0, bannerSubTextNormalPaint.textSize.toInt() * 2)
//        canvas?.drawText(
//            bannerSubText, rect.centerX().toFloat(),
//            rect.centerY().toFloat(), bannerSubTextNormalPaint
//        )
//    }

    private fun initXOPlayerTints() {
        if (gameInfo.player1.xo == "x") {
            DrawableCompat.setTint(canvasHelper.mXDrawable, gameInfo.colorSet.player1TintColor)
        } else {
            DrawableCompat.setTint(canvasHelper.mODrawable, gameInfo.colorSet.player1TintColor)
        }

        if (gameInfo.player2.xo == "x") {
            DrawableCompat.setTint(canvasHelper.mXDrawable, gameInfo.colorSet.player2TintColor)
        } else {
            DrawableCompat.setTint(canvasHelper.mODrawable, gameInfo.colorSet.player2TintColor)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(javaClass.simpleName, "onTouch action ${event?.actionMasked} ${event?.x} {$event?.y}")
        if (event?.action == MotionEvent.ACTION_DOWN) {
            selectedSquare = getBoardPos(event.x, event.y)
            invalidate()
            return true
        } else if (event?.action == MotionEvent.ACTION_MOVE) {
            val newSquare = getBoardPos(event.x, event.y)
            if (newSquare != selectedSquare) {
                selectedSquare = Point(-1, -1)
            }
            invalidate()
            return true
        } else if (event?.action == MotionEvent.ACTION_UP) {
            if (selectedSquare.x != -1 && selectedSquare.y != -1) {
                Log.d(javaClass.simpleName, "yo moveInput $selectedSquare")
                Log.d(
                    javaClass.simpleName,
                    "isAgainstComputer $isAgainstComputer isHumanLocalGame ${gameInfo.isHumanLocalGame}"
                )
                audioManager?.playClick()
                if (isAgainstComputer) {
                    moveInputListener.makeMove(
                        gameInfo.player1.playerId,
                        selectedSquare.x,
                        selectedSquare.y
                    )
                } else if (gameInfo.isHumanLocalGame) {
                    val playerIdToMove = if (game.getToMove() == gameInfo.player1.xo)
                        gameInfo.player1.playerId
                    else
                        gameInfo.player2.playerId
                    moveInputListener.makeMove(
                        playerIdToMove,
                        selectedSquare.x,
                        selectedSquare.y
                    )
                } else {
                    if (game.getToMove() == gameInfo.player1.xo) {
                        moveInputListener.makeMove(
                            gameInfo.player1.playerId,
                            selectedSquare.x,
                            selectedSquare.y
                        )
                    }

                }
                selectedSquare = Point(-1, -1)
            }
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun getBoardPos(x: Float, y: Float): Point {
        val p = Point()
        if (!outerRect.contains(x.toInt(), y.toInt())) {
            p.x = -1
            p.y = -1
        } else {

            val aX = (outerRect.right - outerRect.left) / 3
            val aY = (outerRect.bottom - outerRect.top) / 3

            val bX = x - outerRect.left
            val bY = y - outerRect.top

            p.x = (bY / aY).toInt()
            p.y = (bX / aX).toInt()
        }
        return p
    }

    fun registerMoveListener(moveInputListener: MoveInputListener) {
        this.moveInputListener = moveInputListener
    }

    override fun gameEnd(checkEnd: GameState) {
        Log.d(javaClass.simpleName, "END - $checkEnd")
    }

    override fun gameStarting(
        game: Game,
        gameInfo: GameInfo
    ) {
        this.didYouWin = false
        this.didYouDraw = false
        this.didYouLose = false
        this.game = game
        this.gameInfo = gameInfo
        this.canvasHelper.updateStatusMsgs()

        if (gameInfo.player2 is ComputerPlayer) {
            isAgainstComputer = true
        }
        initXOPlayerTints()
        invalidate()

        if (gameInfo.player1.xo == game.getToMove()) {
            audioManager?.playClick()
        }
    }


    override fun moveDone(x: Int, y: Int, xo: String) {
        audioManager?.playPing()
        updateStatusMsg()
        invalidate()
    }

    private fun updateStatusMsg() {

    }

    override fun moveRejected() {
//        invalidate()
    }

    fun setAudioManager(audioManager: AudioManager?) {
        this.audioManager = audioManager
    }
}