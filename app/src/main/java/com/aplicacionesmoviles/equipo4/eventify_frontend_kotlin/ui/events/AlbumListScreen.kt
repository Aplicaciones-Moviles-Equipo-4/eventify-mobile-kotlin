package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Album
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@Composable
fun AlbumListScreen(
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    // We hoist the state and logic to a stateless Composable
    // This allows us to use it in Previews without a real ViewModel
    AlbumListScreenContent(
        isLoading = viewModel.isLoading,
        albums = viewModel.albums,
        onBackClick = onBackClick,
        onRefresh = { viewModel.loadAllData() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreenContent(
    isLoading: Boolean,
    albums: List<Album>,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        onRefresh()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Mis Álbumes", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add Album */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2E2E8F))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(albums) { album ->
                        AlbumCard(album)
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumCard(album: Album) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column {
            AsyncImage(
                model = album.photos?.firstOrNull() ?: "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=800&q=80",
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = album.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text(text = album.description, fontSize = 14.sp, color = Color.Gray, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(text = " ${album.photos?.size ?: 0} fotos", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumListScreenPreview() {
    val mockAlbums = listOf(
        Album(1, 1, "Boda Ana y Jose", "Fotos de la ceremonia y recepción", listOf("https://example.com/photo1.jpg")),
        Album(2, 1, "Graduación 2023", "Evento de fin de curso", emptyList())
    )
    EventifyfrontendkotlinTheme {
        // We use the stateless content Composable for the preview
        AlbumListScreenContent(
            isLoading = false,
            albums = mockAlbums,
            onBackClick = {}
        )
    }
}
