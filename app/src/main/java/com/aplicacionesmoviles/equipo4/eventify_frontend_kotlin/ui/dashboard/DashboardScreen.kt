package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.AppHeader
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Quote

@Composable
fun DashboardScreen(
    onEventClick: (String) -> Unit,
    onOpenNotifications: () -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    onOpenChat: () -> Unit = {},
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    val ratingAverage = if (viewModel.reviews.isNotEmpty()) {
        String.format(java.util.Locale.getDefault(), "%.1f", viewModel.reviews.map { it.rating }.average())
    } else {
        "—"
    }

    DashboardContent(
        onEventClick = onEventClick,
        onOpenNotifications = onOpenNotifications,
        onOpenCalendar = onOpenCalendar,
        onOpenChat = onOpenChat,
        isLoading = viewModel.isLoading,
        error = viewModel.error,
        userName = viewModel.profile?.firstName ?: "Organizador",
        ratingAverage = ratingAverage,
        serviceCatalogsCount = viewModel.serviceCatalogs.size,
        reviewsCount = viewModel.reviews.size,
        albumsCount = viewModel.albums.size,
        quotes = viewModel.quotes,
        socialEvents = viewModel.socialEvents
    )
}

@Composable
fun DashboardContent(
    onEventClick: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenChat: () -> Unit,
    isLoading: Boolean,
    error: String?,
    userName: String,
    ratingAverage: String,
    serviceCatalogsCount: Int,
    reviewsCount: Int,
    albumsCount: Int,
    quotes: List<Quote>,
    socialEvents: List<com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.SocialEvent>
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
        ) { innerPadding ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2E2E8F))
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        AppHeader(onBellClick = onOpenNotifications)
                        Spacer(modifier = Modifier.height(24.dp))
                        GreetingSection(userName = userName)
                        Spacer(modifier = Modifier.height(20.dp))
                        QuickActionsRow(
                            onOpenCalendar = onOpenCalendar,
                            onOpenChat = onOpenChat,
                            onOpenNotifications = onOpenNotifications
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        SummaryGrid(
                            rating = ratingAverage,
                            servicesCount = serviceCatalogsCount.toString(),
                            reviewsCount = reviewsCount.toString(),
                            albumsCount = albumsCount.toString()
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(text = "Próximos eventos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (socialEvents.isEmpty()) {
                            Text(
                                text = "No hay eventos programados en este momento.",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }

                    items(socialEvents.filter { it.status == "Active" }) { event ->
                        FeaturedEventCard(
                            title = event.title,
                            date = event.date.substringBefore("T"),
                            location = event.place,
                            customerName = event.customerName,
                            onEventClick = { onEventClick(event.id.toString()) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        PendingQuotesSection(quotes = quotes.filter { it.state == "PENDING" })
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(
    onOpenCalendar: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    val unread = com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore.unreadNotifications()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAction("Calendario", Icons.Default.CalendarToday, modifier = Modifier.weight(1f), onClick = onOpenCalendar)
        QuickAction("Mensajes", Icons.Outlined.ChatBubbleOutline, modifier = Modifier.weight(1f), onClick = onOpenChat)
        QuickAction("Alertas", Icons.Default.NotificationsNone, modifier = Modifier.weight(1f), badge = unread, onClick = onOpenNotifications)
    }
}

@Composable
private fun QuickAction(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    badge: Int = 0,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, contentDescription = label, tint = Color(0xFF2E2E8F))
                Spacer(modifier = Modifier.height(6.dp))
                Text(label, fontSize = 11.sp, color = Color(0xFF2E2E8F), fontWeight = FontWeight.Medium)
            }
            if (badge > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (badge > 9) "9+" else badge.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Column {
        Text(text = "¡Hola, $userName!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E8F))
        Text(text = "Tu resumen del día.", color = Color.Gray, fontSize = 16.sp)
    }
}

@Composable
fun SummaryGrid(
    rating: String,
    servicesCount: String,
    reviewsCount: String,
    albumsCount: String
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            SummaryCard(
                title = "Calificación promedio",
                value = rating,
                icon = Icons.Default.Star,
                modifier = Modifier.weight(1f),
                iconColor = Color(0xFFFFB300)
            )
            Spacer(modifier = Modifier.width(16.dp))
            SummaryCard(
                title = "Servicios publicados",
                value = servicesCount,
                icon = Icons.Outlined.Assignment,
                modifier = Modifier.weight(1f),
                iconColor = Color(0xFF2E2E8F)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            SummaryCard(
                title = "Reseñas totales",
                value = reviewsCount,
                icon = Icons.Default.ThumbUp,
                modifier = Modifier.weight(1f),
                iconColor = Color(0xFF2E2E8F)
            )
            Spacer(modifier = Modifier.width(16.dp))
            SummaryCard(
                title = "Álbumes creados",
                value = albumsCount,
                icon = Icons.Default.PhotoAlbum,
                modifier = Modifier.weight(1f),
                iconColor = Color(0xFF2E2E8F)
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconColor: Color = Color.Black
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun FeaturedEventCard(
    title: String,
    date: String,
    location: String,
    customerName: String,
    onEventClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=800&q=80",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(text = " $date", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "  •  ", color = Color.Gray)
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(text = " $location", color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(text = " $customerName", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onEventClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Ver detalles", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PendingQuotesSection(quotes: List<Quote>) {
    Column {
        Text(text = "Cotizaciones pendientes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        quotes.forEach { quote ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF2E2E8F))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = quote.title, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${quote.guestQuantity} invitados • S/ ${quote.totalPrice}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                    }
                }
            }
        }
        
        if (quotes.isEmpty()) {
            Text(text = "No hay cotizaciones pendientes.", color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    EventifyfrontendkotlinTheme {
        DashboardContent(
            onEventClick = {},
            onOpenNotifications = {},
            onOpenCalendar = {},
            onOpenChat = {},
            isLoading = false,
            error = null,
            userName = "Marco",
            ratingAverage = "4.8",
            serviceCatalogsCount = 5,
            reviewsCount = 10,
            albumsCount = 3,
            quotes = emptyList(),
            socialEvents = emptyList()
        )
    }
}
