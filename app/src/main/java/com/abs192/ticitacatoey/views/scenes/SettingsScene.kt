package com.abs192.ticitacatoey.views.scenes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
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
    private var switchDarkMode: Switch? = null
    private var rbTheme: RadioGroup? = null

    override fun initScene() {
        settingsLayout = layoutInflater.inflate(R.layout.layout_settings, null)
        addChildInMainLayout(settingsLayout!!)
        fadeIn()
        switchDarkMode = settingsLayout?.findViewById(R.id.settingsSwitchDarkMode)
        rbTheme = settingsLayout?.findViewById(R.id.settingsRBTheme)

        switchDarkMode?.isChecked = store.getDarkMOde() != Store.DarkMode.OFF
        rbTheme?.check(
            if (store.getTheme() == Store.Theme.BLUE) R.id.radioButtonBlue else
                R.id.radioButtonPink
        )

        switchDarkMode?.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                store.saveDarkMode(Store.DarkMode.OFF)
                listener.onDarkModeOffClicked()
            } else {
                store.saveDarkMode(Store.DarkMode.ON)
                listener.onDarkModeOnClicked()
            }
        }

        rbTheme?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonPink -> {
                    store.saveTheme(Store.Theme.PINK)
                    listener.OnThemePinkClicked()
                }
                R.id.radioButtonBlue
                -> {
                    store.saveTheme(Store.Theme.BLUE)
                    listener.OnThemeBlueClicked()
                }
            }
        }
    }

    override fun backPressed() {
        fadeOut()
        mainLayout.removeView(settingsLayout)
    }

    override fun fadeIn() {
        enableDisableViews(true, settingsLayout, switchDarkMode, rbTheme)
        animatorUtil.fadeIn(settingsLayout!!, AnimatorUtil.Duration.LONG, null)
    }

    override fun fadeInFast() {
        enableDisableViews(true, settingsLayout, switchDarkMode, rbTheme)
        animatorUtil.fadeIn(settingsLayout!!, AnimatorUtil.Duration.SHORT, null)
    }

    override fun fadeOut() {
        enableDisableViews(false, settingsLayout, switchDarkMode, rbTheme)
        animatorUtil.fadeOut(settingsLayout!!, AnimatorUtil.Duration.MEDIUM, null)
    }

    interface SettingsClickListener {
        fun onDarkModeOffClicked()
        fun onDarkModeOnClicked()
        fun OnThemePinkClicked()
        fun OnThemeBlueClicked()
    }

}