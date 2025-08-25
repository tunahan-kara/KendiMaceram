package com.kendimaceram.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// ViewModel'i doğrudan bu dosyada tanımlayalım, işimiz kolaylaşsın
@HiltViewModel
class ThemeSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val currentTheme = settingsRepository.themeSettingFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeSetting.SYSTEM)

    fun setTheme(theme: ThemeSetting) {
        viewModelScope.launch {
            settingsRepository.setThemeSetting(theme)
        }
    }
}

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
                .padding(16.dp)
        ) {
            // "Cihazın temasını kullan" anahtarı (Switch)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Cihazın Temasını Kullan", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (currentTheme == ThemeSetting.SYSTEM) "Açık" else "Kapalı",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = currentTheme == ThemeSetting.SYSTEM,
                    onCheckedChange = { isChecked ->
                        val newTheme = if (isChecked) ThemeSetting.SYSTEM else ThemeSetting.LIGHT
                        viewModel.setTheme(newTheme)
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // "Cihazın temasını kullan" kapalıysa, manuel seçim seçeneklerini göster
            AnimatedVisibility(visible = currentTheme != ThemeSetting.SYSTEM) {
                Column {
                    Text("Temayı Manuel Seç", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = currentTheme == ThemeSetting.LIGHT,
                            onClick = { viewModel.setTheme(ThemeSetting.LIGHT) }
                        )
                        Text("Aydınlık Mod")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = currentTheme == ThemeSetting.DARK,
                            onClick = { viewModel.setTheme(ThemeSetting.DARK) }
                        )
                        Text("Karanlık Mod")
                    }
                }
            }
        }
    }
}