package com.abs192.ticitacatoey.views.canvas

import android.animation.PropertyValuesHolder
import android.animation.TimeAnimator
import android.animation.TimeAnimator.TimeListener
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.Utils
import java.util.*
import kotlin.math.roundToInt

class BackgroundCanvas(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    private val baseSpeed = 200
    private val xoCount = 35
    private val randomSeed = 1994L

    private val scaleMinPart = 0.45f
    private val scaleRandomPart = 0.55f

    private val alphaScalePart = 0.5f
    private val alphaRandomPart = 0.5f

    private val colorTintScalePart = -20f
    private val colorTintRandomPart = 0.75f

    private val propertyZoom = "anim_property_zoom"

    private var mCurrentPlayTime = 0L
    private var mBaseSpeed = 0f
    private var mBaseSize = 0f

    private var zoomScale = 1F
    private var maxZoom = 1.2F
    private var minZoom = 1F
    private var zoomInAnimator = ValueAnimator()
    private var zoomOutAnimator = ValueAnimator()

    var mRnd = Random(randomSeed)
    private val xoArray: Array<XO?> = arrayOfNulls(xoCount)

    private var mTimeAnimator: TimeAnimator = TimeAnimator()
    private val mXDrawable = ContextCompat.getDrawable(context, R.drawable.x_shape)!!
    private val mODrawable = ContextCompat.getDrawable(context, R.drawable.o_shape)!!

    private var mXDrawableTint: Int = Color.parseColor("#EEEEEE")
    private var mODrawableTint: Int = Color.parseColor("#EEEEEE")

    private val utils = Utils()

    init {
        mBaseSize = mXDrawable.intrinsicWidth.coerceAtLeast(mXDrawable.intrinsicHeight) / 2f
        mBaseSpeed = baseSpeed * context.resources.displayMetrics.density
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.scale(zoomScale, zoomScale, width / 2F, height / 2F)
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
                    val tintColor = Color.parseColor(
                        utils.shadeColor(
                            mXDrawableTint,
                            (colorTintScalePart + colorTintRandomPart * mRnd.nextFloat()).toInt()
                        )
                    )
                    DrawableCompat.setTint(mXDrawable, tintColor)
//                    canvas?.scale(zoomScale, zoomScale)
                    // Draw the star to the canvas
                    canvas?.let { mXDrawable.draw(it) }

                }
                XOShape.O -> {

                    mODrawable.setBounds(-size, -size, size, size)
                    mODrawable.alpha = (255 * xo.alpha).roundToInt()
                    val tintColor = Color.parseColor(
                        utils.shadeColor(
                            mODrawableTint,
                            (colorTintScalePart + colorTintRandomPart * mRnd.nextFloat()).toInt()
                        )
                    )
                    DrawableCompat.setTint(mODrawable, tintColor)
//                    canvas?.scale(zoomScale, zoomScale)
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

        xo.shape = if (mRnd.nextInt(2) == 1) {
            XOShape.O
        } else {
            XOShape.X
        }

        xo.scale = scaleMinPart + scaleRandomPart * mRnd.nextFloat()

        xo.x = viewWidth * mRnd.nextFloat()
        xo.y = viewHeight.toFloat()

        xo.y += xo.scale * mBaseSize

        xo.y += viewHeight * mRnd.nextFloat() / 4f
        xo.alpha = alphaScalePart * xo.scale + alphaRandomPart * mRnd.nextFloat()
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
        if (mTimeAnimator.isPaused) {
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


    private fun startZoomOut() {

        if (zoomInAnimator.isRunning) {
            zoomInAnimator.end()
        }

        if (!zoomOutAnimator.isRunning && zoomScale != minZoom) {
            val propertyZoomIn: PropertyValuesHolder =
                PropertyValuesHolder.ofFloat(propertyZoom, maxZoom, minZoom)
            zoomOutAnimator = ValueAnimator()
            zoomOutAnimator.setValues(propertyZoomIn)
            zoomOutAnimator.duration = 1000L
            zoomOutAnimator.addUpdateListener { animation ->
                zoomScale = animation.getAnimatedValue(propertyZoom) as Float
            }
            zoomOutAnimator.start()
        }
    }

    private fun startZoomIn() {

        if (zoomOutAnimator.isRunning) {
            zoomOutAnimator.end()
        }
        if (!zoomInAnimator.isRunning && zoomScale != maxZoom) {
            if (zoomScale != 1.2F) {
                val propertyZoomIn: PropertyValuesHolder =
                    PropertyValuesHolder.ofFloat(propertyZoom, minZoom, maxZoom)
                zoomInAnimator = ValueAnimator()
                zoomInAnimator.setValues(propertyZoomIn)
                zoomInAnimator.duration = 1000L
                zoomInAnimator.addUpdateListener { animation ->
                    zoomScale = animation.getAnimatedValue(propertyZoom) as Float
                }
                zoomInAnimator.start()
            }
        }
    }

    fun setDrawableTints(xColor: String, oColor: String) {
        setDrawableTints(Color.parseColor(xColor), Color.parseColor(oColor))
    }

    fun setDrawableTints(xColor: Int, oColor: Int) {
        mXDrawableTint = xColor
        mODrawableTint = oColor
    }

    fun computerGameStart() {
        startZoomIn()
        setDrawableTints("#00AB00", "#00AB00")
    }

    fun normalTint() {
        startZoomOut()
        setDrawableTints("#EEEEEE", "#EEEEEE")
    }

    fun showErrorTint() {
        setDrawableTints("#EE1111", "#EE1111")
    }

    fun showBluetoothTint() {
        setDrawableTints("#287AA9", "#287AA9")
    }
}