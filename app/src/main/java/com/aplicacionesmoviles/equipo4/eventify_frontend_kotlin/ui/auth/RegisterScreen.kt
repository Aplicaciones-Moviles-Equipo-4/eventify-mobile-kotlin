package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.EventifyfrontendkotlinTheme
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // The account is identified by email: the same value is reused as the Profile email so that
    // login can resolve the profileId later via getProfileByEmail.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    var activeDialogInfo by remember { mutableStateOf<DialogInfo?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    LaunchedEffect(viewModel.registerSuccess) {
        if (viewModel.registerSuccess) {
            onRegisterClick()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Organizador") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea tu cuenta profesional para empezar a gestionar tus eventos.",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email field (doubles as the account username and the profile email)
            Text(
                text = "Correo electrónico",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; localError = null },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("tu@correo.com") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = email.isNotBlank() && !isEmailValid,
                supportingText = {
                    if (email.isNotBlank() && !isEmailValid) {
                        Text("Ingresa un correo válido")
                    }
                },
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
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !viewModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            Text(
                text = "Confirmar contraseña",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !viewModel.isLoading
            )

            val errorMessage = localError ?: viewModel.error
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and conditions checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    enabled = !viewModel.isLoading
                )
                Spacer(modifier = Modifier.width(4.dp))
                
                val annotatedText = buildAnnotatedString {
                    append("Acepto los ")
                    pushStringAnnotation(tag = "TERMS", annotation = "terms")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF2E2E8F),
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Términos y Condiciones")
                    }
                    pop()
                    append(" y la ")
                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF2E2E8F),
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Política de Privacidad")
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        var clickedLink = false
                        annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                            .firstOrNull()?.let {
                                clickedLink = true
                                activeDialogInfo = DialogInfo(
                                    title = "Términos y Condiciones",
                                    summary = "Al usar Eventify, aceptas nuestras reglas de uso de la plataforma. Esto incluye el compromiso de proporcionar información real al crear eventos, respetar las cotizaciones pactadas entre organizadores y proveedores, y el uso adecuado del chat y del catálogo de servicios. El incumplimiento de estas normas puede resultar en la suspensión de la cuenta.",
                                    url = "https://aplicaciones-moviles-equipo-4.github.io/eventify-landing-page-realtec/terminoscondiciones.html"
                                )
                            }
                        annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                            .firstOrNull()?.let {
                                clickedLink = true
                                activeDialogInfo = DialogInfo(
                                    title = "Política de Privacidad",
                                    summary = "Nos tomamos muy en serio la seguridad de tus datos. Recopilamos información básica de tu perfil (como nombre de usuario, correo y foto) y datos de tus eventos para garantizar el correcto funcionamiento de las cotizaciones y chat. Nunca compartiremos tu información personal con terceros sin tu consentimiento explícito.",
                                    url = "https://aplicaciones-moviles-equipo-4.github.io/eventify-landing-page-realtec/privacidad.html"
                                )
                            }
                        if (!clickedLink) {
                            termsAccepted = !termsAccepted
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register Button
            Button(
                onClick = {
                    localError = when {
                        !isEmailValid -> "Ingresa un correo válido"
                        password != confirmPassword -> "Las contraseñas no coinciden"
                        else -> null
                    }
                    if (localError == null) {
                        viewModel.signUp(email.trim(), password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F)),
                enabled = !viewModel.isLoading && username.isNotBlank() && password.isNotBlank() && termsAccepted
                enabled = !viewModel.isLoading && isEmailValid &&
                        password.isNotBlank() && confirmPassword.isNotBlank()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Registrarse", fontSize = 16.sp)
                }
            }
        }
    }
    }

    if (activeDialogInfo != null) {
        AlertDialog(
            onDismissRequest = { activeDialogInfo = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2E2E8F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = activeDialogInfo!!.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = activeDialogInfo!!.summary,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    TextButton(
                        onClick = {
                            try {
                                uriHandler.openUri(activeDialogInfo!!.url)
                            } catch (e: Exception) {
                                // Safe catch
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Leer documento completo en el navegador",
                            color = Color(0xFF2E2E8F),
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { activeDialogInfo = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8F))
                ) {
                    Text("Entendido")
                }
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    EventifyfrontendkotlinTheme {
        RegisterScreen(onRegisterClick = {}, onBackClick = {})
    }
}

private data class DialogInfo(
    val title: String,
    val summary: String,
    val url: String
)
