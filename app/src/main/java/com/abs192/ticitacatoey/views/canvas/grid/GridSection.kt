package com.abs192.ticitacatoey.views.canvas.grid

import android.graphics.*
import android.util.Log
import com.abs192.ticitacatoey.game.ColorSet
import com.abs192.ticitacatoey.game.Game
import com.abs192.ticitacatoey.game.GameState
import com.abs192.ticitacatoey.views.canvas.CanvasHelper

class GridSection(private val canvasHelper: CanvasHelper) {

    private var outerRect = Rect()

    private val gridPaint = Paint()
    private val gridBackgroundPaint = Paint()
    private val squarePaintNormal = Paint()
    private val squarePaintSelected = Paint()
    private val squarePaintWin = Paint()
    private val squarePaintDraw = Paint()

    private var squareRects = arrayListOf<Rect>()
    private val dividers = arrayListOf<RectF>()

    private val dividerColors = mutableListOf<Int>()

    fun initRects() {
        outerRect = Rect(
            0,
            (canvasHelper.displayHeight / 2) - (canvasHelper.displayWidth / 2),
            canvasHelper.displayWidth,
            (canvasHelper.displayHeight / 2) + (canvasHelper.displayWidth / 2)
        )

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                squareRects.add(
                    Rect(
                        i * canvasHelper.squareSize + i * canvasHelper.dividerSize,
                        outerRect.top +
                                j * canvasHelper.squareSize + j * canvasHelper.dividerSize,
                        i * canvasHelper.squareSize + i * canvasHelper.dividerSize + canvasHelper.squareSize,
                        outerRect.top +
                                j * canvasHelper.squareSize + j * canvasHelper.dividerSize + canvasHelper.squareSize
                    )
                )
            }
        }
        initDividers()
    }

    private fun initDividers() {
        dividers.clear()
        dividers.add(
            RectF(
                Rect(
                    canvasHelper.squareSize,
                    outerRect.top,
                    canvasHelper.squareSize + canvasHelper.dividerSize,
                    outerRect.bottom
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    outerRect.left,
                    outerRect.top + canvasHelper.squareSize,
                    outerRect.right,
                    outerRect.top + canvasHelper.squareSize + canvasHelper.dividerSize
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    (2 * canvasHelper.squareSize) + canvasHelper.dividerSize,
                    outerRect.top,
                    (2 * canvasHelper.squareSize) + (2 * canvasHelper.dividerSize),
                    outerRect.bottom
                )
            )
        )
        dividers.add(
            RectF(
                Rect(
                    outerRect.left,
                    outerRect.top + (2 * canvasHelper.squareSize) + canvasHelper.dividerSize,
                    outerRect.right,
                    outerRect.top + (2 * canvasHelper.squareSize) + (2 * canvasHelper.dividerSize)
                )
            )
        )
    }

    fun initColors(colorSet: ColorSet) {
        dividerColors.clear()
        for (i in 0 until 4) {
            dividerColors.add(canvasHelper.shadeColor(colorSet.primaryColor, i * -10))
        }
        dividers.shuffle()

        gridPaint.strokeWidth = canvasHelper.dividerSize.toFloat()

        gridPaint.style = Paint.Style.FILL

        gridBackgroundPaint.color = colorSet.backgroundColor
        gridBackgroundPaint.alpha = 150
        gridBackgroundPaint.strokeWidth = canvasHelper.dividerSize.toFloat()
        gridBackgroundPaint.style = Paint.Style.FILL

        squarePaintSelected.color = colorSet.accentColor
        squarePaintSelected.alpha = 100

        squarePaintNormal.color = Color.WHITE
        squarePaintNormal.alpha = 0

        squarePaintWin.color = colorSet.primaryColor
        squarePaintWin.alpha = 75

        squarePaintDraw.color = canvasHelper.shadeColor(colorSet.foregroundColor, -20)
        squarePaintDraw.alpha = 75
    }

    fun draw(
        canvas: Canvas?,
        selectedSquare: Point,
        game: Game
    ) {

        var drawEnd = false
        var ongoing = true
        if (game.currentState == GameState.X_WIN || game.currentState == GameState.O_WIN) {
            drawEnd = false
            ongoing = false
        } else if (game.currentState == GameState.DRAW) {
            drawEnd = true
            ongoing = false
        }

        gridBackgroundPaint.style = Paint.Style.FILL
        canvas?.drawRoundRect(RectF(outerRect), 15F, 15F, gridBackgroundPaint)
        gridBackgroundPaint.style = Paint.Style.STROKE
        canvas?.drawRoundRect(RectF(outerRect), 15F, 15F, gridBackgroundPaint)
        dividers.forEachIndexed { idx, it ->
            gridPaint.color = dividerColors[idx]
            canvas?.drawRoundRect(it, 5F, 5F, gridPaint)
        }
        Log.d("a", "ongoing $ongoing drawEnd $drawEnd ")
        squareRects.forEachIndexed { index, rect ->
            when {
                ongoing -> canvas?.drawRect(rect, squarePaintNormal)
                drawEnd -> canvas?.drawRect(rect, squarePaintDraw)
                index in game.highlightedIndices -> {
                    canvas?.drawRect(rect, squarePaintWin)
                }
                else -> {
                    canvas?.drawRect(rect, squarePaintNormal)
                }
            }
        }
        if (selectedSquare.x != -1 && selectedSquare.y != -1) {
            canvas?.drawRect(
                squareRects[(selectedSquare.y * 3) + (selectedSquare.x % 3)],
                squarePaintSelected
            )
        }
        canvasHelper.mXDrawable.alpha = 255
        canvasHelper.mODrawable.alpha = 255
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val xo = game.getXO(j, i)
                val r = squareRects[(i * 3) + (j % 3)]
                val rectBounds = Rect(
                    r.left + (r.width() / 3),
                    r.top + (r.height() / 3),
                    r.right - (r.width() / 3),
                    r.bottom - (r.height() / 3)
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


    }


}