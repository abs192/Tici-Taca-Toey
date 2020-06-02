package com.abs192.ticitacatoey.views.canvas.grid

import android.graphics.*
import com.abs192.ticitacatoey.game.ColorSet
import com.abs192.ticitacatoey.views.canvas.CanvasHelper

class YourBanner(private val canvasHelper: CanvasHelper) {

    private val bannerPadding = 20

    private var yourBannerRect = Rect()
    private var youAreDrawableRect = Rect()

    private val yourBannerTextPaint = Paint()
    private val yourBannerStatusTextPaint = Paint()
    private val yourBannerBackgroundPaint = Paint()
    private val yourBannerEdgePaint = Paint()


    fun initRects() {
        yourBannerRect = Rect(
            canvasHelper.bannerPadding,
            canvasHelper.displayHeight - canvasHelper.bannerSize,
            canvasHelper.displayWidth - canvasHelper.bannerPadding,
            canvasHelper.displayHeight + canvasHelper.bannerSize
        )

        youAreDrawableRect = Rect(
            canvasHelper.displayWidth / 2 - canvasHelper.squareSize / 2,
            canvasHelper.displayHeight - canvasHelper.bannerSize,
            canvasHelper.displayWidth / 2 + canvasHelper.squareSize / 2,
            canvasHelper.displayHeight - canvasHelper.bannerSize + canvasHelper.squareSize
        )
    }

    fun initColors(colorSet: ColorSet) {
        yourBannerTextPaint.color = colorSet.defaultTextColor
        yourBannerTextPaint.textAlign = Paint.Align.CENTER
        yourBannerTextPaint.textSize = yourBannerRect.width() / 20F
        yourBannerTextPaint.isFakeBoldText = true

        yourBannerBackgroundPaint.color = canvasHelper.shadeColor(colorSet.playerBaseColor, -20)

        val yourBannerBackgroundColor1 = canvasHelper.shadeColor(colorSet.backgroundColor, -30)
        val yourBannerRadialGradient = RadialGradient(
            yourBannerRect.right / 2F,
            yourBannerRect.bottom.toFloat(),
            yourBannerRect.left / 2F,
            intArrayOf(yourBannerBackgroundColor1, Color.WHITE),
            null,
            Shader.TileMode.MIRROR
        )
        yourBannerBackgroundPaint.shader = yourBannerRadialGradient
        yourBannerBackgroundPaint.style = Paint.Style.FILL
        yourBannerBackgroundPaint.alpha = 100

        yourBannerEdgePaint.color = canvasHelper.shadeColor(colorSet.foregroundColor, -20)
        yourBannerEdgePaint.style = Paint.Style.STROKE
        yourBannerEdgePaint.strokeWidth = 15F
        yourBannerEdgePaint.alpha = 150

    }

    fun draw(
        canvas: Canvas?,
        isItYourMove: Boolean,
        xo: String
    ) {

        canvas?.drawRoundRect(
            RectF(yourBannerRect), 15F, 15F, yourBannerBackgroundPaint
        )

        if (isItYourMove) {
            canvas?.drawRoundRect(
                RectF(yourBannerRect), 15F, 15F, yourBannerEdgePaint
            )
        }
        canvasHelper.mXDrawable.alpha = 255
        canvasHelper.mODrawable.alpha = 255
        val rectBounds = Rect(
            youAreDrawableRect.left + (youAreDrawableRect.width() / 3),
            youAreDrawableRect.top + (youAreDrawableRect.height() / 3),
            youAreDrawableRect.right - (youAreDrawableRect.width() / 3),
            youAreDrawableRect.bottom - (youAreDrawableRect.height() / 3)
        )
        val textRect = Rect(
            yourBannerRect.left,
            rectBounds.top - yourBannerTextPaint.textSize.toInt(),
            yourBannerRect.right,
            rectBounds.top
        )
        canvas?.drawText(
            "You are", textRect.centerX().toFloat(),
            textRect.centerY().toFloat(), yourBannerTextPaint
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