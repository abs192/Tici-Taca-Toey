package com.abs192.ticitacatoey.views.canvas.grid

import android.graphics.*
import com.abs192.ticitacatoey.game.ColorSet
import com.abs192.ticitacatoey.game.GameState
import com.abs192.ticitacatoey.views.canvas.CanvasHelper

class OpponentBanner(private val canvasHelper: CanvasHelper) {

    private val bannerPadding = 20

    private var bannerRect = Rect()
    private var isDrawableRect = Rect()

    private val bannerBackgroundPaint = Paint()
    private val bannerEdgePaint = Paint()
    private val bannerEdgePaintWin = Paint()
    private val bannerTextPaint = Paint()

    fun initRects() {
        bannerRect = Rect(
            bannerPadding,
            0 - bannerPadding,
            canvasHelper.displayWidth - bannerPadding,
            canvasHelper.bannerSize
        )
        isDrawableRect = Rect(
            canvasHelper.displayWidth / 2 - canvasHelper.squareSize / 2,
            canvasHelper.bannerSize - canvasHelper.squareSize,
            canvasHelper.displayWidth / 2 + canvasHelper.squareSize / 2,
            canvasHelper.bannerSize
        )
    }

    fun initColors(colorSet: ColorSet) {
        bannerTextPaint.color = colorSet.defaultTextColor
        bannerTextPaint.textAlign = Paint.Align.CENTER
        bannerTextPaint.textSize = bannerRect.width() / 20F
        bannerTextPaint.isFakeBoldText = true

        bannerBackgroundPaint.color =
            canvasHelper.shadeColor(colorSet.opponentBaseColor, -20)

        val oppBannerBackgroundColor1 = canvasHelper.shadeColor(colorSet.foregroundColor, -20)
        val oppBannerBackgroundColor2 = canvasHelper.shadeColor(colorSet.opponentBaseColor, 20)
        val oppBannerRadialGradient = RadialGradient(
            bannerRect.right / 2F,
            bannerRect.top.toFloat(),
            bannerRect.left / 2F,
            intArrayOf(oppBannerBackgroundColor1, oppBannerBackgroundColor2),
            null,
            Shader.TileMode.MIRROR
        )
        bannerBackgroundPaint.shader = oppBannerRadialGradient
        bannerBackgroundPaint.style = Paint.Style.FILL
        bannerBackgroundPaint.alpha = 50

        bannerEdgePaint.color = canvasHelper.shadeColor(colorSet.opponentBaseColor, 20)
        bannerEdgePaint.style = Paint.Style.STROKE
        bannerEdgePaint.strokeWidth = 15F
        bannerEdgePaint.alpha = 150

        bannerEdgePaintWin.color = canvasHelper.shadeColor(colorSet.primaryColor, 20)
        bannerEdgePaintWin.style = Paint.Style.FILL_AND_STROKE
        bannerEdgePaintWin.strokeWidth = 30F
        bannerEdgePaintWin.alpha = 150
    }

    fun draw(
        canvas: Canvas?,
        isItOpponentsMove: Boolean,
        playerName: String,
        playerId: String,
        xo: String,
        gameState: GameState,
        statusMsg: String,
        isHumanLocalGame: Boolean
    ) {

        canvas?.drawRoundRect(
            RectF(bannerRect), 15F, 15F, bannerBackgroundPaint
        )

        if (isItOpponentsMove) {
            canvas?.drawRoundRect(
                RectF(bannerRect), 15F, 15F, bannerEdgePaint
            )
        }
        val textRect = Rect(
            bannerRect.left + bannerPadding,
            bannerRect.top - bannerPadding,
            bannerRect.right - bannerPadding,
            bannerRect.bottom - bannerPadding
        )
        val rectBounds = Rect(
            isDrawableRect.left + (isDrawableRect.width() / 3),
            isDrawableRect.top + (isDrawableRect.height() / 3),
            isDrawableRect.right - (isDrawableRect.width() / 3),
            isDrawableRect.bottom - (isDrawableRect.height() / 3)
        )
        canvas?.save()
        if (isHumanLocalGame) {
            canvas?.translate(0F, canvasHelper.bannerSize / 5F)
            canvas?.rotate(180F, textRect.exactCenterX(), textRect.exactCenterY())
        }
        val playerNameText =
            if (isHumanLocalGame) "You are"
            else "$playerName - $playerId"
        canvasHelper.mXDrawable.alpha = 255
        canvasHelper.mODrawable.alpha = 255

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
            if (isHumanLocalGame) {
                canvasHelper.drawText(canvas, canvasHelper.winMsg, textRect, bannerTextPaint)
            } else {
                canvasHelper.drawText(canvas, playerNameText, textRect, bannerTextPaint)
            }
        } else if (isHumanLocalGame) {
            if ((xo == "x" && gameState == GameState.O_WIN) ||
                (xo == "o" && gameState == GameState.X_WIN)
            ) {
                canvasHelper.drawText(canvas, canvasHelper.loseMsg, textRect, bannerTextPaint)
            } else if (gameState == GameState.DRAW) {
                canvasHelper.drawText(canvas, canvasHelper.drawMsg, textRect, bannerTextPaint)
            } else {
                canvasHelper.drawText(canvas, "You are", textRect, bannerTextPaint)
            }
        } else {
            canvasHelper.drawText(canvas, playerNameText, textRect, bannerTextPaint)
        }
        canvas?.restore()


        if ((xo == "x" && gameState == GameState.X_WIN) ||
            (xo == "o" && gameState == GameState.O_WIN)
        ) {
            canvas?.drawRoundRect(
                RectF(bannerRect), 15F, 15F, bannerEdgePaintWin
            )
        }
    }
}