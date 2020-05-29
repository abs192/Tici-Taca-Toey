package com.abs192.ticitacatoey

import android.content.Context

class Store(private val activity: MainActivity) {

    private val settingsThemeKey = "key_theme"
    private val DEFAULT_THEME = 1 // Light theme

    fun getBackgroundTheme(): Theme {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val saved = sharedPref.getInt(settingsThemeKey, DEFAULT_THEME)
        return Theme.values()[saved]
    }

    fun saveBackgroundTheme(t: Theme): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.edit().putInt(settingsThemeKey, Theme.values().indexOf(t)).commit()
    }


    enum class Theme {
        DARK,
        LIGHT
    }

}