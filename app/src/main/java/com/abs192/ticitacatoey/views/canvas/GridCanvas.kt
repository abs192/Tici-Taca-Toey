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
import com.abs192.ticitacatoey.Game
import com.abs192.ticitacatoey.types.GameInfo

class GridCanvas(context: Context?, attributeSet: AttributeSet?) : View(context, attributeSet) {

    var displayWidth: Int = context?.resources?.displayMetrics?.widthPixels!!
    var displayHeight: Int = context?.resources?.displayMetrics?.heightPixels!!
    private val gridPaint = Paint()
    private var animator: ValueAnimator = ValueAnimator()

    private var propertyDividerSize = "anim_property_divider_size"
    private var dividerSize = 0

    private var outerRect = Rect()
    private val dividers = arrayListOf<RectF>()

    private val dividerColors = mutableListOf(
        Color.parseColor("#ABABAB"),
        Color.parseColor("#9A9A9A"),
        Color.parseColor("#898989"),
        Color.parseColor("#787878")
    )
    private var squareSize = (displayWidth - 2 * dividerSize) / 3

    private lateinit var game: Game
    private lateinit var gameInfo: GameInfo

    init {

        outerRect = Rect(
            0,
            (displayHeight / 2) - (displayWidth / 2),
            displayWidth,
            (displayHeight / 2) + (displayWidth / 2)
        )

        initAnim()
        dividerColors.shuffle()
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
            PropertyValuesHolder.ofInt(propertyDividerSize, 0, 15)

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
    }

    fun startGame(game: Game, gameInfo: GameInfo) {
        this.game = game
        this.gameInfo = gameInfo
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            Log.d(javaClass.simpleName, "${event.x} ${event.y}")
            return true
        }
        return super.onTouchEvent(event)
    }
}