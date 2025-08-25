package com.kendimaceram.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore'u tek bir yerden yönetmek için
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Kullanıcının seçebileceği tema seçenekleri
enum class ThemeSetting {
    SYSTEM, LIGHT, DARK
}

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val themeKey = stringPreferencesKey("theme_setting")

    val themeSettingFlow: Flow<ThemeSetting> = context.dataStore.data
        .map { preferences ->
            ThemeSetting.valueOf(preferences[themeKey] ?: ThemeSetting.SYSTEM.name)
        }

    suspend fun setThemeSetting(theme: ThemeSetting) {
        context.dataStore.edit { settings ->
            settings[themeKey] = theme.name
        }
    }
}