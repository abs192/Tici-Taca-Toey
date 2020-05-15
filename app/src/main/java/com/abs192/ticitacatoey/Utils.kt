package com.abs192.ticitacatoey

import android.util.Log
import java.lang.Integer.parseInt
import kotlin.math.abs

class Utils {

    fun shadeColor(colorInt: Int, percent: Int): String {
        Log.d(javaClass.simpleName, "$colorInt , $percent")
//        val color = abs(colorInt).toString(16)
        val color = java.lang.String.format("#%06X", 0xFFFFFF and colorInt)
        Log.d(javaClass.simpleName, "color $color")
        var r = parseInt(color.substring(1, 3), 16)
        var g = parseInt(color.substring(3, 5), 16)
        var b = parseInt(color.substring(5, 7), 16)

        Log.d(javaClass.simpleName, "rgb $r $g $b")

        r = r * (100 + percent) / 100
        g = g * (100 + percent) / 100
        b = b * (100 + percent) / 100

        Log.d(javaClass.simpleName, "rgb $r $g $b")


        if (r > 255)
            r = 255
        else if (r < 0)
            r = 0

        if (g > 255)
            g = 255
        else if (g < 0)
            g = 0

        if (b > 255)
            b = 255
        else if (b < 0)
            b = 0

        Log.d(javaClass.simpleName, "rgb $r $g $b")

        val rString = r.toString(16)
        val gString = g.toString(16)
        val bString = b.toString(16)

        Log.d(javaClass.simpleName, "rgb $rString $gString $bString")

        val rR = if (rString.length == 1) "0$rString" else rString
        val gG = if (gString.length == 1) "0$gString" else gString
        val bB = if (bString.length == 1) "0$bString" else bString


        Log.d(javaClass.simpleName, "rgb $rR $gG $bB")

        return "#$rR$gG$bB"
    }

}