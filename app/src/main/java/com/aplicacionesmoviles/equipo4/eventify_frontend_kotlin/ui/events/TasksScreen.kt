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
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme

@Composable
fun TasksTab() {
    var selectedStatusIndex by remember { mutableStateOf(1) } // "En progreso"
    val statuses = listOf("Pendiente (5)", "En progreso (3)", "Completada (12)")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScrollableTabRow(
            selectedTabIndex = selectedStatusIndex,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF2E2E8F),
            edgePadding = 0.dp,
            divider = {},
            indicator = {}
        ) {
            statuses.forEachIndexed { index, title ->
                Tab(
                    selected = selectedStatusIndex == index,
                    onClick = { selectedStatusIndex = index },
                    modifier = Modifier.padding(end = 8.dp).clip(RoundedCornerShape(8.dp)).background(if (selectedStatusIndex == index) Color(0xFFE8EAF6) else Color(0xFFF5F5F5)),
                    text = { Text(text = title, fontSize = 12.sp, color = if (selectedStatusIndex == index) Color(0xFF2E2E8F) else Color.Gray) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(listOf(
                TaskItem("Confirmar menú con catering", "Alta", "18 jun", "LM"),
                TaskItem("Validar distribución de mesas", "Media", "20 jun", null)
            )) { task ->
                TaskCard(task)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { /* New Task */ },
            modifier = Modifier.align(Alignment.End).padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F)),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Nueva tarea")
        }
    }
}

data class TaskItem(val title: String, val priority: String, val date: String, val assignee: String?)

@Composable
fun TaskCard(task: TaskItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (task.priority == "Alta") Color(0xFFFFF3F3) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (task.priority == "Alta") Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                        Text(text = task.priority, fontSize = 10.sp, color = if (task.priority == "Alta") Color.Red else Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Text(text = " ${task.date}", fontSize = 10.sp, color = Color.Gray)
                
                if (task.assignee != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = task.assignee, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TasksTabPreview() {
    EventifyfrontendkotlinTheme {
        Surface(color = Color.White) {
            TasksTab()
        }
    }
}
