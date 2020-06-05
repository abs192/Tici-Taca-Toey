package com.abs192.ticitacatoey.views.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.Utils
import com.abs192.ticitacatoey.game.EndGameMsg

/**
 * Holds constants and util functions for all canvas drawing classes
 */
class CanvasHelper(context: Context) {

    private val utils = Utils()

    var displayWidth: Int = context.resources?.displayMetrics?.widthPixels!!
    var displayHeight: Int = context.resources?.displayMetrics?.heightPixels!!

    val mXDrawable = ContextCompat.getDrawable(context, R.drawable.x_shape)!!
    val mODrawable = ContextCompat.getDrawable(context, R.drawable.o_shape)!!

    var dividerSize = 15
    var squareSize = (displayWidth - 2 * dividerSize) / 3

    val bannerSize = displayHeight / 5
    val bannerPadding = 20

    private val endGameMsg = EndGameMsg()
    var winMsg = ""
    var drawMsg = ""
    var loseMsg = ""

    fun shadeColor(color: Int, percent: Int): Int {
        return Color.parseColor(utils.shadeColor(color, percent))
    }


    fun drawText(canvas: Canvas?, text: String, textRect: Rect, paint: Paint) {
        val backPaint = Paint(paint)
        backPaint.color = shadeColor(paint.color, -20)
        backPaint.alpha = 40
        backPaint.textSize = paint.textSize + 2
        canvas?.drawText(
            text, textRect.centerX().toFloat(),
            textRect.centerY().toFloat(), backPaint
        )
        canvas?.drawText(
            text, textRect.centerX().toFloat(),
            textRect.centerY().toFloat(), paint
        )
    }

    fun updateStatusMsgs() {
        winMsg = endGameMsg.win()
        drawMsg = endGameMsg.draw()
        loseMsg = endGameMsg.loss()
    }
}