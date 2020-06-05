package com.abs192.ticitacatoey.views.canvas.grid

import android.graphics.*
import com.abs192.ticitacatoey.game.ColorSet
import com.abs192.ticitacatoey.game.GameState
import com.abs192.ticitacatoey.views.canvas.CanvasHelper

class YourBanner(private val canvasHelper: CanvasHelper) {

    private var bannerRect = Rect()
    private var isDrawableRect = Rect()

    private val bannerTextPaint = Paint()
    private val bannerTextPaintAccent = Paint()
    private val bannerBackgroundPaint = Paint()
    private val bannerEdgePaint = Paint()
    private val bannerEdgePaintWin = Paint()


    fun initRects() {
        bannerRect = Rect(
            canvasHelper.bannerPadding,
            canvasHelper.displayHeight - canvasHelper.bannerSize,
            canvasHelper.displayWidth - canvasHelper.bannerPadding,
            canvasHelper.displayHeight + canvasHelper.bannerSize
        )

        isDrawableRect = Rect(
            canvasHelper.displayWidth / 2 - canvasHelper.squareSize / 2,
            canvasHelper.displayHeight - canvasHelper.bannerSize,
            canvasHelper.displayWidth / 2 + canvasHelper.squareSize / 2,
            canvasHelper.displayHeight - canvasHelper.bannerSize + canvasHelper.squareSize
        )
    }

    fun initColors(colorSet: ColorSet) {
        bannerTextPaint.color = colorSet.defaultTextColor
        bannerTextPaint.textAlign = Paint.Align.CENTER
        bannerTextPaint.textSize = bannerRect.width() / 20F
        bannerTextPaint.isFakeBoldText = true

        bannerTextPaintAccent.color = colorSet.accentColor
        bannerTextPaintAccent.textAlign = Paint.Align.CENTER
        bannerTextPaintAccent.textSize = bannerRect.width() / 15F
        bannerTextPaintAccent.isFakeBoldText = true

        bannerBackgroundPaint.color = canvasHelper.shadeColor(colorSet.playerBaseColor, -20)

        val yourBannerBackgroundColor1 = canvasHelper.shadeColor(colorSet.backgroundColor, -30)
        val yourBannerRadialGradient = RadialGradient(
            bannerRect.right / 2F,
            bannerRect.bottom.toFloat(),
            bannerRect.left / 2F,
            intArrayOf(yourBannerBackgroundColor1, Color.WHITE),
            null,
            Shader.TileMode.MIRROR
        )
        bannerBackgroundPaint.shader = yourBannerRadialGradient
        bannerBackgroundPaint.style = Paint.Style.FILL
        bannerBackgroundPaint.alpha = 100

        bannerEdgePaint.color = canvasHelper.shadeColor(colorSet.foregroundColor, -20)
        bannerEdgePaint.style = Paint.Style.STROKE
        bannerEdgePaint.strokeWidth = 15F
        bannerEdgePaint.alpha = 150

        bannerEdgePaintWin.color = canvasHelper.shadeColor(colorSet.primaryColor, 20)
        bannerEdgePaintWin.style = Paint.Style.FILL_AND_STROKE
        bannerEdgePaintWin.strokeWidth = 30F
        bannerEdgePaintWin.alpha = 100

    }

    fun draw(
        canvas: Canvas?,
        isItYourMove: Boolean,
        xo: String,
        gameState: GameState,
        statusMsg: String
    ) {
        canvas?.drawRoundRect(
            RectF(bannerRect), 15F, 15F, bannerBackgroundPaint
        )

        if (isItYourMove) {
            canvas?.drawRoundRect(
                RectF(bannerRect), 15F, 15F, bannerEdgePaint
            )
        }
        canvasHelper.mXDrawable.alpha = 255
        canvasHelper.mODrawable.alpha = 255
//        val rectBounds = Rect(
//            youAreDrawableRect.left + (youAreDrawableRect.width() / 3),
//            youAreDrawableRect.top + (youAreDrawableRect.height() / 3),
//            youAreDrawableRect.right - (youAreDrawableRect.width() / 3),
//            youAreDrawableRect.bottom - (youAreDrawableRect.height() / 3)
//        )
        val rectBounds = Rect(
            isDrawableRect.left + (isDrawableRect.width() / 3),
            isDrawableRect.top + (isDrawableRect.height() / 3) + canvasHelper.bannerSize / 10,
            isDrawableRect.right - (isDrawableRect.width() / 3),
            isDrawableRect.bottom - (isDrawableRect.height() / 3) + canvasHelper.bannerSize / 10
        )
        val textRect = Rect(
            bannerRect.left,
            rectBounds.top - bannerTextPaint.textSize.toInt(),
            bannerRect.right,
            rectBounds.top
        )

        if (xo == "o") {
            canvasHelper.mODrawable.bounds = rectBounds
            canvas?.let { canvasHelper.mODrawable.draw(it) }
        } else if (xo == "x") {
            canvasHelper.mXDrawable.bounds = rectBounds
            canvas?.let { canvasHelper.mXDrawable.draw(it) }
        }

        if ((xo == "x" && gameState == GameState.X_WIN) ||
            (xo == "o" && gameState == GameState.O_WIN)
        ) {
            //you win
            canvas?.drawRoundRect(
                RectF(bannerRect), 15F, 15F, bannerEdgePaintWin
            )
            canvasHelper.drawText(canvas, canvasHelper.winMsg, textRect, bannerTextPaint)

        } else if ((xo == "x" && gameState == GameState.O_WIN) ||
            (xo == "o" && gameState == GameState.X_WIN)
        ) {
            canvasHelper.drawText(canvas, canvasHelper.loseMsg, textRect, bannerTextPaint)
        } else if (gameState == GameState.DRAW) {
            canvasHelper.drawText(canvas, canvasHelper.drawMsg, textRect, bannerTextPaint)
        } else {
            canvasHelper.drawText(canvas, "You are", textRect, bannerTextPaint)
        }
    }

}