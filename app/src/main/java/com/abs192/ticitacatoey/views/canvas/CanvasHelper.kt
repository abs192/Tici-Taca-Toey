package com.abs192.ticitacatoey.views.canvas

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.Utils

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


    fun shadeColor(color: Int, percent: Int): Int {
        return Color.parseColor(utils.shadeColor(color, percent))
    }

}