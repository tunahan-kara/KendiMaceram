package com.kendimaceram.app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kendimaceram.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavController,
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(Screen.MyStories.route, Screen.NewStories.route, Screen.Premium.route, Screen.Profile.route)
    val shouldShowBottomBar = currentRoute in bottomNavRoutes

    val screenTitle = when (currentRoute) {
        Screen.MyStories.route -> "Kütüphane"
        Screen.NewStories.route -> "Keşfet"
        Screen.Premium.route -> "Premium Üyelik"
        Screen.Profile.route -> "Hesabım"
        Screen.AccountInfo.route -> "Hesap Bilgileri"
        Screen.NotificationSettings.route -> "Bildirim Ayarları"
        Screen.ThemeSettings.route -> "Tema Ayarları"
        Screen.Help.route -> "Yardım & Destek"
        Screen.StoryDetail.route -> "Hikaye Detayı"
        else -> ""
    }

    val showBackButton = navController.previousBackStackEntry != null && currentRoute != Screen.Splash.route

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            if (screenTitle.isNotEmpty()) {
                CenterAlignedTopAppBar(
                    title = { Text(text = screenTitle, fontWeight = FontWeight.Bold) },
                    // DEĞİŞİKLİK BURADA: Üst barın rengini tamamen şeffaf yapıyoruz.
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
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
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        },
        snackbarHost = snackbarHost
    ) { innerPadding ->
        content(innerPadding)
    }
}