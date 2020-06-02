package com.abs192.ticitacatoey.views.canvas.grid

import android.graphics.*
import com.abs192.ticitacatoey.game.ColorSet
import com.abs192.ticitacatoey.views.canvas.CanvasHelper

class OpponentBanner(private val canvasHelper: CanvasHelper) {

    private val bannerPadding = 20

    private var opponentBannerRect = Rect()
    private var opponentIsDrawableRect = Rect()

    private val opponentBannerBackgroundPaint = Paint()
    private val opponentBannerEdgePaint = Paint()
    private val opponentBannerTextPaint = Paint()

    fun initRects() {
        opponentBannerRect = Rect(
            bannerPadding,
            0 - bannerPadding,
            canvasHelper.displayWidth - bannerPadding,
            canvasHelper.bannerSize
        )
        opponentIsDrawableRect = Rect(
            canvasHelper.displayWidth / 2 - canvasHelper.squareSize / 2,
            canvasHelper.bannerSize - canvasHelper.squareSize,
            canvasHelper.displayWidth / 2 + canvasHelper.squareSize / 2,
            canvasHelper.bannerSize
        )
    }

    fun initColors(colorSet: ColorSet) {
        opponentBannerTextPaint.color = colorSet.defaultTextColor
        opponentBannerTextPaint.textAlign = Paint.Align.CENTER
        opponentBannerTextPaint.textSize = opponentBannerRect.width() / 20F
        opponentBannerTextPaint.isFakeBoldText = true

        opponentBannerBackgroundPaint.color =
            canvasHelper.shadeColor(colorSet.opponentBaseColor, -20)

        val oppBannerBackgroundColor1 = canvasHelper.shadeColor(colorSet.foregroundColor, -20)
        val oppBannerBackgroundColor2 = canvasHelper.shadeColor(colorSet.opponentBaseColor, 20)
        val oppBannerRadialGradient = RadialGradient(
            opponentBannerRect.right / 2F,
            opponentBannerRect.top.toFloat(),
            opponentBannerRect.left / 2F,
            intArrayOf(oppBannerBackgroundColor1, oppBannerBackgroundColor2),
            null,
            Shader.TileMode.MIRROR
        )
        opponentBannerBackgroundPaint.shader = oppBannerRadialGradient
        opponentBannerBackgroundPaint.style = Paint.Style.FILL
        opponentBannerBackgroundPaint.alpha = 50

        opponentBannerEdgePaint.color = canvasHelper.shadeColor(colorSet.opponentBaseColor, 20)
        opponentBannerEdgePaint.style = Paint.Style.STROKE
        opponentBannerEdgePaint.strokeWidth = 15F
        opponentBannerEdgePaint.alpha = 150
    }

    fun draw(
        canvas: Canvas?,
        isItOpponentsMove: Boolean,
        playerName: String,
        playerId: String,
        xo: String
    ) {

        canvas?.drawRoundRect(
            RectF(opponentBannerRect), 15F, 15F, opponentBannerBackgroundPaint
        )

        if (isItOpponentsMove) {
            canvas?.drawRoundRect(
                RectF(opponentBannerRect), 15F, 15F, opponentBannerEdgePaint
            )
        }
        val rect = Rect(
            opponentBannerRect.left + bannerPadding,
            opponentBannerRect.top - bannerPadding,
            opponentBannerRect.right - bannerPadding,
            opponentBannerRect.bottom - bannerPadding
        )
        canvas?.drawText(
            "$playerName [$playerId]", rect.centerX().toFloat(),
            rect.centerY().toFloat(), opponentBannerTextPaint
        )
        canvasHelper.mXDrawable.alpha = 255
        canvasHelper.mODrawable.alpha = 255
        val rectBounds = Rect(
            opponentIsDrawableRect.left + (opponentIsDrawableRect.width() / 3),
            opponentIsDrawableRect.top + (opponentIsDrawableRect.height() / 3),
            opponentIsDrawableRect.right - (opponentIsDrawableRect.width() / 3),
            opponentIsDrawableRect.bottom - (opponentIsDrawableRect.height() / 3)
        )
//        val textRect = Rect(
//            opponentIsDrawableRect.left,
//            rectBounds.top - opponentBannerTextPaint.textSize.toInt(),
//            opponentIsDrawableRect.right,
//            rectBounds.top
//        )

        if (xo == "o") {
            canvasHelper.mODrawable.bounds = rectBounds
            canvas?.let { canvasHelper.mODrawable.draw(it) }
        } else if (xo == "x") {
            canvasHelper.mXDrawable.bounds = rectBounds
            canvas?.let { canvasHelper.mXDrawable.draw(it) }
        }
    }
}