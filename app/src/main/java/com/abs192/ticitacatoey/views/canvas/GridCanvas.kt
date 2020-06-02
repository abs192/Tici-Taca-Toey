package com.abs192.ticitacatoey.views.canvas

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import com.abs192.ticitacatoey.game.*
import com.abs192.ticitacatoey.types.GameInfo
import com.abs192.ticitacatoey.views.canvas.grid.OpponentBanner
import com.abs192.ticitacatoey.views.canvas.grid.YourBanner
import java.util.*

class GridCanvas(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet),
    PostMoveEventListener {

    private val canvasHelper = CanvasHelper(context)

    private lateinit var game: Game
    private lateinit var gameInfo: GameInfo
    private lateinit var moveInputListener: MoveInputListener

    private var animator: ValueAnimator = ValueAnimator()
    private var propertyDividerSize = "anim_property_divider_size"

    private val dividers = arrayListOf<RectF>()
    private var squareRects = arrayListOf<Rect>()

    private val dividerColors = mutableListOf<Int>()

    private var selectedSquare = Point(-1, -1)
    private var winSquares = arrayListOf<Point>()

    private var bannerText = ""
    private var bannerSubText = ""
    private var isAgainstComputer = false

    private var didYouWin = false
    private var didYouLose = false
    private var didYouDraw = false

    private val opponentBanner = OpponentBanner(canvasHelper)
    private val yourBanner = YourBanner(canvasHelper)

    /**
     *  PAINT objects
     */
    private val gridPaint = Paint()
    private val gridBackgroundPaint = Paint()
    private val squarePaintNormal = Paint()
    private val squarePaintSelected = Paint()
    private val bannerTextNormalPaint = Paint()
    private val bannerSubTextNormalPaint = Paint()
    private val bannerBottomTextWinPaint = Paint()
    private val bannerBottomTextLosePaint = Paint()
    private val bannerBottomTextDrawPaint = Paint()

    /**
     * Rect objects
     */
    private var outerRect = Rect()
    private var topRect = Rect()
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

        yourBanner.initRects()
        opponentBanner.initRects()

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                squareRects.add(
                    Rect(
                        i * canvasHelper.squareSize + i * canvasHelper.dividerSize,
                        outerRect.top +
                                j * canvasHelper.squareSize + j * canvasHelper.dividerSize,
                        i * canvasHelper.squareSize + i * canvasHelper.dividerSize + canvasHelper.squareSize,
                        outerRect.top +
                                j * canvasHelper.squareSize + j * canvasHelper.dividerSize + canvasHelper.squareSize
                    )
                )
            }
        }

