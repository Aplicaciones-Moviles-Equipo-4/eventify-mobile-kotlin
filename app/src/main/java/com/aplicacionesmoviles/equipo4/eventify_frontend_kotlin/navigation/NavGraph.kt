package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth.CreateProfileScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth.LoginScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth.RegisterScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events.*
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.main.MainScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

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
    object CreateService : Screen("createService?serviceId={serviceId}") {
        fun createRoute(serviceId: Int? = null) =
            if (serviceId != null) "createService?serviceId=$serviceId" else "createService"
    }
    object AlbumDetail : Screen("albumDetail/{albumId}") {
        fun createRoute(albumId: String) = "albumDetail/$albumId"
    }
    object Notifications : Screen("notifications")
    object Calendar : Screen("calendar")
    object Subscription : Screen("subscription")
    object ChatDetail : Screen("chatDetail/{contact}") {
        fun createRoute(contact: String) = "chatDetail/$contact"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    // Single OrganizerViewModel scoped to the Activity so every destination shares the same
    // loaded state (profile, catalogs, albums, quotes, reviews). This is what keeps edit/detail
    // screens populated instead of each creating an empty instance of its own.
    val organizerViewModel: OrganizerViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
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
                },
                viewModel = organizerViewModel
            )
        }
        composable(Screen.EditProfile.route) {
            com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.main.EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = organizerViewModel
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
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                },
                onOpenChat = { contact ->
                    navController.navigate(Screen.ChatDetail.createRoute(android.net.Uri.encode(contact)))
                },
                onOpenNotifications = {
                    navController.navigate(Screen.Notifications.route)
                },
                onOpenCalendar = {
                    navController.navigate(Screen.Calendar.route)
                },
                onOpenSubscription = {
                    navController.navigate(Screen.Subscription.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                organizerViewModel = organizerViewModel
            )
        }
        composable(
            route = Screen.CreateService.route,
            arguments = listOf(navArgument("serviceId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            CreateEditServiceScreen(
                serviceId = serviceId,
                onBackClick = { navController.popBackStack() },
                viewModel = organizerViewModel
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
                },
                viewModel = organizerViewModel
            )
        }
        composable(Screen.QuoteDetail.route) { backStackEntry ->
            val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
            QuoteDetailScreen(
                quoteId = quoteId,
                onBackClick = { navController.popBackStack() },
                viewModel = organizerViewModel
            )
        }
        composable(Screen.CreateQuote.route) {
            CreateEditQuoteScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = organizerViewModel
            )
        }
        composable(Screen.AlbumDetail.route) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            AlbumDetailScreen(
                albumId = albumId,
                onBackClick = { navController.popBackStack() },
                viewModel = organizerViewModel
            )
        }
        composable(Screen.Notifications.route) {
            com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.more.NotificationsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Calendar.route) {
            com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.more.CalendarScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = organizerViewModel
            )
        }
        composable(Screen.Subscription.route) {
            com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.more.SubscriptionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.ChatDetail.route) { backStackEntry ->
            val contact = backStackEntry.arguments?.getString("contact") ?: ""
            com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.chat.ChatDetailScreen(
                contactName = contact,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
