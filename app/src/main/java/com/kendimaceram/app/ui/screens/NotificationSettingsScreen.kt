package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold

@Composable
fun NotificationSettingsScreen(navController: NavController) {
    var newStoryNotifications by remember { mutableStateOf(true) }
    var recommendationNotifications by remember { mutableStateOf(true) }
    var promotionalNotifications by remember { mutableStateOf(false) }

    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Text("Bildirim Ayarları", style = MaterialTheme.typography.headlineMedium) <-- Bu artık Toolbar'da
            Spacer(modifier = Modifier.height(16.dp))

            NotificationSettingItem(
                title = "Yeni Hikaye Bildirimleri",
                subtitle = "Uygulamaya yeni bir macera eklendiğinde haber ver.",
                isChecked = newStoryNotifications,
                onCheckedChange = { newStoryNotifications = it }
            )
            Divider()
            NotificationSettingItem(
                title = "Öneri Bildirimleri",
                subtitle = "Sevebileceğin hikayeler hakkında öneriler al.",
                isChecked = recommendationNotifications,
                onCheckedChange = { recommendationNotifications = it }
            )
            Divider()
            NotificationSettingItem(
                title = "Promosyon ve Haberler",
                subtitle = "İndirimler ve özel duyurular hakkında bilgi al.",
                isChecked = promotionalNotifications,
                onCheckedChange = { promotionalNotifications = it }
            )
        }
    }
}

@Composable
private fun NotificationSettingItem(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}