package com.abs192.ticitacatoey.views.canvas

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class GridCanvas(context: Context?, attributeSet: AttributeSet?) : View(context, attributeSet) {

    var squareSize: Int = context?.resources?.displayMetrics?.widthPixels!!

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}