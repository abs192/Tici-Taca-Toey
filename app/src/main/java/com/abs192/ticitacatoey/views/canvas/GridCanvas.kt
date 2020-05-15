package com.abs192.ticitacatoey.views.canvas

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.Utils
import com.abs192.ticitacatoey.game.*
import com.abs192.ticitacatoey.types.GameInfo
import java.util.*

class GridCanvas(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet),
    PostMoveEventListener {

    private val utils = Utils()

    var displayWidth: Int = context.resources?.displayMetrics?.widthPixels!!
    var displayHeight: Int = context.resources?.displayMetrics?.heightPixels!!
    private val gridPaint = Paint()

    private val squarePaintNormal = Paint()
    private val squarePaintSelected = Paint()
    private val squarePaintWin = Paint()
    private val squarePaintDraw = Paint()
    private var animator: ValueAnimator = ValueAnimator()

    private var propertyDividerSize = "anim_property_divider_size"

    private var dividerSize = 15
    private val finalDividerSize = 15

    private var outerRect = Rect()
    private var topRect = Rect()
    private var bottomRect = Rect()
    private var bottomRectWin = Rect()
    private var bottomRectDraw = Rect()
    private var bottomRectLoss = Rect()

    private val mXDrawable = ContextCompat.getDrawable(context, R.drawable.x_shape)!!

    private val mODrawable = ContextCompat.getDrawable(context, R.drawable.o_shape)!!
    private val dividers = arrayListOf<RectF>()

    private var squareRects = arrayListOf<Rect>()
    private val dividerColors = mutableListOf(
        Color.parseColor("#ABABAB"),
        Color.parseColor("#9A9A9A"),
        Color.parseColor("#898989"),
        Color.parseColor("#787878")
    )

    private var selectedSquare = Point(-1, -1)

    private var winSquares = arrayListOf<Point>()
    private var squareSize = (displayWidth - 2 * dividerSize) / 3

    private lateinit var game: Game
    private lateinit var gameInfo: GameInfo

    private lateinit var moveInputListener: MoveInputListener
    private var bannerText = ""
    private var bannerSubText = ""
    private val bannerTextNormalPaint = Paint()
    private val bannerSubTextNormalPaint = Paint()
    private val bannerBottomTextWinPaint = Paint()
    private val bannerBottomTextLosePaint = Paint()
    private val bannerBottomTextDrawPaint = Paint()

    init {
        topRect = Rect(
            0,
            0, displayWidth,
            (displayHeight / 2) - (displayWidth / 2)
        )

        outerRect = Rect(
            0,
            (displayHeight / 2) - (displayWidth / 2),
            displayWidth,
            (displayHeight / 2) + (displayWidth / 2)
        )

        bottomRect = Rect(
            0,
            (displayHeight / 2) + (displayWidth / 2),
            displayWidth,
            displayHeight
        )

        bottomRectWin = Rect(
            0,
            (displayHeight / 2) + (displayWidth / 2),
            displayWidth / 3,
            displayHeight
        )
        bottomRectDraw = Rect(
            displayWidth / 3,
            (displayHeight / 2) + (displayWidth / 2),
            2 * displayWidth / 3,
            displayHeight
        )
        bottomRectLoss = Rect(
            2 * displayWidth / 3,
            (displayHeight / 2) + (displayWidth / 2),
            displayWidth,
            displayHeight
        )

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                squareRects.add(
                    Rect(
                        i * squareSize + i * dividerSize,
                        outerRect.top +
                                j * squareSize + j * dividerSize,
                        i * squareSize + i * dividerSize + squareSize,
                        outerRect.top +
                                j * squareSize + j * dividerSize + squareSize
                    )
                )
            }
        }

