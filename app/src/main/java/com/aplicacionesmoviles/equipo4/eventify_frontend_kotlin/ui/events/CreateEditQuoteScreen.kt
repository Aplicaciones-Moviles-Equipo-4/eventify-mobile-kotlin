package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditQuoteScreen(
    quoteId: String? = null,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(if (quoteId == null) "Crear Cotización" else "Editar Cotización", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section: Detalles Principales
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Detalles Principales", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2E2E8F))
                    
                    Text(text = "Título de la Cotización", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. Boda Civil Familia Pérez") },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Tipo de Evento", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = eventType,
                                onValueChange = { eventType = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Cantidad de invitados", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = guests,
                                onValueChange = { guests = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                leadingIcon = { Icon(Icons.Default.People, null, modifier = Modifier.size(18.dp)) }
                            )
                        }
                    }
                }

                // Section: Cuándo y Dónde
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Cuándo y Dónde", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2E2E8F))
                    
                    Text(text = "Ubicación / Lugar", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar dirección o recinto...") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { /* Save */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Guardar cotización", fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEditQuoteScreenPreview() {
    EventifyfrontendkotlinTheme {
        CreateEditQuoteScreen(onBackClick = {})
    }
}
