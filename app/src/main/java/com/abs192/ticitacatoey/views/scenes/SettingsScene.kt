package com.abs192.ticitacatoey.views.scenes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import com.abs192.ticitacatoey.R
import com.abs192.ticitacatoey.Store
import com.abs192.ticitacatoey.views.AnimatorUtil

class SettingsScene(
    context: Context,
    val store: Store,
    layoutInflater: LayoutInflater,
    mainLayout: ConstraintLayout,
    var listener: SettingsClickListener
) : TTTScene(context, layoutInflater, mainLayout, SceneType.NEW_GAME) {

    private var settingsLayout: View? = null
    private var switchTheme: Switch? = null

    override fun initScene() {
        settingsLayout = layoutInflater.inflate(R.layout.layout_settings, null)
        addChildInMainLayout(settingsLayout!!)
        fadeIn()
        switchTheme = settingsLayout?.findViewById(R.id.settingsSwitchTheme)

        switchTheme?.isChecked = store.getBackgroundTheme() != Store.Theme.LIGHT

        switchTheme?.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                store.saveBackgroundTheme(Store.Theme.LIGHT)
                listener.onThemeLightClicked()
            } else {
                store.saveBackgroundTheme(Store.Theme.DARK)
                listener.onThemeDarkClicked()
            }

        }
    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(settingsLayout)
    }

    override fun fadeIn() {
        enableDisableViews(true, settingsLayout, switchTheme)
        animatorUtil.fadeIn(settingsLayout!!, AnimatorUtil.Duration.LONG, null)
    }

    override fun fadeInFast() {
        enableDisableViews(true, settingsLayout, switchTheme)
        animatorUtil.fadeIn(settingsLayout!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(false, settingsLayout, switchTheme)
        animatorUtil.fadeOut(settingsLayout!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    interface SettingsClickListener {
        fun onThemeLightClicked()
        fun onThemeDarkClicked()
    }

}