//        initAnim()
        dividerSize = finalDividerSize
        initDividers()
        dividerColors.shuffle()

        squarePaintNormal.color = Color.WHITE
        squarePaintNormal.alpha = 0

        squarePaintSelected.color = Color.BLUE
        squarePaintSelected.alpha = 100

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
//        dividerColors
        for (i in 0 until 4) {
            dividerColors[i] = Color.parseColor(utils.shadeColor(colorSet.dividerColor, i * -10))
        }
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

    }

    private fun initDividers() {
        dividers.clear()
        dividers.add(
            RectF(
                Rect(
                    squareSize,
                    outerRect.top,
                    squareSize + dividerSize,
                    outerRect.bottom
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    outerRect.left,
                    outerRect.top + squareSize,
                    outerRect.right,
                    outerRect.top + squareSize + dividerSize
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    (2 * squareSize) + dividerSize,
                    outerRect.top,
                    (2 * squareSize) + (2 * dividerSize),
                    outerRect.bottom
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    outerRect.left,
                    outerRect.top + (2 * squareSize) + dividerSize,
                    outerRect.right,
                    outerRect.top + (2 * squareSize) + (2 * dividerSize)
                )
            )
        )
    }

    private fun initAnim() {
        val propertyDividerSize: PropertyValuesHolder =
            PropertyValuesHolder.ofInt(propertyDividerSize, 0, finalDividerSize)

        animator.setValues(propertyDividerSize)
        animator.duration = 2000
        animator.addUpdateListener { animation ->
            dividerSize = animation.getAnimatedValue(this.propertyDividerSize) as Int
            initDividers()
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dividers.forEachIndexed { idx, it ->
            gridPaint.color = dividerColors[idx]
            canvas?.drawRoundRect(it, 5F, 5F, gridPaint)
        }
        drawXOs(canvas)
        drawSquares(canvas)
        drawBanner(canvas)
        drawBottomBanners(canvas)
    }

    private fun drawBottomBanners(canvas: Canvas?) {

        val rect1 = Rect(squareRects[2])
        rect1.offset(0, squareSize)
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
        rect2.offset(0, squareSize)
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
        rect3.offset(0, squareSize)
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
            displayWidth / 3F + finalDividerSize / 2F,
            rect1.top.toFloat() + bannerBottomTextDrawPaint.textSize,
            displayWidth / 3F + finalDividerSize / 2F,
            rect1.bottom.toFloat() - 2 * bannerBottomTextDrawPaint.textSize,
            bannerBottomTextDrawPaint
        )
        canvas?.drawLine(
            2 * displayWidth / 3F + finalDividerSize,
            rect2.top.toFloat() + bannerBottomTextDrawPaint.textSize,
            2 * displayWidth / 3F + finalDividerSize,
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
        squareRects.forEachIndexed { index, rect ->
            canvas?.drawRect(rect, squarePaintNormal)
        }
//        if (selectedSquare.x != -1 && selectedSquare.y != -1) {
//            canvas?.drawRect(
//                squareRects[(selectedSquare.x * 3) + (selectedSquare.y % 3)],
//                squarePaintSelected
//            )
//        }
    }

    private fun drawXOs(canvas: Canvas?) {
        mXDrawable.alpha = 255
        mODrawable.alpha = 255
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
                    mODrawable.bounds = rectBounds
                    canvas?.let { mODrawable.draw(it) }
                } else if (xo == "x") {
                    mXDrawable.bounds = rectBounds
                    canvas?.let { mXDrawable.draw(it) }
                }

            }
        }
    }

    private fun initXOPlayerTints() {

        if (gameInfo.player1.xo == "x") {
            DrawableCompat.setTint(mXDrawable, gameInfo.colorSet.player1TintColor)
        } else {
            DrawableCompat.setTint(mODrawable, gameInfo.colorSet.player1TintColor)
        }

        if (gameInfo.player2.xo == "x") {
            DrawableCompat.setTint(mXDrawable, gameInfo.colorSet.player2TintColor)
        } else {
            DrawableCompat.setTint(mODrawable, gameInfo.colorSet.player2TintColor)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val p = getBoardPos(event.x, event.y)
            selectedSquare = Point(-1, -1)
            invalidate()
        } else if (event?.action == MotionEvent.ACTION_DOWN) {
            selectedSquare = getBoardPos(event.x, event.y)
            if (selectedSquare.x != -1 && selectedSquare.y != -1) {
                Log.d(javaClass.simpleName, "yo moveInput $selectedSquare")
                moveInputListener.makeMove(
                    gameInfo.player1.playerId,
                    selectedSquare.x,
                    selectedSquare.y
                )
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
        bannerSubText = "Restarting game..."
    }

    override fun gameStarting(
        game: Game,
        gameInfo: GameInfo
    ) {
        this.game = game
        this.gameInfo = gameInfo
        initBannerText()
        initXOPlayerTints()
        invalidate()
    }


    override fun moveDone(x: Int, y: Int, xo: String) {
        bannerText = game.getToMove().toUpperCase(Locale.getDefault()) + " to move"
        bannerSubText = "You are ${gameInfo.player1.xo}"
        initBannerText()
        invalidate()
    }

    override fun moveRejected() {
        bannerSubText =
            "Not your move.. you are ${gameInfo.player1.xo.toUpperCase(Locale.getDefault())}"
        invalidate()

    }

    private fun initBannerText() {
//        bannerSubText = if (gameInfo.player1.xo == game.getToMove())
//            "Your move"
//        else
//            ""
        bannerText = game.getToMove().toUpperCase(Locale.getDefault()) + " to move"
        bannerSubText = "You are ${gameInfo.player1.xo.toUpperCase(Locale.getDefault())}"
    }
}