package com.kendimaceram.app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kendimaceram.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    // Mevcut rotayı (adresi) dinliyoruz
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Rotaya göre başlığı ve geri butonunun görünüp görünmeyeceğini belirliyoruz
    val screenTitle = when (currentRoute) {
        Screen.Home.route -> "Ana Ekran"
        Screen.MyStories.route -> "Hikayelerim"
        Screen.NewStories.route -> "Yeni Hikayeler"
        Screen.Premium.route -> "Premium Üyelik"
        else -> "Kendi Maceram"
    }

    val showBackButton = navController.previousBackStackEntry != null && currentRoute != Screen.Home.route

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = screenTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // Eğer geri butonu gösterilecekse, IconButton'u oluştur
                    if (showBackButton) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri Butonu"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        // Bu, ekranın asıl içeriğinin geleceği yer.
        // innerPadding, içeriğin üst barın altına gizlenmemesini sağlar.
        content(innerPadding)
    }
}