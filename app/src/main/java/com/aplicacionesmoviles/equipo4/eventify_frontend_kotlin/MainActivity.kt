package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.SessionManager
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.NetworkModule
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.navigation.NavGraph
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.navigation.Screen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Restore a previous session: if we already have a token and a resolved profile,
        // skip the login screen and prime the HTTP client with the saved token.
        val session = SessionManager(this)
        val hasSession = !session.token.isNullOrEmpty() && session.profileId != -1
        if (hasSession) {
            NetworkModule.setAuthToken(session.token)
        }
        val startDestination = if (hasSession) Screen.Main.route else Screen.Login.route

        setContent {
            EventifyfrontendkotlinTheme {
                Surface(color = Color.White) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}