//        initAnim()
        initDividers()
        dividerColors.shuffle()
        gridPaint.strokeWidth = canvasHelper.dividerSize.toFloat()

        squarePaintNormal.color = Color.WHITE
        squarePaintNormal.alpha = 0

        bannerTextNormalPaint.color = Color.GRAY
        bannerTextNormalPaint.strokeMiter = 2F
        bannerTextNormalPaint.style = Paint.Style.FILL
        bannerTextNormalPaint.textAlign = Paint.Align.CENTER
        bannerTextNormalPaint.textSize = topRect.width() / 10F

        bannerSubTextNormalPaint.color = Color.CYAN
        bannerSubTextNormalPaint.textAlign = Paint.Align.CENTER
        bannerSubTextNormalPaint.textSize = topRect.width() / 20F

    }

    fun initColors(colorSet: ColorSet) {
        dividerColors.clear()
        for (i in 0 until 4) {
            dividerColors.add(canvasHelper.shadeColor(colorSet.dividerColor, i * -10))
        }
        gridPaint.style = Paint.Style.FILL

        gridBackgroundPaint.color = colorSet.backgroundColor
        gridBackgroundPaint.alpha = 150
        gridBackgroundPaint.strokeWidth = canvasHelper.dividerSize.toFloat()
        gridBackgroundPaint.style = Paint.Style.FILL

        squarePaintSelected.color = colorSet.accentColor
        squarePaintSelected.alpha = 100

        bannerTextNormalPaint.color = colorSet.bannerTextColor
        bannerSubTextNormalPaint.color = colorSet.bannerSubTextColor
//            Color.parseColor(utils.shadeColor(colorSet.bannerTextColor, -10))

        bannerBottomTextWinPaint.color = colorSet.gameWinSquareColor
        bannerBottomTextWinPaint.textAlign = Paint.Align.CENTER
        bannerBottomTextWinPaint.textSize = topRect.width() / 20F

        bannerBottomTextLosePaint.color = colorSet.gameLoseSquareColor
        bannerBottomTextLosePaint.textAlign = Paint.Align.CENTER
        bannerBottomTextLosePaint.textSize = topRect.width() / 20F

        bannerBottomTextDrawPaint.color = colorSet.gameDrawSquareColor
        bannerBottomTextDrawPaint.textAlign = Paint.Align.CENTER
        bannerBottomTextDrawPaint.textSize = topRect.width() / 20F

        yourBanner.initColors(colorSet)
        opponentBanner.initColors(colorSet)
    }

    private fun initDividers() {
        dividers.clear()
        dividers.add(
            RectF(
                Rect(
                    canvasHelper.squareSize,
                    outerRect.top,
                    canvasHelper.squareSize + canvasHelper.dividerSize,
                    outerRect.bottom
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    outerRect.left,
                    outerRect.top + canvasHelper.squareSize,
                    outerRect.right,
                    outerRect.top + canvasHelper.squareSize + canvasHelper.dividerSize
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    (2 * canvasHelper.squareSize) + canvasHelper.dividerSize,
                    outerRect.top,
                    (2 * canvasHelper.squareSize) + (2 * canvasHelper.dividerSize),
                    outerRect.bottom
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    outerRect.left,
                    outerRect.top + (2 * canvasHelper.squareSize) + canvasHelper.dividerSize,
                    outerRect.right,
                    outerRect.top + (2 * canvasHelper.squareSize) + (2 * canvasHelper.dividerSize)
                )
            )
        )
    }

    private fun initAnim() {
        val propertyDividerSize: PropertyValuesHolder =
            PropertyValuesHolder.ofInt(propertyDividerSize, 0, canvasHelper.dividerSize)

        animator.setValues(propertyDividerSize)
        animator.duration = 2000
        animator.addUpdateListener { animation ->
            canvasHelper.dividerSize = animation.getAnimatedValue(this.propertyDividerSize) as Int
            initDividers()
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBoard(canvas)
        drawXOs(canvas)
        drawSquares(canvas)
//        drawBanner(canvas)
//        drawBottomBanners(canvas)
        drawYourBanner(canvas)
        drawOpponentBanner(canvas)
    }

    private fun drawBoard(canvas: Canvas?) {
        gridBackgroundPaint.style = Paint.Style.FILL
        canvas?.drawRoundRect(RectF(outerRect), 15F, 15F, gridBackgroundPaint)
        gridBackgroundPaint.style = Paint.Style.STROKE
        canvas?.drawRoundRect(RectF(outerRect), 15F, 15F, gridBackgroundPaint)
        dividers.forEachIndexed { idx, it ->
            gridPaint.color = dividerColors[idx]
            canvas?.drawRoundRect(it, 5F, 5F, gridPaint)
        }
    }

    private fun drawOpponentBanner(canvas: Canvas?) {
        opponentBanner.draw(
            canvas,
            game.getToMove() == gameInfo.player2.xo,
            gameInfo.player2.name,
            gameInfo.player2.playerId,
            gameInfo.player2.xo
        )
    }

    private fun drawYourBanner(canvas: Canvas?) {
        yourBanner.draw(
            canvas,
            game.getToMove() == gameInfo.player1.xo,
            gameInfo.player1.xo
        )

    }

    private fun drawBottomBanners(canvas: Canvas?) {

        val rect1 = Rect(squareRects[2])
        rect1.offset(0, canvasHelper.squareSize)
        canvas?.drawText(
            "Wins", rect1.centerX().toFloat(),
            rect1.centerY().toFloat(), bannerBottomTextWinPaint
        )
        rect1.offset(0, bannerSubTextNormalPaint.textSize.toInt())
        canvas?.drawText(
            gameInfo.countWinsPlayer1.toString(), rect1.centerX().toFloat(),
            rect1.centerY().toFloat(), bannerBottomTextWinPaint
        )

        val rect2 = Rect(squareRects[5])
        rect2.offset(0, canvasHelper.squareSize)
        canvas?.drawText(
            "Draw", rect2.centerX().toFloat(),
            rect2.centerY().toFloat(), bannerBottomTextDrawPaint
        )
        rect2.offset(0, bannerBottomTextDrawPaint.textSize.toInt())
        canvas?.drawText(
            gameInfo.countDraws.toString(), rect2.centerX().toFloat(),
            rect2.centerY().toFloat(), bannerBottomTextDrawPaint
        )
        val rect3 = Rect(squareRects[8])
        rect3.offset(0, canvasHelper.squareSize)
        canvas?.drawText(
            "Loss", rect3.centerX().toFloat(),
            rect3.centerY().toFloat(), bannerBottomTextLosePaint
        )
        rect3.offset(0, bannerBottomTextLosePaint.textSize.toInt())
        canvas?.drawText(
            gameInfo.countWinsPlayer2.toString(), rect3.centerX().toFloat(),
            rect3.centerY().toFloat(), bannerBottomTextLosePaint
        )

        canvas?.drawLine(
            canvasHelper.displayWidth / 3F + canvasHelper.dividerSize / 2F,
            rect1.top.toFloat() + bannerBottomTextDrawPaint.textSize,
            canvasHelper.displayWidth / 3F + canvasHelper.dividerSize / 2F,
            rect1.bottom.toFloat() - 2 * bannerBottomTextDrawPaint.textSize,
            bannerBottomTextDrawPaint
        )
        canvas?.drawLine(
            2 * canvasHelper.displayWidth / 3F + canvasHelper.dividerSize,
            rect2.top.toFloat() + bannerBottomTextDrawPaint.textSize,
            2 * canvasHelper.displayWidth / 3F + canvasHelper.dividerSize,
            rect2.bottom.toFloat() - 2 * bannerBottomTextDrawPaint.textSize,
            bannerBottomTextDrawPaint
        )
    }

    private fun drawBanner(canvas: Canvas?) {
        val rect = Rect(topRect)
        canvas?.drawText(
            bannerText, rect.centerX().toFloat(),
            rect.centerY().toFloat(), bannerTextNormalPaint
        )
        rect.offset(0, bannerSubTextNormalPaint.textSize.toInt() * 2)
        canvas?.drawText(
            bannerSubText, rect.centerX().toFloat(),
            rect.centerY().toFloat(), bannerSubTextNormalPaint
        )
    }

    private fun drawSquares(canvas: Canvas?) {
        squareRects.forEachIndexed { _, rect ->
            canvas?.drawRect(rect, squarePaintNormal)
        }
        if (selectedSquare.x != -1 && selectedSquare.y != -1) {
            canvas?.drawRect(
                squareRects[(selectedSquare.y * 3) + (selectedSquare.x % 3)],
                squarePaintSelected
            )
        }
    }

    private fun drawXOs(canvas: Canvas?) {
        canvasHelper.mXDrawable.alpha = 255
        canvasHelper.mODrawable.alpha = 255
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val xo = game.getXO(j, i)
                val r = squareRects[(i * 3) + (j % 3)]
                val rectBounds = Rect(
                    r.left + (r.width() / 3),
                    r.top + (r.height() / 3),
                    r.right - (r.width() / 3),
                    r.bottom - (r.height() / 3)
                )
                if (xo == "o") {
                    canvasHelper.mODrawable.bounds = rectBounds
                    canvas?.let { canvasHelper.mODrawable.draw(it) }
                } else if (xo == "x") {
                    canvasHelper.mXDrawable.bounds = rectBounds
                    canvas?.let { canvasHelper.mXDrawable.draw(it) }
                }

            }
        }
    }

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

                if (isAgainstComputer) {
                    moveInputListener.makeMove(
                        gameInfo.player1.playerId,
                        selectedSquare.x,
                        selectedSquare.y
                    )
                } else {
                    val playerIdToMove = if (game.getToMove() == gameInfo.player1.xo)
                        gameInfo.player1.playerId
                    else
                        gameInfo.player2.playerId
                    moveInputListener.makeMove(
                        playerIdToMove,
                        selectedSquare.x,
                        selectedSquare.y
                    )
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
        bannerText = when (checkEnd) {
            GameState.DRAW -> {
                "That's a draw"
            }
            GameState.X_WIN -> {
                game.checkWin()
                "X WINS!"
            }
            GameState.O_WIN -> {
                "O WINS!"
            }
            GameState.ONGOING -> {
                "..."
            }
            GameState.STARTING -> {
                "Starting"
            }
        }
//        winAnimation()
        bannerSubText = "Restarting game..."
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

        if (gameInfo.player2 is ComputerPlayer) {
            isAgainstComputer = true
        }
        initXOPlayerTints()
        invalidate()
    }


    override fun moveDone(x: Int, y: Int, xo: String) {
        bannerText = game.getToMove().toUpperCase(Locale.getDefault()) + " to move"
        bannerSubText = "You are ${gameInfo.player1.xo}"
        invalidate()
    }

    override fun moveRejected() {
        bannerSubText =
            "Not your move.. you are ${gameInfo.player1.xo.toUpperCase(Locale.getDefault())}"
        invalidate()

    }
}