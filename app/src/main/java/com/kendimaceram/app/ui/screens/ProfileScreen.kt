package com.kendimaceram.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // ------- HESAP BÖLÜMÜ -------
            item { SectionHeader("Hesap") }
            item {
                SettingsRow(
                    icon = Icons.Default.Person,
                    label = "Hesap Bilgileri",
                    onClick = { navController.navigate(Screen.AccountInfo.route) }
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Notifications,
                    label = "Bildirim Ayarları",
                    onClick = { navController.navigate(Screen.NotificationSettings.route) }
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Palette,
                    label = "Tema",
                    onClick = { navController.navigate(Screen.ThemeSettings.route) }
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Favorite,
                    label = "Favorilerim",
                    onClick = { Toast.makeText(context, "Favorilerim tıklandı", Toast.LENGTH_SHORT).show() }
                )
            }

            // ------- UYGULAMA BÖLÜMÜ -------
            item { Spacer(Modifier.height(8.dp)) }
            item { SectionHeader("Uygulama") }
            item {
                SettingsRow(
                    icon = Icons.Default.HelpOutline,
                    label = "Yardım & Destek",
                    onClick = { navController.navigate(Screen.Help.route) }
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.StarRate,
                    label = "Uygulamayı Değerlendir",
                    onClick = { Toast.makeText(context, "Değerlendir tıklandı", Toast.LENGTH_SHORT).show() }
                )
            }

            // ------- TEHLİKE BÖLÜMÜ -------
            item { Spacer(Modifier.height(16.dp)) }
            item { SectionHeader("Hesap İşlemleri") }
            item {
                DangerRow(
                    icon = Icons.Default.DeleteForever,
                    label = "Hesabımı Sil",
                    onClick = {
                        Toast.makeText(context, "Hesabımı Sil tıklandı", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // ------- ÇIKIŞ -------
            item {
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Çıkış Yap")
                }
                Spacer(Modifier.height(8.dp))
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
                ) { Text("Çıkış Yap") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("İptal") }
            }
        )
    }
}

/* === Parçalar === */

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.surface
    val onBg = MaterialTheme.colorScheme.onSurface

    Surface(
        color = bg,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = onBg)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = onBg,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = onBg.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun DangerRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
        contentColor = MaterialTheme.colorScheme.error,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
