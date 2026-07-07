package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TravelExplore
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
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Profile
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.AppHeader
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.EmptyState
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.BrandIndigo
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.BrandIndigoContainer
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@Composable
fun ExploreScreen(
    onOrganizerClick: (Int) -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadExploreProfiles()
    }

    var query by remember { mutableStateOf("") }
    val profiles = viewModel.exploreProfiles
    val filtered = if (query.isBlank()) profiles else profiles.filter {
        it.fullName.contains(query, ignoreCase = true) ||
            (it.city?.contains(query, ignoreCase = true) ?: false)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            AppHeader(
                profileImageUrl = viewModel.profile?.profileImageUrl,
                initials = viewModel.profile?.let {
                    "${it.firstName.firstOrNull() ?: ""}${it.lastName.firstOrNull() ?: ""}"
                } ?: "E"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Explorar organizadores", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                "Descubre a otros profesionales de eventos",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Buscar por nombre o ciudad...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            when {
                viewModel.isLoadingExplore -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandIndigo)
                    }
                }
                filtered.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.TravelExplore,
                        title = "Sin organizadores",
                        message = "Aún no hay otros organizadores para mostrar."
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filtered) { organizer ->
                            OrganizerCard(organizer, onClick = { onOrganizerClick(organizer.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganizerCard(organizer: Profile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(BrandIndigoContainer),
                contentAlignment = Alignment.Center
            ) {
                if (!organizer.profileImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = organizer.profileImageUrl,
                        contentDescription = organizer.fullName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "${organizer.firstName.firstOrNull() ?: ""}${organizer.lastName.firstOrNull() ?: ""}".uppercase(),
                        color = BrandIndigo,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(organizer.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                if (!organizer.city.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Text(" ${organizer.city}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
