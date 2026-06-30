package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.navigation.NavGraph
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventifyfrontendkotlinTheme {
                Surface(color = Color.White) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
