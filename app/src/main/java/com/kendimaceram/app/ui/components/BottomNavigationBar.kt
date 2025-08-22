package com.kendimaceram.app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kendimaceram.app.ui.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Navigasyon listemizi tanımlıyoruz.
    val items = listOf(
        Screen.MyStories,
        Screen.NewStories,
        Screen.Premium,
        Screen.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Geri tuşuna basıldığında ilk ekrana kadar tüm yığını temizle.
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Aynı sekmeye tekrar tıklanınca yeni bir kopya oluşturma.
                        launchSingleTop = true
                        // Sekmeler arası geçişte durumu koru.
                        restoreState = true
                    }
                }
            )
        }
    }
}