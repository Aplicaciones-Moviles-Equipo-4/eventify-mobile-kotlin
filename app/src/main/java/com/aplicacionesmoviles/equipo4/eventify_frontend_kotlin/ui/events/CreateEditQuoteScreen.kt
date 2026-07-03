package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.DatePickerField
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

private val Indigo = Color(0xFF2E2E8F)

private val eventTypes = listOf(
    "WEDDING" to "Boda",
    "BIRTHDAY" to "Cumpleaños",
    "CONFERENCE" to "Conferencia",
    "GRADUATION" to "Graduación"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditQuoteScreen(
    quoteId: String? = null,
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf("WEDDING") }
    var typeMenuOpen by remember { mutableStateOf(false) }
    var guests by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }   // yyyy-MM-dd
    var totalPrice by remember { mutableStateOf("") }
    var hostId by remember { mutableStateOf("1") }

    val dateValid = Regex("""\d{4}-\d{2}-\d{2}""").matches(eventDate)
    val canSave = title.isNotBlank() && guests.toIntOrNull()?.let { it > 0 } == true &&
        location.isNotBlank() && (totalPrice.toDoubleOrNull() ?: 0.0) > 0 && dateValid &&
        (hostId.toIntOrNull() ?: 0) > 0

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (quoteId == null) "Nueva cotización" else "Editar cotización",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (viewModel.error != null) {
                    Text(viewModel.error!!, color = Color.Red, fontSize = 13.sp)
                }

                Text("Detalles principales", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Indigo)

                LabeledField("Título de la cotización", title, { title = it }, "Ej. Boda Civil Familia Pérez")

                // Event type dropdown
                Column {
                    Text("Tipo de evento", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    ExposedDropdownMenuBox(expanded = typeMenuOpen, onExpandedChange = { typeMenuOpen = !typeMenuOpen }) {
                        OutlinedTextField(
                            value = eventTypes.firstOrNull { it.first == eventType }?.second ?: eventType,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuOpen) },
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = typeMenuOpen, onDismissRequest = { typeMenuOpen = false }) {
                            eventTypes.forEach { (value, label) ->
                                DropdownMenuItem(text = { Text(label) }, onClick = { eventType = value; typeMenuOpen = false })
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(Modifier.weight(1f)) { LabeledNumberField("Invitados", guests, { guests = it }) }
                    Box(Modifier.weight(1f)) { LabeledNumberField("Total estimado (S/)", totalPrice, { totalPrice = it }) }
                }

                Text("Cuándo y dónde", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Indigo)
                LabeledField("Ubicación / lugar", location, { location = it }, "Dirección o recinto")
                DatePickerField("Fecha del evento", eventDate, { eventDate = it })
                LabeledNumberField("ID del cliente (anfitrión)", hostId, { hostId = it })

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val iso = "${eventDate}T18:00:00.000Z"
                        viewModel.createQuote(
                            title = title,
                            eventType = eventType,
                            guestQuantity = guests.toIntOrNull() ?: 0,
                            location = location,
                            totalPrice = totalPrice.toDoubleOrNull() ?: 0.0,
                            eventDate = iso,
                            hostId = hostId.toIntOrNull() ?: 1
                        ) { onBackClick() }
                    },
                    enabled = canSave,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar cotización", fontSize = 16.sp)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LabeledField(label: String, value: String, onChange: (String) -> Unit, placeholder: String) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun LabeledNumberField(label: String, value: String, onChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = { onChange(it.filter { c -> c.isDigit() || c == '.' }) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEditQuoteScreenPreview() {
    EventifyfrontendkotlinTheme {
        CreateEditQuoteScreen(onBackClick = {})
    }
}
