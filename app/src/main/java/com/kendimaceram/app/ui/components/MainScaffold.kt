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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val bottomNavRoutes = listOf(
        Screen.MyStories.route,
        Screen.NewStories.route,
        Screen.Premium.route,
        Screen.Profile.route //
    )
    val shouldShowBottomBar = currentRoute in bottomNavRoutes


    val screenTitle = when (currentRoute) {
        Screen.MyStories.route -> "Hikayelerim"
        Screen.NewStories.route -> "Keşfet"
        Screen.Premium.route -> "Premium Üyelik"
        Screen.Profile.route -> "Hesabım" //
        Screen.Login.route -> "Giriş Yap"
        Screen.Register.route -> "Kayıt Ol"
        else -> ""
    }


    val showBackButton = navController.previousBackStackEntry != null && !shouldShowBottomBar

    Scaffold(
        topBar = {
            if (screenTitle.isNotEmpty()) {
                CenterAlignedTopAppBar(
                    title = { Text(text = screenTitle, fontWeight = FontWeight.Bold) },
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
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}