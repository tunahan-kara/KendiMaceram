// MainActivity.kt (YENİ VE TAM HALİ)
package com.kendimaceram.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.ui.screens.HomeScreen
import com.kendimaceram.app.ui.screens.MyStoriesScreen
import com.kendimaceram.app.ui.screens.NewStoriesScreen
import com.kendimaceram.app.ui.screens.PremiumScreen
import com.kendimaceram.app.ui.screens.StoryReaderScreen
import com.kendimaceram.app.ui.theme.KendiMaceramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KendiMaceramTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigasyon kontrolcüsünü oluşturuyoruz.
                    val navController = rememberNavController()

                    // NavHost, hangi adreste hangi ekranın gösterileceğini belirleyen ana yapıdır.
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route // Uygulama bu ekrandan başlayacak
                    ) {
                        // Her bir ekran için bir rota tanımlıyoruz.
                        composable(route = Screen.Home.route) {
                            HomeScreen(navController = navController)
                        }
                        composable(route = Screen.MyStories.route) {
                            MyStoriesScreen(navController = navController)
                        }
                        composable(route = Screen.NewStories.route) {
                            NewStoriesScreen(navController = navController)
                        }
                        composable(route = Screen.Premium.route) {
                            PremiumScreen(navController = navController)
                        }
                        composable(route = Screen.StoryReader.route) { backStackEntry ->
                            // Hikaye okuma ekranına şimdilik boş bir veri yolluyoruz.
                            val storyId = backStackEntry.arguments?.getString("storyId") ?: "N/A"
                            StoryReaderScreen(navController = navController, storyId = storyId)
                        }
                    }
                }
            }
        }
    }
}