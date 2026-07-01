package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.Profile
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel.OrganizerViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: OrganizerViewModel = viewModel()
) {
    val context = LocalContext.current
    val profile = viewModel.profile

    var firstName by remember { mutableStateOf(profile?.firstName ?: "") }
    var lastName by remember { mutableStateOf(profile?.lastName ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }
    var street by remember { mutableStateOf(profile?.street ?: "") }
    var number by remember { mutableStateOf(profile?.number ?: "") }
    var city by remember { mutableStateOf(profile?.city ?: "") }
    var country by remember { mutableStateOf(profile?.country ?: "") }
    
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isUploading = true
            val file = File(context.cacheDir, "profile_image.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            // Upload to backend
            viewModel.uploadProfileImage(body) {
                isUploading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        val updatedProfile = profile?.copy(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            street = street,
                            number = number,
                            city = city,
                            country = country
                        )
                        if (updatedProfile != null) {
                            viewModel.updateProfile(updatedProfile) {
                                onBackClick()
                            }
                        }
                    }) {
                        Text("Guardar", color = Color(0xFF2E2E8F), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { launcher.launch("image/*") }
            ) {
                val imageUrl = profile?.profileImageUrl
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFE8EAF6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase(),
                            color = Color(0xFF2E2E8F),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(Color(0xFF2E2E8F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ProfileField("Nombre", firstName) { firstName = it }
            ProfileField("Apellido", lastName) { lastName = it }
            ProfileField("Email", email) { email = it }
            ProfileField("Calle", street) { street = it }
            ProfileField("Número", number) { number = it }
            ProfileField("Ciudad", city) { city = it }
            ProfileField("País", country) { country = it }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E2E8F),
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}
