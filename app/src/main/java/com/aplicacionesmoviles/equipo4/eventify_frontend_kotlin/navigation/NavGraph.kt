package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth.CreateProfileScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth.LoginScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth.RegisterScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events.*
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.main.MainScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object CreateEvent : Screen("createEvent")
    object EventDetail : Screen("eventDetail/{eventId}") {
        fun createRoute(eventId: String) = "eventDetail/$eventId"
    }
    object QuoteDetail : Screen("quoteDetail/{quoteId}") {
        fun createRoute(quoteId: String) = "quoteDetail/$quoteId"
    }
    object CreateQuote : Screen("createQuote")
    object CreateProfile : Screen("createProfile")
    object EditProfile : Screen("editProfile")
    object CreateService : Screen("createService/{serviceId}?") {
        fun createRoute(serviceId: Int? = null) = if (serviceId != null) "createService/$serviceId" else "createService"
    }
    object AlbumDetail : Screen("albumDetail/{albumId}") {
        fun createRoute(albumId: String) = "albumDetail/$albumId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate(Screen.CreateProfile.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.CreateProfile.route) {
            CreateProfileScreen(
                onBackClick = { navController.popBackStack() },
                onProfileCreated = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.EditProfile.route) {
            com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.main.EditProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                onEventClick = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onQuoteClick = { quoteId ->
                    navController.navigate(Screen.QuoteDetail.createRoute(quoteId))
                },
                onCreateQuoteClick = {
                    navController.navigate(Screen.CreateQuote.route)
                },
                onCreateServiceClick = {
                    navController.navigate(Screen.CreateService.createRoute())
                },
                onEditServiceClick = { serviceId ->
                    navController.navigate(Screen.CreateService.createRoute(serviceId))
                },
                onEditProfileClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.CreateService.route) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            CreateEditServiceScreen(
                serviceId = serviceId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EventDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.QuoteDetail.route) { backStackEntry ->
            val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
            QuoteDetailScreen(
                quoteId = quoteId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.CreateQuote.route) {
            CreateEditQuoteScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.AlbumDetail.route) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            AlbumDetailScreen(
                albumId = albumId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
