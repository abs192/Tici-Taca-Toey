package com.abs192.ticitacatoey.game

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import com.abs192.ticitacatoey.R

class DefaultColorSets(context: Context) {

    private var backgroundColor: Int
    private var foregroundColor: Int
    private var primaryColor: Int
    private var accentColor: Int

    init {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorBackground, typedValue, true)
        backgroundColor = typedValue.data
        context.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        accentColor = typedValue.data
        context.theme.resolveAttribute(R.attr.colorText, typedValue, true)
        foregroundColor = typedValue.data
        context.theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)
        primaryColor = typedValue.data
    }

    val computerColorSet = ColorSet(
//        Color.parseColor("#ABABAB"),
        primaryColor,
        foregroundColor,
        backgroundColor,
        accentColor,
        foregroundColor,
        Color.parseColor("#00EF00"),
        Color.parseColor("#C0DF00"),
        Color.parseColor("#94AC00"),
        Color.parseColor("#EFEFEF"),
        Color.parseColor("#EFEFEF"),
        Color.parseColor("#EFEFEF"),
        foregroundColor,
        Color.parseColor("#007400"),
        foregroundColor,
        foregroundColor
    )

}