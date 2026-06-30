package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Profile
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    onBackClick: () -> Unit,
    onProfileCreated: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Crear perfil de organizador", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (viewModel.error != null) {
                    Text(text = viewModel.error!!, color = Color.Red, fontSize = 12.sp)
                }

                Text(
                    text = "Completa tus datos para empezar a gestionar eventos profesionales de forma eficiente y segura.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Nombres", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tus nombres") },
                    shape = RoundedCornerShape(8.dp),
                    enabled = !viewModel.isLoading
                )

                Text("Apellidos", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tus apellidos") },
                    shape = RoundedCornerShape(8.dp),
                    enabled = !viewModel.isLoading
                )

                Text("Correo electrónico", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("tu@correo.com") },
                    shape = RoundedCornerShape(8.dp),
                    enabled = !viewModel.isLoading
                )

                Text("Dirección principal", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Calle, Avenida, etc.") },
                    shape = RoundedCornerShape(8.dp),
                    enabled = !viewModel.isLoading
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ciudad", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            placeholder = { Text("Tu ciudad") },
                            shape = RoundedCornerShape(8.dp),
                            enabled = !viewModel.isLoading
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("País", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = "Perú",
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val newProfile = Profile(
                            id = 0,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            street = address,
                            number = "S/N",
                            city = city,
                            postalCode = "15000",
                            country = "Peru",
                            type = "ORGANIZER"
                        )
                        viewModel.createProfile(newProfile, onProfileCreated)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading && firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Crear perfil de organizador", fontSize = 16.sp)
                    }
                }

                Text(
                    text = "Al crear una cuenta, aceptas nuestros Términos de servicio y Política de privacidad.",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateProfileScreenPreview() {
    EventifyfrontendkotlinTheme {
        CreateProfileScreen(onBackClick = {}, onProfileCreated = {})
    }
}
