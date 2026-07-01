package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.BrandIndigo
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme.BrandIndigoContainer

@Composable
fun AppHeader(onBellClick: () -> Unit = {}) {
    val unread = LocalStore.notifications.count { !it.read }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrandIndigoContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "E", color = BrandIndigo, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Eventify",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo
            )
        }
        Box(
            modifier = Modifier.clip(CircleShape).clickable { onBellClick() }.padding(6.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notificaciones",
                tint = BrandIndigo
            )
            if (unread > 0) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unread > 9) "9+" else unread.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Friendly empty-state placeholder used across list screens so the app never shows a blank void.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(BrandIndigoContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
