package com.kendimaceram.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
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
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            KendiMaceramTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route
                    ) {
                        composable(route = Screen.Splash.route) {
                            SplashScreen(navController = navController)
                        }
                        composable(
                            route = Screen.Login.route,
                            enterTransition = { fadeIn(animationSpec = tween(700)) },
                            exitTransition = { fadeOut(animationSpec = tween(700)) }
                        ) {
                            LoginScreen(navController = navController)
                        }
                        composable(
                            route = Screen.Register.route,
                            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) },
                            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) }
                        ) {
                            RegisterScreen(navController = navController)
                        }
                        composable(
                            route = Screen.MyStories.route,
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            MyStoriesScreen(navController = navController)
                        }
                        composable(
                            route = Screen.NewStories.route,
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            NewStoriesScreen(navController = navController)
                        }
                        composable(
                            route = Screen.Premium.route,
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            PremiumScreen(navController = navController)
                        }
                        // YENİ EKRANIMIZI NAVHOST'A EKLİYORUZ
                        composable(
                            route = Screen.Profile.route,
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            ProfileScreen(navController = navController)
                        }
                        composable(
                            route = Screen.StoryReader.route,
                            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) },
                            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) }
                        ) { backStackEntry ->
                            val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
                            StoryReaderScreen(navController = navController, storyId = storyId)
                        }
                    }
                }
            }
        }
    }
}