package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Quote
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.ServiceItem
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteDetailScreen(
    quoteId: String,
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ServiceItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar cotización") },
            text = { Text("¿Seguro que deseas eliminar esta cotización? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteQuote(quoteId) { onBackClick() }
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    val quote = remember(quoteId, viewModel.quotes) {
        viewModel.quotes.find { it.id == quoteId }
    }

    LaunchedEffect(quoteId) {
        viewModel.loadQuoteItems(quoteId)
    }

    if (showAddDialog || editingItem != null) {
        AddEditServiceItemDialog(
            item = editingItem,
            onDismiss = { 
                showAddDialog = false
                editingItem = null 
            },
            onSave = { description, qty, price ->
                val newItem = ServiceItem(
                    id = editingItem?.id,
                    description = description,
                    quantity = qty,
                    unitPrice = price,
                    totalPrice = qty * price,
                    quoteId = quoteId
                )
                if (editingItem == null) {
                    viewModel.addServiceItem(quoteId, newItem) { 
                        showAddDialog = false
                        // Refresh main data for potential price updates
                        viewModel.loadAllData()
                    }
                } else {
                    viewModel.updateServiceItem(quoteId, editingItem!!.id!!, newItem) {
                        editingItem = null
                        viewModel.loadAllData()
                    }
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
                title = { Text("Detalle de cotización", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (quote?.state == "PENDING") {
                        IconButton(onClick = { viewModel.confirmQuote(quoteId) }) {
                            Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color(0xFF2E7D32))
                        }
                        IconButton(onClick = { viewModel.rejectQuote(quoteId) }) {
                            Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color.Red)
                        }
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                }
            )

            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                item {
                    quote?.let { QuoteInfoSection(it) }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Artículos del servicio", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color(0xFF2E2E8F))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (viewModel.isLoadingItems) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF2E2E8F))
                        }
                    }
                } else {
                    items(viewModel.currentQuoteItems) { item ->
                        ServiceItemRow(
                            item = item,
                            onEdit = { editingItem = it },
                            onDelete = { viewModel.removeServiceItem(quoteId, it.id!!) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Volver", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteInfoSection(quote: Quote) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = "ESTADO", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events.StatusTag(quote.state)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "TOTAL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(text = "S/ ${quote.totalPrice}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E8F))
            }
        }
        
        HorizontalDivider(color = Color(0xFFF5F5F5))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Cliente ID: ${quote.hostId}", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = quote.title, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ServiceItemRow(
    item: ServiceItem,
    onEdit: (ServiceItem) -> Unit,
    onDelete: (ServiceItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.description, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                Text(text = "${item.quantity} x S/ ${item.unitPrice}", fontSize = 12.sp, color = Color.Gray)
            }
            Text(text = "S/ ${item.totalPrice}", fontWeight = FontWeight.Bold, color = Color(0xFF2E2E8F))
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
            IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = Color.Red)
            }
        }
    }
}

@Composable
fun AddEditServiceItemDialog(
    item: ServiceItem?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Double) -> Unit
) {
    var description by remember { mutableStateOf(item?.description ?: "") }
    var quantity by remember { mutableStateOf(item?.quantity?.toString() ?: "1") }
    var price by remember { mutableStateOf(item?.unitPrice?.toString() ?: "0.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Añadir Ítem" else "Editar Ítem") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Cantidad") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio Unitario") })
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                onSave(description, quantity.toIntOrNull() ?: 1, price.toDoubleOrNull() ?: 0.0) 
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun QuoteDetailScreenPreview() {
    EventifyfrontendkotlinTheme {
        QuoteDetailScreen(quoteId = "1", onBackClick = {})
    }
}
