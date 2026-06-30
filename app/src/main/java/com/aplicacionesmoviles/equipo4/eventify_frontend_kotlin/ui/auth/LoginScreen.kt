package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccountClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess) {
            onLoginSuccess()
        }
    }

    var username by remember { mutableStateOf("organizador_vip") }
    var password by remember { mutableStateOf("prueba123") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo and Title
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF2E2E8F)
            )
            Text(
                text = "Eventify",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E8F)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tu espacio central para organizar eventos de éxito",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email/Username field
            Text(
                text = "Usuario",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("admin_eventos") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                enabled = !viewModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            Text(
                text = "Contraseña",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !viewModel.isLoading
            )

            if (viewModel.error != null) {
                Text(
                    text = viewModel.error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Remember me and Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                    Text(text = "Recordarme")
                }
                TextButton(onClick = { /* Handle forgot password */ }) {
                    Text(text = "¿Olvidaste tu contraseña?", color = Color(0xFF2E2E8F))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = { 
                    viewModel.username = username
                    viewModel.password = password
                    viewModel.signIn() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F)),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Iniciar sesión", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Create Account Button
            OutlinedButton(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E8F)),
                enabled = !viewModel.isLoading
            ) {
                Text(text = "Crear cuenta", color = Color(0xFF2E2E8F), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                text = "Al iniciar sesión, aceptas nuestros Términos de servicio y Política de privacidad.",
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    EventifyfrontendkotlinTheme {
        LoginScreen(onLoginSuccess = {}, onCreateAccountClick = {})
    }
}
