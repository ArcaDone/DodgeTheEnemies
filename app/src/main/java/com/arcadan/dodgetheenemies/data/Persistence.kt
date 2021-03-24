package com.arcadan.dodgetheenemies.data

import android.content.Context
import android.content.SharedPreferences
import com.arcadan.dodgetheenemies.Dodgers

class Persistence {
    companion object {
        var instance = Persistence()
    }
    private lateinit var sharedPreferences: SharedPreferences

    fun saveString(key: String, value: String) {
        refreshPreferences()
        sharedPreferences.edit()?.putString(key, value)?.apply()
    }

    fun saveInt(key: String, value: Int) {
        refreshPreferences()
        sharedPreferences.edit()?.putInt(key, value)?.apply()
    }

    fun saveBool(key: String, value: Boolean) {
        refreshPreferences()
        sharedPreferences.edit()?.putBoolean(key, value)?.apply()
    }

    fun getString(key: String): String {
        refreshPreferences()
        return sharedPreferences.getString(key, "")!!
    }

    fun getInt(key: String): Int {
        refreshPreferences()
        return sharedPreferences.getInt(key, 0)
    }

    fun getBool(key: String): Boolean {
        refreshPreferences()
        return sharedPreferences.getBoolean(key, false)
    }

    private fun refreshPreferences() {
        sharedPreferences = Dodgers.context!!.getSharedPreferences("dodgeEnemies", Context.MODE_PRIVATE)
    }
}
