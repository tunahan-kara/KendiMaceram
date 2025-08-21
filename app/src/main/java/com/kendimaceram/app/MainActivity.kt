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
import com.kendimaceram.app.ui.screens.*
import com.kendimaceram.app.ui.theme.KendiMaceramTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KendiMaceramTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route // Yeni başlangıç noktası
                    ) {

                        composable(route = Screen.Splash.route) {
                            SplashScreen(navController = navController)
                        }
                        // Rotaların tanımlandığı yer
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
                            val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
                            StoryReaderScreen(navController = navController, storyId = storyId)
                        }
                        composable(route = Screen.Login.route) {
                            LoginScreen(navController = navController)
                        }
                        composable(route = Screen.Register.route) {
                            RegisterScreen(navController = navController)
                        }

                        composable(route = Screen.Register.route) {
                            RegisterScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}