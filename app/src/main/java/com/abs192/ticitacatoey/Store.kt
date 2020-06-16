package com.abs192.ticitacatoey

import android.content.Context

class Store(private val activity: MainActivity) {

    private val settingsThemeKey = "key_theme"
    private val settingsProfileName = "key_profile_name"
    private val settingsDarkModeKey = "dark_mode"
    private val keyDEFAULT = 0

    fun getProfileName(): String {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString(settingsProfileName, "")!!
    }

    fun setProfileName(s: String): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.edit().putString(settingsProfileName, s).commit()
    }

    fun getTheme(): Theme {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val saved = sharedPref.getInt(settingsThemeKey, keyDEFAULT)
        return Theme.values()[saved]
    }

    fun saveTheme(t: Theme): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.edit().putInt(settingsThemeKey, Theme.values().indexOf(t)).commit()
    }

    fun getDarkMOde(): DarkMode {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val saved = sharedPref.getInt(settingsDarkModeKey, keyDEFAULT)
        return DarkMode.values()[saved]
    }

    fun saveDarkMode(t: DarkMode): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.edit().putInt(settingsDarkModeKey, DarkMode.values().indexOf(t)).commit()
    }


    enum class Theme {
        PINK,
        BLUE
    }

    enum class DarkMode {
        OFF,
        ON
    }
}