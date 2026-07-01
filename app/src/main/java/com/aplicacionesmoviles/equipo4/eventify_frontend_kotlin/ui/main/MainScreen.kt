package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.chat.ChatListScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.dashboard.DashboardScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events.MyEventsScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events.QuoteListScreen
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.AuthViewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Inicio : BottomNavItem("inicio", Icons.Outlined.Home, "Inicio")
    object Eventos : BottomNavItem("eventos", Icons.Outlined.CalendarToday, "Eventos")
    object Cotizaciones : BottomNavItem("cotizaciones", Icons.Outlined.Description, "Cotizaciones")
    object Mensajes : BottomNavItem("mensajes", Icons.Outlined.ChatBubbleOutline, "Mensajes")
    object Perfil : BottomNavItem("perfil", Icons.Outlined.Person, "Perfil")
}

@Composable
fun MainScreen(
    onEventClick: (String) -> Unit,
    onQuoteClick: (String) -> Unit,
    onCreateServiceClick: () -> Unit,
    onEditServiceClick: (Int) -> Unit,
    onEditProfileClick: () -> Unit,
    onAlbumClick: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenSubscription: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    organizerViewModel: OrganizerViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Inicio) }

    val items = listOf(
        BottomNavItem.Inicio,
        BottomNavItem.Eventos,
        BottomNavItem.Cotizaciones,
        BottomNavItem.Mensajes,
        BottomNavItem.Perfil
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF2E2E8F)
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF2E2E8F),
                            selectedTextColor = Color(0xFF2E2E8F),
                            indicatorColor = Color(0xFFE8EAF6),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                BottomNavItem.Inicio -> DashboardScreen(
                    onEventClick = onEventClick,
                    onOpenNotifications = onOpenNotifications,
                    onOpenCalendar = onOpenCalendar,
                    onOpenChat = { selectedItem = BottomNavItem.Mensajes },
                    viewModel = organizerViewModel
                )
                BottomNavItem.Eventos -> MyEventsScreen(
                    onEventClick = onEventClick,
                    viewModel = organizerViewModel
                )
                BottomNavItem.Cotizaciones -> QuoteListScreen(
                    onQuoteClick = onQuoteClick,
                    viewModel = organizerViewModel
                )
                BottomNavItem.Mensajes -> ChatListScreen(
                    onOpenChat = onOpenChat
                )
                BottomNavItem.Perfil -> ProfileScreen(
                    onEditProfileClick = onEditProfileClick,
                    onAlbumClick = onAlbumClick,
                    onCreateServiceClick = onCreateServiceClick,
                    onEditServiceClick = onEditServiceClick,
                    onOpenSubscription = onOpenSubscription,
                    onLogout = {
                        authViewModel.logout()
                        onLogout()
                    },
                    viewModel = organizerViewModel
                )
            }
        }
    }
}
