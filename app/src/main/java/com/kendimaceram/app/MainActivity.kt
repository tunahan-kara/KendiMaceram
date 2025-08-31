package com.kendimaceram.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kendimaceram.app.data.SettingsRepository
import com.kendimaceram.app.data.ThemeSetting
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.ui.screens.*
import com.kendimaceram.app.ui.theme.KendiMaceramTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.kendimaceram.app.ui.screens.LoginScreen
import com.kendimaceram.app.ui.screens.SplashScreen
import com.kendimaceram.app.ui.theme.GPDarkGreen
import com.kendimaceram.app.ui.theme.GPDarkPurple

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val themeSetting by settingsRepository.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val useDarkTheme = when (themeSetting) {
                ThemeSetting.SYSTEM -> isSystemInDarkTheme()
                ThemeSetting.LIGHT -> false
                ThemeSetting.DARK -> true

            }

            KendiMaceramTheme(darkTheme = useDarkTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    GPDarkPurple,
                                    GPDarkGreen,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        // Surface'i şeffaf yapıyoruz ki arkasındaki gradient görünsün
                        color = Color.Transparent
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
                                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
                                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
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
                            composable(
                                route = Screen.Profile.route,
                                enterTransition = { fadeIn(animationSpec = tween(300)) },
                                exitTransition = { fadeOut(animationSpec = tween(300)) }
                            ) {
                                ProfileScreen(navController = navController)
                            }
                            composable(
                                route = Screen.AccountInfo.route,
                                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) }
                            ) {
                                AccountInfoScreen(navController = navController)
                            }
                            composable(
                                route = Screen.NotificationSettings.route,
                                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) }
                            ) {
                                NotificationSettingsScreen(navController = navController)
                            }
                            composable(
                                route = Screen.ThemeSettings.route,
                                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) }
                            ) {
                                ThemeSettingsScreen(navController = navController)
                            }
                            composable(
                                route = Screen.Help.route,
                                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) }
                            ) {
                                HelpScreen(navController = navController)
                            }
                            composable(
                                route = Screen.StoryDetail.route,
                                arguments = listOf(navArgument("storyId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
                                StoryDetailScreen(navController = navController, storyId = storyId)
                            }
                            composable(
                                route = Screen.StoryReader.route,
                                arguments = listOf(navArgument("storyId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
                                StoryReaderScreen(navController = navController, storyId = storyId)
                            }
                        }
                    }
                }
            }
        }
    }}