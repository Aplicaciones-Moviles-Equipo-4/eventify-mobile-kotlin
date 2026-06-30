package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    albumId: String,
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }

    val album = remember(albumId, viewModel.albums) {
        viewModel.albums.find { it.id.toString() == albumId }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isUploading = true
            // Convert URI to File Part for Multipart
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            viewModel.uploadAlbumImage(body) { secureUrl ->
                album?.let { currentAlbum ->
                    val updatedPhotos = (currentAlbum.photos ?: emptyList()) + secureUrl
                    viewModel.updateAlbum(currentAlbum.id, currentAlbum.copy(photos = updatedPhotos)) {
                        isUploading = false
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Álbum") },
            text = { Text("¿Estás seguro de que deseas eliminar este álbum por completo?") },
            confirmButton = {
                TextButton(onClick = {
                    album?.let { viewModel.deleteAlbum(it.id) { onBackClick() } }
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(album?.title ?: "Álbum", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = album?.description ?: "",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clickable { launcher.launch("image/*") },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (isUploading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF2E2E8F))
                                        Text("Añadir foto", color = Color(0xFF2E2E8F), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    items(album?.photos ?: emptyList()) { photoUrl ->
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumDetailScreenPreview() {
    EventifyfrontendkotlinTheme {
        AlbumDetailScreen(albumId = "1", onBackClick = {})
    }
}
