package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.ServiceCatalog
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditServiceScreen(
    serviceId: String? = null,
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceFrom by remember { mutableStateOf("") }
    var priceTo by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val existingService = remember(serviceId, viewModel.serviceCatalogs) {
        serviceId?.toIntOrNull()?.let { id ->
            viewModel.serviceCatalogs.find { it.id == id }
        }
    }

    LaunchedEffect(existingService) {
        existingService?.let {
            title = it.title
            category = it.category
            description = it.description
            priceFrom = it.priceFrom.toInt().toString()
            priceTo = it.priceTo.toInt().toString()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Servicio") },
            text = { Text("¿Estás seguro de que deseas eliminar este servicio? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    existingService?.let { viewModel.deleteService(it.id) }
                    onBackClick()
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(text = if (serviceId == null) "Nuevo Servicio" else "Editar Servicio", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (serviceId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                    TextButton(
                        onClick = {
                            val catalog = ServiceCatalog(
                                id = existingService?.id ?: 0,
                                profileId = 0, // Set by ViewModel
                                title = title,
                                description = description,
                                category = category,
                                priceFrom = priceFrom.toDoubleOrNull() ?: 0.0,
                                priceTo = priceTo.toDoubleOrNull() ?: 0.0
                            )
                            if (serviceId == null) {
                                viewModel.createService(catalog) { onBackClick() }
                            } else {
                                viewModel.updateService(existingService!!.id, catalog) { onBackClick() }
                            }
                        },
                        enabled = title.isNotBlank() && category.isNotBlank()
                    ) {
                        Text(text = "Guardar", color = Color(0xFF2E2E8F), fontWeight = FontWeight.Bold)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (viewModel.error != null) {
                    Text(text = viewModel.error!!, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Título del Servicio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("Ej. Catering para Bodas") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Categoría", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("Ej. Alimentación") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Descripción", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("Describe brevemente lo que incluye tu servicio...") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Rango de Precios Estimado (S/)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Precio Mínimo", fontSize = 12.sp, color = Color.Gray)
                        OutlinedTextField(
                            value = priceFrom,
                            onValueChange = { priceFrom = it },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Precio Máximo", fontSize = 12.sp, color = Color.Gray)
                        OutlinedTextField(
                            value = priceTo,
                            onValueChange = { priceTo = it },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEditServiceScreenPreview() {
    EventifyfrontendkotlinTheme {
        CreateEditServiceScreen(onBackClick = {})
    }
}
