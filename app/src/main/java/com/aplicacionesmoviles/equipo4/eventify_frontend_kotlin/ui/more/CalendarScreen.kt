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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val Indigo = Color(0xFF2E2E8F)

private data class CalItem(val title: String, val subtitle: String, val isQuote: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadAllData() }

    // Build a date -> items index from real social events and quotes.
    val byDate = remember(viewModel.socialEvents, viewModel.quotes) {
        val map = mutableMapOf<LocalDate, MutableList<CalItem>>()
        viewModel.socialEvents.forEach { e ->
            parseDate(e.date)?.let { d ->
                map.getOrPut(d) { mutableListOf() }.add(CalItem(e.title, e.place, false))
            }
        }
        viewModel.quotes.forEach { q ->
            parseDate(q.eventDate)?.let { d ->
                map.getOrPut(d) { mutableListOf() }.add(CalItem(q.title, "Cotización · ${q.guestQuantity} inv.", true))
            }
        }
        map
    }

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Calendario", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") }
                }
            )

            // Month header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior", tint = Indigo)
                }
                Text(
                    "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente", tint = Indigo)
                }
            }

            // Weekday headers (Mon-first)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do").forEach {
                    Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            // Day grid
            val firstOfMonth = currentMonth.atDay(1)
            val leadingBlanks = (firstOfMonth.dayOfWeek.value - 1) // Monday=1 -> 0 blanks
            val daysInMonth = currentMonth.lengthOfMonth()
            val cells = leadingBlanks + daysInMonth
            val rows = (cells + 6) / 7

            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dayNum = cellIndex - leadingBlanks + 1
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                                if (dayNum in 1..daysInMonth) {
                                    val date = currentMonth.atDay(dayNum)
                                    val hasEvents = byDate.containsKey(date)
                                    val isSelected = date == selectedDay
                                    val isToday = date == LocalDate.now()
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.size(40.dp)
                                            .background(
                                                if (isSelected) Indigo else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable { selectedDay = date },
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            "$dayNum",
                                            color = when {
                                                isSelected -> Color.White
                                                isToday -> Indigo
                                                else -> Color.Black
                                            },
                                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        )
                                        if (hasEvents) {
                                            Box(modifier = Modifier.size(5.dp).background(if (isSelected) Color.White else Color(0xFFFFB300), CircleShape))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))

            // Selected day's events
            val dayItems = selectedDay?.let { byDate[it] } ?: emptyList()
            Text(
                selectedDay?.let { "Eventos del ${it.dayOfMonth}/${it.monthValue}" } ?: "Selecciona un día",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            if (dayItems.isEmpty()) {
                Text("No hay eventos este día.", modifier = Modifier.padding(16.dp), color = Color.Gray, fontSize = 14.sp)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dayItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(if (item.isQuote) Color(0xFF00897B) else Indigo, CircleShape))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(item.subtitle, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Parses "yyyy-MM-dd" or ISO "yyyy-MM-ddTHH:mm:ss..." to a LocalDate. */
private fun parseDate(raw: String?): LocalDate? {
    if (raw.isNullOrBlank()) return null
    val datePart = raw.take(10)
    return runCatching { LocalDate.parse(datePart) }.getOrNull()
}
