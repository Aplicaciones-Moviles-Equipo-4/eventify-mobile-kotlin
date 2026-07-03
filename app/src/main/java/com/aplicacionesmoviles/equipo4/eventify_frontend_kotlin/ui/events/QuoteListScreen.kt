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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Quote
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.AppHeader
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.EmptyState
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@Composable
fun QuoteListScreen(
    onQuoteClick: (String) -> Unit,
    onCreateQuoteClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    QuoteListContent(
        onQuoteClick = onQuoteClick,
        onCreateQuoteClick = onCreateQuoteClick,
        isLoading = viewModel.isLoading,
        quotes = viewModel.quotes,
        profileImageUrl = viewModel.profile?.profileImageUrl,
        initials = viewModel.profile?.let {
            "${it.firstName.firstOrNull() ?: ""}${it.lastName.firstOrNull() ?: ""}"
        } ?: "E"
    )
}

@Composable
fun QuoteListContent(
    onQuoteClick: (String) -> Unit,
    onCreateQuoteClick: () -> Unit,
    isLoading: Boolean,
    quotes: List<Quote>,
    profileImageUrl: String? = null,
    initials: String = "E"
) {
    var selectedFilter by remember { mutableStateOf("Pendientes") }
    val filters = listOf("Todas", "Pendientes", "Aceptadas", "Rechazadas")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onCreateQuoteClick,
                    containerColor = Color(0xFF2E2E8F),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva cotización")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                AppHeader(
                    profileImageUrl = profileImageUrl,
                    initials = initials
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Lista de cotizaciones", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                
                Spacer(modifier = Modifier.height(16.dp))
                QuoteSearchBar()
                
                Spacer(modifier = Modifier.height(16.dp))
                ScrollableTabRow(
                    selectedTabIndex = filters.indexOf(selectedFilter),
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF2E2E8F),
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    filters.forEach { filter ->
                        Tab(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            text = { Text(text = filter, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2E2E8F))
                    }
                } else {
                    val filteredQuotes = when (selectedFilter) {
                        "Pendientes" -> quotes.filter { it.state == "PENDING" }
                        "Aceptadas" -> quotes.filter { it.state == "ACCEPTED" }
                        "Rechazadas" -> quotes.filter { it.state == "REJECTED" }
                        else -> quotes
                    }

                    if (filteredQuotes.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Description,
                            title = "Sin cotizaciones",
                            message = "Aquí verás las solicitudes de cotización que te envíen los anfitriones."
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(filteredQuotes) { quote ->
                                QuoteCard(quote, onQuoteClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuoteSearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Boda...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
fun QuoteCard(quote: Quote, onQuoteClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onQuoteClick(quote.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = quote.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                StatusTag(quote.state)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Text(text = " Invitados: ${quote.guestQuantity}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Text(text = " ${quote.eventDate.substringBefore("T")}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "TOTAL ESTIMADO", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(text = "S/ ${quote.totalPrice}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E8F))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuoteListScreenPreview() {
    EventifyfrontendkotlinTheme {
        QuoteListContent(
            onQuoteClick = {},
            onCreateQuoteClick = {},
            isLoading = false,
            quotes = emptyList()
        )
    }
}
