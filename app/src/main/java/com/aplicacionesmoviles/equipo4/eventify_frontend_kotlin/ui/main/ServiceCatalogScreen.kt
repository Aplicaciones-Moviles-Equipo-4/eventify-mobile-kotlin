package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.main

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.ServiceCatalog
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.AppHeader
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.EmptyState
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@Composable
fun ServiceCatalogScreen(
    onCreateServiceClick: () -> Unit,
    onEditServiceClick: (Int) -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    ServiceCatalogContent(
        isLoading = viewModel.isLoading,
        serviceCatalogs = viewModel.serviceCatalogs,
        onCreateServiceClick = onCreateServiceClick,
        onEditServiceClick = onEditServiceClick
    )
}

@Composable
fun ServiceCatalogContent(
    isLoading: Boolean,
    serviceCatalogs: List<ServiceCatalog>,
    onCreateServiceClick: () -> Unit,
    onEditServiceClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2E2E8F))
            }
        } else {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onCreateServiceClick,
                        containerColor = Color(0xFF2E2E8F),
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    var searchQuery by remember { mutableStateOf("") }
                    var selectedFilter by remember { mutableStateOf("Todos") }

                    AppHeader()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Catálogo de servicios", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "Gestiona y actualiza los servicios que ofreces.", color = Color.Gray, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    CatalogSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

                    Spacer(modifier = Modifier.height(16.dp))
                    val categories = remember(serviceCatalogs) {
                        listOf("Todos") + serviceCatalogs.map { it.category }.distinct()
                    }
                    CatalogFilterChips(
                        filters = categories,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    val filtered = serviceCatalogs.filter { service ->
                        (selectedFilter == "Todos" || service.category == selectedFilter) &&
                            (searchQuery.isBlank() ||
                                service.title.contains(searchQuery, ignoreCase = true) ||
                                service.category.contains(searchQuery, ignoreCase = true))
                    }
                    if (filtered.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Assignment,
                            title = "Sin servicios",
                            message = "Pulsa el botón + para publicar tu primer servicio en el catálogo."
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(filtered) { service ->
                                ServiceItemCard(service, onEditServiceClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar servicios...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun CatalogFilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2E2E8F),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ServiceItemCard(service: ServiceCatalog, onEditClick: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = service.category.uppercase(), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = service.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = { onEditClick(service.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "RANGO DE PRECIO ESTIMADO", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(
                    text = "S/ ${service.priceFrom.toInt()} - S/ ${service.priceTo.toInt()}",
                    color = Color(0xFF2E2E8F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceCatalogScreenPreview() {
    EventifyfrontendkotlinTheme {
        ServiceCatalogContent(
            isLoading = false,
            serviceCatalogs = listOf(
                ServiceCatalog(1, 1, "Catering Services", "Delicious food for your event", "Catering", 100.0, 500.0),
                ServiceCatalog(2, 1, "Party Decoration", "Beautiful decor for all occasions", "Decoration", 200.0, 800.0)
            ),
            onCreateServiceClick = {},
            onEditServiceClick = {}
        )
    }
}
