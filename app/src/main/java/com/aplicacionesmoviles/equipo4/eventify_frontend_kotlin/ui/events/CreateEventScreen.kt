package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.AppHeader
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(onBackClick: () -> Unit) {
    var currentStep by remember { mutableIntStateOf(1) }
    var eventName by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    // Step 2 fields
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            AppHeader()
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (currentStep > 1) currentStep-- else onBackClick()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(text = if (currentStep == 1) "Nuevo evento" else "Detalles del Evento", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "PASO $currentStep DE 5", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = when(currentStep) {
                        1 -> "INFORMACIÓN GENERAL"
                        2 -> "CUÁNDO Y DÓNDE"
                        else -> "DETALLES ADICIONALES"
                    }, 
                    fontSize = 12.sp, 
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { currentStep / 5.0f },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = Color(0xFF2E2E8F),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(32.dp))

            when(currentStep) {
                1 -> {
                    // Step 1: Info General
                    Text(text = "Nombre del evento *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. Boda Civil Camila y Diego") },
                        shape = RoundedCornerShape(8.dp),
                        isError = eventName.length < 10 && eventName.isNotEmpty()
                    )
                    if (eventName.length < 10 && eventName.isNotEmpty()) {
                        Text(text = "El nombre debe tener al menos 10 caracteres.", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Tipo de evento *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = eventType,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            placeholder = { Text("Selecciona una opción") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("Boda", "Cumpleaños", "Graduación", "Corporativo").forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = { eventType = type; expanded = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Descripción", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Breve descripción...") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                2 -> {
                    // Step 2: Cuándo y Dónde
                    Text(text = "Fecha *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("dd/mm/aaaa") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Lugar *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Dirección o nombre del salón") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                else -> {
                    Text(text = "Próximos pasos en desarrollo...", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { if (currentStep < 5) currentStep++ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (currentStep < 5) "Siguiente" else "Finalizar", fontSize = 16.sp)
                    if (currentStep < 5) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onBackClick() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E8F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Guardar borrador", color = Color(0xFF2E2E8F), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEventScreenPreview() {
    EventifyfrontendkotlinTheme {
        CreateEventScreen(onBackClick = {})
    }
}
