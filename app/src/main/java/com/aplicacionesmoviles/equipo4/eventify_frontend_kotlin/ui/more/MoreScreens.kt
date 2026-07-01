package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.AppNotification
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.NotificationType
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.EmptyState

private val Indigo = Color(0xFF2E2E8F)

// ============================ NOTIFICATIONS ============================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBackClick: () -> Unit) {
    val notifications = LocalStore.notifications.toList()
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Notificaciones", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") }
                },
                actions = {
                    TextButton(onClick = { LocalStore.markAllNotificationsRead() }) {
                        Text("Marcar todas", color = Indigo, fontSize = 12.sp)
                    }
                }
            )
            if (notifications.isEmpty()) {
                EmptyState(icon = Icons.Default.NotificationsNone, title = "Sin notificaciones", message = "Aquí aparecerán avisos de cotizaciones, mensajes y pagos.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notifications) { n ->
                        NotificationRow(n) { LocalStore.markNotificationRead(n.id) }
                        HorizontalDivider(color = Color(0xFFF5F5F5))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(n: AppNotification, onClick: () -> Unit) {
    val (icon, tint) = iconFor(n.type)
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick() }
            .background(if (n.read) Color.White else Color(0xFFF6F7FF))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).background(tint.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(n.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(n.body, fontSize = 12.sp, color = Color.Gray)
        }
        if (!n.read) {
            Box(modifier = Modifier.size(8.dp).background(Indigo, CircleShape))
        }
    }
}

private fun iconFor(type: NotificationType): Pair<ImageVector, Color> = when (type) {
    NotificationType.MESSAGE -> Icons.Default.ChatBubbleOutline to Color(0xFF2E2E8F)
    NotificationType.QUOTE -> Icons.Default.Description to Color(0xFF00897B)
    NotificationType.TASK -> Icons.Default.CheckCircleOutline to Color(0xFF5E35B1)
    NotificationType.PAYMENT -> Icons.Default.AccountBalanceWallet to Color(0xFFF57F17)
    NotificationType.SYSTEM -> Icons.Default.Info to Color(0xFF1E88E5)
}

// ============================ SUBSCRIPTION ============================

private data class Plan(val name: String, val price: String, val features: List<String>, val highlighted: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(onBackClick: () -> Unit) {
    val current = LocalStore.subscription
    val plans = listOf(
        Plan("Free", "S/ 0 / mes", listOf("Perfil profesional", "Hasta 3 servicios", "Cotizaciones básicas"), false),
        Plan("Pro", "S/ 49 / mes", listOf("Servicios ilimitados", "Tablero Kanban y presupuesto", "Chat con anfitriones", "Estadísticas del perfil"), true),
        Plan("Business", "S/ 99 / mes", listOf("Todo lo de Pro", "Notificaciones prioritarias", "Soporte dedicado", "Reportes avanzados"), false)
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Suscripción", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") }
                }
            )
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Indigo)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Plan actual: ${current.plan}", fontWeight = FontWeight.Bold)
                                Text(
                                    if (current.active) "Renueva el ${current.renewalDate}" else "Sin suscripción activa",
                                    fontSize = 12.sp, color = Color.Gray
                                )
                            }
                        }
                    }
                }
                items(plans) { plan ->
                    PlanCard(plan = plan, isCurrent = plan.name == current.plan) {
                        LocalStore.setSubscription(plan.name, nextMonthLabel())
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanCard(plan: Plan, isCurrent: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (plan.highlighted) Indigo else Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (plan.highlighted) Indigo else Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (plan.highlighted) 6.dp else 1.dp)
    ) {
        val onColor = if (plan.highlighted) Color.White else Color.Black
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(plan.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = onColor)
                if (plan.highlighted) {
                    Surface(color = Color(0xFFFFB300), shape = RoundedCornerShape(8.dp)) {
                        Text("Recomendado", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
            Text(plan.price, color = if (plan.highlighted) Color.White.copy(alpha = 0.85f) else Indigo, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            plan.features.forEach { f ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = if (plan.highlighted) Color(0xFFFFB300) else Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(f, fontSize = 13.sp, color = if (plan.highlighted) Color.White else Color.DarkGray)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onSelect,
                enabled = !isCurrent,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (plan.highlighted) Color.White else Indigo,
                    contentColor = if (plan.highlighted) Indigo else Color.White,
                    disabledContainerColor = Color(0xFFBDBDBD)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isCurrent) "Plan actual" else "Elegir ${plan.name}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun nextMonthLabel(): String {
    val date = java.time.LocalDate.now().plusMonths(1)
    return "%02d/%02d/%d".format(date.dayOfMonth, date.monthValue, date.year)
}
