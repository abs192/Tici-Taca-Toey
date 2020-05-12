package com.abs192.ticitacatoey.views.scenes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.abs192.ticitacatoey.views.AnimatorUtil

abstract class TTTScene(
    var context: Context,
    var layoutInflater: LayoutInflater,
    var mainLayout: ConstraintLayout,
    var sceneType: SceneType
) {

    protected var animatorUtil = AnimatorUtil()

    abstract fun initScene()

    public open fun backPressed() {}

    public open fun fadeIn() {}
    public open fun fadeOut() {}

    public open fun fadeInFast() {}


    protected fun enableDisableViews(
        enableDisableFlag: Boolean,
        vararg views: View?
    ) {
        views.forEach { it?.isEnabled = enableDisableFlag }
    }

    protected fun addChildInMainLayout(childView: View) {
        val set = ConstraintSet()
        mainLayout.addView(childView)

        set.clone(mainLayout)
        set.connect(
            childView.id,
            ConstraintSet.TOP,
            mainLayout.id,
            ConstraintSet.TOP,
            60
        )
        set.connect(
            childView.id,
            ConstraintSet.BOTTOM,
            mainLayout.id,
            ConstraintSet.BOTTOM,
            60
        )
        set.connect(
            childView.id,
            ConstraintSet.START,
            mainLayout.id,
            ConstraintSet.START,
            60
        )
        set.connect(
            childView.id,
            ConstraintSet.END,
            mainLayout.id,
            ConstraintSet.END,
            60
        )
        set.applyTo(mainLayout)
    }
}