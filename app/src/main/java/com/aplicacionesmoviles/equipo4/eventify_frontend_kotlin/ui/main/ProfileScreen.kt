package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.util.Locale
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.*
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    ProfileScreenContent(
        onEditProfileClick = onEditProfileClick,
        onLogout = onLogout,
        isLoading = viewModel.isLoading,
        profile = viewModel.profile,
        serviceCatalogs = viewModel.serviceCatalogs,
        albums = viewModel.albums,
        reviews = viewModel.reviews
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    onEditProfileClick: () -> Unit,
    onLogout: () -> Unit,
    isLoading: Boolean,
    profile: Profile?,
    serviceCatalogs: List<ServiceCatalog>,
    albums: List<Album>,
    reviews: List<Review>
) {
    var selectedTabIndex by remember { mutableStateOf(2) } // Reviews selected in Figma
    val tabs = listOf("Servicios", "Álbumes", "Reseñas")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2E2E8F))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Perfil Profesional", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { /* Back */ }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = onEditProfileClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                        }
                    }
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        val ratingAverage = if (reviews.isNotEmpty()) {
                            reviews.map { it.rating }.average()
                        } else {
                            5.0
                        }
                        ProfileHeader(
                            name = profile?.fullName ?: "Cargando...",
                            location = "${profile?.city ?: ""}, ${profile?.country ?: ""}",
                            email = profile?.email ?: "",
                            rating = String.format(Locale.getDefault(), "%.1f", ratingAverage),
                            reviewsCount = reviews.size.toString(),
                            profileType = if (profile?.type == "ORGANIZER") "Organizador Profesional" else "Anfitrión"
                        )
                    }

                    item {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.White,
                            contentColor = Color(0xFF2E2E8F),
                            divider = {}
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(text = title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                                )
                            }
                        }
                    }

                    // Tab Content based on selection
                    when (selectedTabIndex) {
                        0 -> { // Servicios
                            items(serviceCatalogs) { service ->
                                ProfileServiceCard(service)
                            }
                        }
                        1 -> { // Álbumes
                            items(albums) { album ->
                                AlbumItemCard(album)
                            }
                        }
                        2 -> { // Reseñas
                            item {
                                ReviewsHeader(count = reviews.size)
                            }
                            items(reviews) { review ->
                                ReviewItem(review)
                            }
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileServiceCard(service: ServiceCatalog) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1519741497674-611481863552?w=200&q=80",
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = service.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = service.category, color = Color.Gray, fontSize = 12.sp)
                Text(text = "S/ ${service.priceFrom.toInt()} - S/ ${service.priceTo.toInt()}", color = Color(0xFF2E2E8F), fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun AlbumItemCard(album: Album) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column {
            AsyncImage(
                model = album.photos?.firstOrNull() ?: "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=800&q=80",
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = album.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "${album.photos?.size ?: 0} fotos", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    location: String,
    email: String,
    rating: String,
    reviewsCount: String,
    profileType: String
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
            contentDescription = "Profile Picture",
            modifier = Modifier.size(100.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = profileType, color = Color(0xFF2E2E8F), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
            Text(text = " $location  ", color = Color.Gray, fontSize = 12.sp)
            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
            Text(text = " $email", color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                Text(text = " $rating", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "  |  $reviewsCount reseñas", color = Color.Gray, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ReviewsHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Mostrando $count reseñas", fontSize = 12.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF2E2E8F))
            Text(text = " Más recientes", color = Color(0xFF2E2E8F), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE8EAF6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = review.fullName.take(2).uppercase(), color = Color(0xFF2E2E8F), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = review.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = review.socialEventDate.substringBefore("T"), fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                        Text(text = " ${review.rating}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "\"${review.content}\"", fontSize = 14.sp, color = Color.DarkGray)
            
            if (review.fullName == "Mateo Vargas") { // Mimic Figma's chips
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(4.dp)) {
                        Text(text = "Bodas", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp)
                    }
                    Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(4.dp)) {
                        Text(text = "Decoración", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    EventifyfrontendkotlinTheme {
        ProfileScreenContent(
            onEditProfileClick = {},
            onLogout = {},
            isLoading = false,
            profile = Profile(
                id = 1,
                firstName = "Maria",
                lastName = "García",
                email = "maria@example.com",
                street = "Calle Real",
                number = "123",
                city = "Lima",
                postalCode = "15001",
                country = "Perú",
                type = "Organizer"
            ),
            serviceCatalogs = listOf(
                ServiceCatalog(1, 1, "Planificación Completa", "Descripción de servicio", "Bodas", 2000.0, 5000.0),
                ServiceCatalog(2, 1, "Decoración Temática", "Descripción de servicio", "Eventos", 1000.0, 3000.0)
            ),
            albums = listOf(
                Album(1, 1, "Boda de Ana & Juan", "Fotos del evento", listOf("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=800&q=80"))
            ),
            reviews = listOf(
                Review(1, "Excelente servicio, muy profesional y atenta a los detalles.", "Mateo Vargas", "2023-10-01T10:00:00", 5.0, 1),
                Review(2, "Me encantó la decoración, todo quedó hermoso.", "Lucía Méndez", "2023-09-15T10:00:00", 4.0, 1)
            )
        )
    }
}
