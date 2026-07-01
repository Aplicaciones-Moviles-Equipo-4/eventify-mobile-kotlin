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

    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar evento") },
            text = { Text("¿Seguro que deseas eliminar este evento? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    eventId.toIntOrNull()?.let { id -> viewModel.deleteEvent(id) { onBackClick() } }
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
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
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
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
                0 -> ResumenTab(eventId = eventId)
                1 -> TasksKanbanTab(eventId = eventId)
                2 -> CronogramaTab(eventId = eventId)
                else -> BudgetTab(eventId = eventId)
            }
        }
    }
}

@Composable
fun ResumenTab(eventId: String) {
    val tasks = com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore.tasksForEvent(eventId)
    val done = tasks.count { it.status == com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.TaskStatus.COMPLETADA }
    val progress = if (tasks.isEmpty()) 0f else done.toFloat() / tasks.size
    val budget = com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore.budgetForEvent(eventId)
    val spent = com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore.spentForEvent(eventId)
    val upcoming = com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore.agendaForEvent(eventId).filter { !it.done }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InfoCard(
                title = "Progreso de tareas",
                value = "${(progress * 100).toInt()}%",
                subtitle = "$done de ${tasks.size} completadas",
                icon = Icons.Default.TrendingUp,
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            val ratio = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1f) else 0f
            InfoCard(
                title = "Presupuesto",
                value = "S/ ${spent.toInt()}",
                subtitle = if (budget > 0) "de S/ ${budget.toInt()}" else "sin presupuesto definido",
                icon = Icons.Default.AccountBalanceWallet,
                progress = ratio,
                progressColor = if (ratio > 0.9f) Color.Red else Color(0xFF2E2E8F),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(text = "Próximas actividades", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        }

        if (upcoming.isEmpty()) {
            item {
                Text(
                    text = "Aún no hay actividades en el cronograma. Agrégalas en la pestaña \"Cronograma\".",
                    color = Color.Gray, fontSize = 13.sp
                )
            }
        }

        items(upcoming) { activity ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.padding(8.dp), tint = Color(0xFF2E2E8F))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = activity.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    Text(text = activity.time, fontSize = 12.sp, color = Color.Gray)
                }
            }
            HorizontalDivider(color = Color(0xFFF5F5F5))
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
