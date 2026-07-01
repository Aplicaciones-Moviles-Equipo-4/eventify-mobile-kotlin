package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.AppHeader
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components.EmptyState

private val Indigo = Color(0xFF2E2E8F)

@Composable
fun ChatListScreen(onOpenChat: (String) -> Unit) {
    val conversations = LocalStore.conversations()
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            AppHeader()
            Text("Mensajes", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            if (conversations.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = "Sin conversaciones",
                    message = "Cuando un anfitrión te escriba, verás aquí la conversación."
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(conversations) { c ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onOpenChat(c.contactName) }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(Color(0xFFE8EAF6), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(c.contactName.take(2).uppercase(), color = Indigo, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(c.contactName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(c.lastMessage, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
                            }
                            if (c.unread > 0) {
                                Box(
                                    modifier = Modifier.size(22.dp).background(Indigo, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${c.unread}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF5F5F5))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(contactName: String, onBackClick: () -> Unit) {
    var input by remember { mutableStateOf("") }
    // Read the observable message list so new messages (and the auto-reply) recompose the screen.
    val messages = LocalStore.messages.filter { it.contactName == contactName }.sortedBy { it.timestamp }

    LaunchedEffect(contactName) { LocalStore.markConversationRead(contactName) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(contactName, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { m ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (m.fromMe) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (m.fromMe) Indigo else Color(0xFFF0F0F5),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                m.text,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                color = if (m.fromMe) Color.White else Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") },
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )
                Spacer(Modifier.width(8.dp))
                val timeBase = messages.size
                IconButton(
                    onClick = {
                        val text = input.trim()
                        if (text.isNotEmpty()) {
                            // Monotonic timestamp derived from message count (Date.now not used to keep deterministic ordering).
                            LocalStore.sendMessage(contactName, text, System.currentTimeMillis() + timeBase)
                            input = ""
                        }
                    },
                    modifier = Modifier.size(48.dp).background(Indigo, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                }
            }
        }
    }
}
