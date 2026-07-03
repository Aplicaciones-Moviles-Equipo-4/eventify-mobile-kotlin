package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

import androidx.compose.foundation.clickable
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
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.SocialEvent
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.AppHeader
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.DatePickerField
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.EmptyState
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@Composable
fun MyEventsScreen(
    onEventClick: (String) -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    MyEventsContent(
        onEventClick = onEventClick,
        onCreateEvent = { title, place, date, customer ->
            viewModel.createEvent(title, place, date, customer) {}
        },
        isLoading = viewModel.isLoading,
        events = viewModel.socialEvents
    )
}

@Composable
fun MyEventsContent(
    onEventClick: (String) -> Unit,
    onCreateEvent: (String, String, String, String) -> Unit,
    isLoading: Boolean,
    events: List<SocialEvent>
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreateEventDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, place, date, customer ->
                onCreateEvent(title, place, date, customer)
                showCreateDialog = false
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = Color(0xFF2E2E8F),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo evento")
                }
            }
        ) { innerPadding ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2E2E8F))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    AppHeader()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Eventos", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (events.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.CalendarToday,
                            title = "Aún no hay eventos",
                            message = "Pulsa + para registrar tu primer evento social."
                        )
                    } else {
                        EventList(events = events, onEventClick = onEventClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateEventDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var customer by remember { mutableStateOf("") }
    val dateValid = Regex("""\d{4}-\d{2}-\d{2}""").matches(date)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo evento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, singleLine = true)
                OutlinedTextField(value = customer, onValueChange = { customer = it }, label = { Text("Cliente / anfitrión") }, singleLine = true)
                OutlinedTextField(value = place, onValueChange = { place = it }, label = { Text("Lugar") }, singleLine = true)
                DatePickerField(label = "Fecha", selectedDate = date, onDateSelected = { date = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(title, place, date, customer) },
                enabled = title.isNotBlank() && place.isNotBlank() && customer.isNotBlank() && dateValid
            ) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun EventList(events: List<SocialEvent>, onEventClick: (String) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(events) { event ->
            EventCard(event, onEventClick)
        }
    }
}

@Composable
fun EventCard(event: SocialEvent, onEventClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEventClick(event.id.toString()) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=800&q=80",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = event.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = event.customerName, color = Color.Gray, fontSize = 12.sp)
                    }
                    StatusTag(event.status)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(text = " ${event.date}", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(text = " ${event.place}", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatusTag(status: String) {
    val backgroundColor = when (status) {
        "Active", "ACCEPTED" -> Color(0xFFE8F5E9)
        "Completed" -> Color(0xFFE3F2FD)
        "PENDING" -> Color(0xFFFFF8E1)
        "Cancelled", "REJECTED" -> Color(0xFFFBE9E7)
        else -> Color(0xFFF5F5F5)
    }
    val textColor = when (status) {
        "Active", "ACCEPTED" -> Color(0xFF2E7D32)
        "Completed" -> Color(0xFF0277BD)
        "PENDING" -> Color(0xFFF57F17)
        "Cancelled", "REJECTED" -> Color(0xFFD84315)
        else -> Color.Black
    }
    val label = when (status) {
        "Active" -> "Activo"
        "Completed" -> "Completado"
        "Cancelled" -> "Cancelado"
        "PENDING" -> "Pendiente"
        "ACCEPTED" -> "Aceptada"
        "REJECTED" -> "Rechazada"
        else -> status
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyEventsScreenPreview() {
    EventifyfrontendkotlinTheme {
        MyEventsContent(
            onEventClick = {},
            onCreateEvent = { _, _, _, _ -> },
            isLoading = false,
            events = emptyList()
        )
    }
}
