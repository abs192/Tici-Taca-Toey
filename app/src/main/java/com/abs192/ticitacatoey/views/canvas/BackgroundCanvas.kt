package com.abs192.ticitacatoey.views.canvas

import android.animation.TimeAnimator
import android.animation.TimeAnimator.TimeListener
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.abs192.ticitacatoey.R
import java.util.*
import kotlin.math.roundToInt

class BackgroundCanvas(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    private val baseSpeed = 200
    private val XO_COUNT = 32
    private val RANDOM_SEED = 1337L

    private val SCALEMINPART = 0.45f
    private val SCALERANDOMPART = 0.55f
    private val ALPHASCALEPART = 0.5f
    private val ALPHARANDOMPART = 0.5f


    private var mCurrentPlayTime = 0L
    private var mBaseSpeed = 0f

    private var mBaseSize = 0f

    var mRnd = Random(RANDOM_SEED)
    private val xoArray: Array<XO?> = arrayOfNulls(XO_COUNT)

    private var mTimeAnimator: TimeAnimator = TimeAnimator()
    private val mXDrawable = ContextCompat.getDrawable(context, R.drawable.x_shape)!!
    private val mODrawable = ContextCompat.getDrawable(context, R.drawable.o_shape)!!

    var squareSize: Int = context.resources?.displayMetrics?.widthPixels!!

    init {
        mBaseSize = mXDrawable.intrinsicWidth.coerceAtLeast(mXDrawable.intrinsicHeight) / 2f
        mBaseSpeed = baseSpeed * context.resources.displayMetrics.density
    }

    override fun onDraw(canvas: Canvas?) {
        val viewHeight = height
        for (xoo in xoArray) {
            val xo = xoo!!
            // Ignore the star if it's outside of the view bounds
            val starSize = xo.scale.times(mBaseSize)
            if (xo.y + starSize < 0 || xo.y - starSize > viewHeight) {
                continue
            }

            // Save the current canvas state
            val save = canvas?.save()
            // Move the canvas to the center of the star
            canvas?.translate(xo.x, xo.y)

            // Rotate the canvas based on how far the star has moved
            val progress: Float = (xo.y + starSize) / viewHeight
            canvas?.rotate(360 * progress)

            // Prepare the size and alpha of the drawable
            val size = starSize.roundToInt()

            when (xo.shape) {
                XOShape.X -> {

                    mXDrawable.setBounds(-size, -size, size, size)
                    mXDrawable.alpha = (255 * xo.alpha).roundToInt()

                    // Draw the star to the canvas
                    canvas?.let { mXDrawable.draw(it) }

                }
                XOShape.O -> {

                    mODrawable.setBounds(-size, -size, size, size)
                    mODrawable.alpha = (255 * xo.alpha).roundToInt()

                    // Draw the star to the canvas
                    canvas?.let { mODrawable.draw(it) }

                }
            }
            // Restore the canvas to it's previous position and rotation
            save?.let { canvas.restoreToCount(it) }
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)

        for (i in xoArray.indices) {
            xoArray[i] = initXO(XO(), width, height)
        }
    }


    private fun initXO(
        xo: XO,
        viewWidth: Int,
        viewHeight: Int
    ): XO {

        xo.shape = if (mRnd.nextInt() % 2 == 1)
            XOShape.X
        else
            XOShape.O

        xo.scale = SCALEMINPART + SCALERANDOMPART * mRnd.nextFloat()

        xo.x = viewWidth * mRnd.nextFloat()
        xo.y = viewHeight.toFloat()

        xo.y += xo.scale * mBaseSize

        xo.y += viewHeight * mRnd.nextFloat() / 4f
        xo.alpha = ALPHASCALEPART * xo.scale + ALPHARANDOMPART * mRnd.nextFloat()
        xo.speed = mBaseSpeed * xo.alpha * xo.scale
        return xo
    }

    private class XO {
        var shape: XOShape =
            XOShape.O
        var x = 0f
        var y = 0f
        var scale = 0f
        var alpha = 0f
        var speed = 0f
    }

    private enum class XOShape {
        O, X
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mTimeAnimator.setTimeListener(TimeListener { _, _, deltaTime ->
            if (!isLaidOut) {
                return@TimeListener
            }
            updateState(deltaTime.toFloat())
            invalidate()
        })
        mTimeAnimator.start()
    }

    private fun updateState(deltaMs: Float) {
        val deltaSeconds = deltaMs / 1000f
        val viewWidth = width
        val viewHeight = height
        for (xo in xoArray) { // Move the star based on the elapsed time and it's speed
            xo?.y = xo?.y?.minus(xo.speed.times(deltaSeconds))!!

            val size: Float = xo.scale * mBaseSize
            if (xo.y + size < 0) {
                initXO(xo, viewWidth, viewHeight)
            }
        }
    }

    fun pause() {
        if (mTimeAnimator.isRunning) { // Store the current play time for later.
            mCurrentPlayTime = mTimeAnimator.currentPlayTime
            mTimeAnimator.pause()
        }
    }

    fun resume() {
        if (mTimeAnimator != null && mTimeAnimator.isPaused) {
            mTimeAnimator.start()
            mTimeAnimator.currentPlayTime = mCurrentPlayTime
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mTimeAnimator.cancel()
        mTimeAnimator.setTimeListener(null)
        mTimeAnimator.removeAllListeners()
    }

}