package com.kendimaceram.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.kendimaceram.app.data.SettingsRepository
import com.kendimaceram.app.data.ThemeSetting
import com.kendimaceram.app.ui.components.MainScaffold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/* ---------- ViewModel ---------- */

@HiltViewModel
class ThemeSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val currentTheme = settingsRepository.themeSettingFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeSetting.SYSTEM)

    fun setTheme(theme: ThemeSetting) {
        viewModelScope.launch { settingsRepository.setThemeSetting(theme) }
    }
}

/* ---------- Screen ---------- */

@Composable
fun ThemeSettingsScreen(
    navController: NavController,
    viewModel: ThemeSettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()

    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Tema Ayarları",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Uygulama görünümünü tercihine göre ayarla.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // Sistem teması kartı
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Cihazın Temasını Kullan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (currentTheme == ThemeSetting.SYSTEM) "Açık" else "Kapalı",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = currentTheme == ThemeSetting.SYSTEM,
                        onCheckedChange = { isChecked ->
                            viewModel.setTheme(if (isChecked) ThemeSetting.SYSTEM else ThemeSetting.LIGHT)
                        }
                    )
                }
            }

            // Manuel seçim
            AnimatedVisibility(visible = currentTheme != ThemeSetting.SYSTEM) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Temayı Manuel Seç",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ThemeOptionCard(
                            title = "Aydınlık Mod",
                            selected = currentTheme == ThemeSetting.LIGHT,
                            icon = Icons.Filled.LightMode,
                            isDarkPreview = false,
                            onClick = { viewModel.setTheme(ThemeSetting.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionCard(
                            title = "Karanlık Mod",
                            selected = currentTheme == ThemeSetting.DARK,
                            icon = Icons.Filled.DarkMode,
                            isDarkPreview = true,
                            onClick = { viewModel.setTheme(ThemeSetting.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/* ---------- Pieces ---------- */

@Composable
private fun ThemeOptionCard(
    title: String,
    selected: Boolean,
    icon: ImageVector,
    isDarkPreview: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    val border = if (selected)
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    else
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    val containerColor = if (selected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    else
        MaterialTheme.colorScheme.surface

    Card(
        onClick = onClick,
        shape = shape,
        border = border,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 3.dp else 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ThemePreview(isDark = isDarkPreview)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                RadioButton(
                    selected = selected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
private fun ThemePreview(isDark: Boolean) {
    // Mini arayüz önizlemesi: üst bar + iki kart + küçük CTA
    val bg = if (isDark) Color(0xFF121212) else Color(0xFFF2F2F2)
    val surface = if (isDark) Color(0xFF1E1E1E) else Color.White
    val accent = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(surface)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(surface)
            )
            Box(
                Modifier
                    .weight(1f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(surface)
            )
        }
        Box(
            Modifier
                .width(56.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(accent)
        )
    }
}
