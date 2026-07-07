package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Album
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Review
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.ServiceCatalog
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.BrandGold
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.BrandIndigo
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.BrandIndigoContainer
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.util.formatSoles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerPublicDetailScreen(
    profileId: Int,
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(profileId) {
        viewModel.loadOrganizerDetail(profileId)
    }

    val profile = viewModel.viewedProfile
    val catalogs = viewModel.viewedCatalogs
    val albums = viewModel.viewedAlbums
    val reviews = viewModel.viewedReviews

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(profile?.fullName ?: "Organizador", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )

            if (viewModel.isLoadingViewed && profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandIndigo)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item { OrganizerHeader(profile, reviews) }

                    item { SectionTitle("Servicios") }
                    if (catalogs.isEmpty()) {
                        item { EmptyLine("Este organizador aún no publicó servicios.") }
                    } else {
                        items(catalogs) { PublicServiceCard(it) }
                    }

                    item { SectionTitle("Portafolio") }
                    if (albums.isEmpty()) {
                        item { EmptyLine("Sin álbumes en el portafolio.") }
                    } else {
                        items(albums) { PublicAlbumCard(it) }
                    }

                    item { SectionTitle("Reseñas") }
                    if (reviews.isEmpty()) {
                        item { EmptyLine("Todavía no tiene reseñas.") }
                    } else {
                        items(reviews) { PublicReviewCard(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganizerHeader(profile: com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Profile?, reviews: List<Review>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(88.dp).clip(CircleShape).background(BrandIndigoContainer),
            contentAlignment = Alignment.Center
        ) {
            if (!profile?.profileImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = profile?.profileImageUrl,
                    contentDescription = profile?.fullName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "${profile?.firstName?.firstOrNull() ?: ""}${profile?.lastName?.firstOrNull() ?: ""}".uppercase(),
                    color = BrandIndigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(profile?.fullName ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        if (!profile?.city.isNullOrBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Text(" ${profile?.city}", fontSize = 14.sp, color = Color.Gray)
            }
        }
        if (reviews.isNotEmpty()) {
            val avg = reviews.map { it.rating }.average()
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = BrandGold)
                Text(
                    " ${String.format(java.util.Locale.getDefault(), "%.1f", avg)}  ·  ${reviews.size} reseñas",
                    fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
    )
}

@Composable
private fun EmptyLine(text: String) {
    Text(text = text, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun PublicServiceCard(service: ServiceCatalog) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(service.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(service.category, fontSize = 12.sp, color = BrandIndigo)
            if (service.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(service.description, fontSize = 13.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${formatSoles(service.priceFrom)} - ${formatSoles(service.priceTo)}",
                color = BrandIndigo, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PublicAlbumCard(album: Album) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(album.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Black)
        if (album.description.isNotBlank()) {
            Text(album.description, fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp))
        val photos = album.photos ?: emptyList()
        if (photos.isEmpty()) {
            Text("Sin fotos", fontSize = 12.sp, color = Color.Gray)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photos) { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = album.title,
                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(10.dp)).background(BrandIndigoContainer),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun PublicReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.fullName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandGold)
                Text(" ${String.format(java.util.Locale.getDefault(), "%.0f", review.rating)}", fontSize = 13.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(review.content, fontSize = 13.sp, color = Color.DarkGray)
        }
    }
}
