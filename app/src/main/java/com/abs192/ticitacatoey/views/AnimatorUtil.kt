package com.abs192.ticitacatoey.views

import android.animation.AnimatorListenerAdapter
import android.view.View

class AnimatorUtil {

    val TIMESHORT = 200L
    private val TIMEMEDIUM = 500L
    private val TIMELONG = 1000L

    fun fadeOut(
        view: View?, duration: Duration, listenerAdapter:
        AnimatorListenerAdapter?
    ) {
        view?.alpha = 1f
        val animate = view?.animate()
        animate?.alpha(0f)?.duration = duration.timeMillis
        listenerAdapter?.let { animate?.setListener(listenerAdapter) }
        animate?.start()
    }

    fun fadeIn(
        view: View?, duration: Duration, listenerAdapter:
        AnimatorListenerAdapter?
    ) {
        view?.alpha = 0f
        val animate = view?.animate()
        animate?.alpha(1f)?.duration = duration.timeMillis
        listenerAdapter?.let { animate?.setListener(listenerAdapter) }
        animate?.start()
    }

    enum class Duration(var timeMillis: Long) {
        SHORT(200L),
        MEDIUM(500L),
        LONG(1000L)
    }
}