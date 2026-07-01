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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Resumen", "Tareas", "Cronograma", "Presupuesto")

    // Real event header pulled from the shared state loaded on the dashboard/events list.
    val event = remember(eventId, viewModel.socialEvents) {
        viewModel.socialEvents.find { it.id.toString() == eventId }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { /* More */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )

            // Header Info
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusTag(event?.status ?: "Active")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(text = " ${event?.date?.substringBefore("T") ?: ""}", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = event?.title ?: "Evento", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(text = " ${event?.place ?: ""}", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF2E2E8F),
                edgePadding = 16.dp,
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

            // Tab Content
            when (selectedTabIndex) {
                0 -> ResumenTab()
                1 -> TasksTab()
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "${tabs[selectedTabIndex]} Content")
                }
            }
        }
    }
}

@Composable
fun ResumenTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Payment Alert
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEEEEE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "1 pago pendiente para hoy", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text(text = "Adelanto para Catering \"Delicias Peruanas\" - S/ 5,000", fontSize = 12.sp, color = Color.Black)
                    }
                    Button(
                        onClick = { /* Resolve */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Resolver", color = Color.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            InfoCard(
                title = "Progreso",
                value = "75%",
                subtitle = "completado",
                icon = Icons.Default.TrendingUp,
                progress = 0.75f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            InfoCard(
                title = "Presupuesto",
                value = "S/ 18,500",
                subtitle = "de S/ 25,000",
                icon = Icons.Default.AccountBalanceWallet,
                progress = 0.74f,
                progressColor = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E8F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Tiempo", color = Color.White, fontSize = 12.sp)
                        Text(text = "12 días faltan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
        }

        item {
            // Moodboard
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) // Placeholder for image
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                    Text(
                        text = "Moodboard Aprobado",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    )
                }
            }
        }

        item {
            Text(text = "Próximas actividades", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        }

        items(listOf(
            ActivityItem("Visita a local \"Fundo Cieneguilla\"", "Mañana, 10:00 AM • Confirmado", Icons.Default.Store),
            ActivityItem("Degustación de menú", "Vie, 14 jun • Catering Delicias ...", Icons.Default.Restaurant),
            ActivityItem("Cierre de lista de invitados", "Lun, 17 jun • 150 confirmados ...", Icons.Default.CheckCircle)
        )) { activity ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(activity.icon, contentDescription = null, modifier = Modifier.padding(8.dp), tint = Color(0xFF2E2E8F))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = activity.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    Text(text = activity.subtitle, fontSize = 12.sp, color = Color.Gray)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
            HorizontalDivider(color = Color(0xFFF5F5F5))
        }

        item {
            TextButton(
                onClick = { /* See all */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ver todas las tareas", color = Color(0xFF2E2E8F))
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    progress: Float,
    progressColor: Color = Color(0xFF2E2E8F),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF2E2E8F), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E8F))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = Color(0xFFE0E0E0)
            )
        }
    }
}

data class ActivityItem(val title: String, val subtitle: String, val icon: ImageVector)

@Preview(showBackground = true)
@Composable
fun EventDetailScreenPreview() {
    EventifyfrontendkotlinTheme {
        EventDetailScreen(eventId = "1", onBackClick = {})
    }
}
