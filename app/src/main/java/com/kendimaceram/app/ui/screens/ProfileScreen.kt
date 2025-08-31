package com.kendimaceram.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.HomeViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 8.dp)
        ) {
            ProfileMenuItem(
                icon = Icons.Default.Person,
                text = "Hesap Bilgileri",
                onClick = { navController.navigate(Screen.AccountInfo.route) }
            )
            ProfileMenuItem(
                icon = Icons.Default.Notifications,
                text = "Bildirim Ayarları",
                onClick = { navController.navigate(Screen.NotificationSettings.route) }
            )
            ProfileMenuItem(
                icon = Icons.Default.Palette,
                text = "Tema",
                onClick = { navController.navigate(Screen.ThemeSettings.route) }
            )
            ProfileMenuItem(
                icon = Icons.Default.Favorite,
                text = "Favorilerim",
                onClick = { Toast.makeText(context, "Favorilerim tıklandı", Toast.LENGTH_SHORT).show() }
            )
            ProfileMenuItem(
                icon = Icons.Default.HelpOutline,
                text = "Yardım & Destek",
                onClick = { navController.navigate(Screen.Help.route) }
            )
            ProfileMenuItem(
                icon = Icons.Default.StarRate,
                text = "Uygulamayı Değerlendir",
                onClick = { Toast.makeText(context, "Uygulamayı Değerlendir tıklandı", Toast.LENGTH_SHORT).show() }
            )
            Spacer(modifier = Modifier.weight(1f))

            ProfileMenuItem(
                icon = Icons.Default.DeleteForever,
                text = "Hesabımı Sil",
                color = MaterialTheme.colorScheme.error,
                onClick = { Toast.makeText(context, "Hesabımı Sil tıklandı", Toast.LENGTH_SHORT).show() }
            )

            OutlinedButton(
                onClick = {
                    showLogoutDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text("Çıkış Yap", color = LocalContentColor.current)
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Çıkış Yap") },
            text = { Text("Çıkış yapmak istediğinizden emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                ) {
                    Text("Çıkış Yap")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}


@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    color: Color = LocalContentColor.current,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = color)
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = text, modifier = Modifier.weight(1f), color = color, fontSize = 18.sp)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = color.copy(alpha = 0.7f)
            )
        }
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}