package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.dashboard

import androidx.compose.foundation.background
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
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    DashboardContent(
        onEventClick = onEventClick,
        isLoading = viewModel.isLoading,
        error = viewModel.error,
        userName = viewModel.profile?.firstName ?: "Organizador",
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
    isLoading: Boolean,
    error: String?,
    userName: String,
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
                        AppHeader()
                        Spacer(modifier = Modifier.height(24.dp))
                        GreetingSection(userName = userName)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        SummaryGrid(
                            rating = "4.8",
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
                            date = event.date,
                            location = event.place,
                            price = event.customerName, // Use customer name as subtitle
                            guests = event.status,
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
    price: String,
    guests: String,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = price, color = Color(0xFF2E2E8F), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(text = " $date", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "  •  ", color = Color.Gray)
                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(text = " $guests", color = Color.Gray, fontSize = 12.sp)
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
                            Text(text = "Hace 2 horas", fontSize = 12.sp, color = Color.Gray)
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
            isLoading = false,
            error = null,
            userName = "Marco",
            serviceCatalogsCount = 5,
            reviewsCount = 10,
            albumsCount = 3,
            quotes = emptyList(),
            socialEvents = emptyList()
        )
    }
}
