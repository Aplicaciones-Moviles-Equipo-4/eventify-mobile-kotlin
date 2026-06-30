package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("eventify_prefs", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString("jwt_token", null)
        set(value) = prefs.edit().putString("jwt_token", value).apply()

    var profileId: Int
        get() = prefs.getInt("profile_id", -1)
        set(value) = prefs.edit().putInt("profile_id", value).apply()

    fun logout() {
        prefs.edit().clear().apply()
    }
}
